package com.example.nexs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.nexs.models.Article;
import com.example.nexs.models.ArticleResponse;
import com.example.nexs.models.User;
import com.example.nexs.models.UserResponse;
import com.example.nexs.room.BookmarkedArticle;
import com.example.nexs.room.LikedArticle;
import com.example.nexs.room.viewmodel.LocalDataViewModel;
import com.example.nexs.utility.LoadingDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final int PHONE_SIGN_IN = 2;
    private Button googleSignIn, phoneSignIn;
    private Context context;
    private GoogleSignInClient mGoogleSignInClient;
    private LoadingDialog dialog;
    public static final String SHARED_PREF_BASIC_INFO = "com.example.nexs.BASIC_INFO";
    public static final String FIRST_NAME = "firstName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createRequest();
        setReferences();
        setListeners();
    }

    private void setReferences() {
        context = this;
        googleSignIn = findViewById(R.id.google);
        phoneSignIn = findViewById(R.id.phone);
        dialog = new LoadingDialog(context);
    }

    private void setListeners() {
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        phoneSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NameActivity.class);
                startActivityForResult(intent, PHONE_SIGN_IN);
            }
        });
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            dialog.showDialog();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                User user = new User();
                assert account != null;
                String name = account.getDisplayName();
                assert name != null;
                user.setFirstname(name.substring(0, name.indexOf(' ')).trim());
                if (name.indexOf(' ') != name.lastIndexOf(' ')) {
                    user.setMiddlename(name.substring(name.indexOf(' ') + 1, name.lastIndexOf(' ') + 1).trim());
                }
                user.setLastname(name.substring(name.lastIndexOf(' ') + 1).trim());
                user.setEmail(account.getEmail());
                user.setCoins(0);
                firebaseAuthWithGoogle(account.getIdToken(), user);
            } catch (ApiException e) {
                dialog.stopDialog();
                failureTask();
            }
        } else if (requestCode == PHONE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                boolean authSuccess = data.getBooleanExtra("success", false);
                if (authSuccess) {
                    dialog.showDialog();
                    importUserData();
                } else {
                    failureTask();
                }
            } else {
                failureTask();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, final User user) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.stopDialog();
                        if (task.isSuccessful()) {
                            user.setUid(FirebaseAuth.getInstance().getUid());
                            MainActivity.api.userNewUser(user).enqueue(new Callback<UserResponse>() {
                                @Override
                                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                    if (response.body().getCode() == 200) {
                                        dialog.showDialog();
                                        saveName(user.getFirstname());
                                        importUserData();
                                    } else {
                                        failureTask();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserResponse> call, Throwable t) {

                                }
                            });
                        } else {
                            failureTask();
                        }
                    }
                });
    }

    private void importUserData() {
        Intent intent = new Intent(context, MainActivity.class);
        MainActivity.api.userGetUserById(FirebaseAuth.getInstance().getUid()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        List<String> bookmarks = response.body().getUser().getBookmarks();
                        List<String> likes = response.body().getUser().getLikes();
                        LocalDataViewModel viewModel = new ViewModelProvider(LoginActivity.this).get(LocalDataViewModel.class);
                        for (String s : likes) {
                            viewModel.addLike(new LikedArticle(s));
                        }
                        for (int i = 0; i < bookmarks.size(); ++i) {
                            final int curr = i;
                            MainActivity.api.articleGetById(bookmarks.get(i)).enqueue(new Callback<ArticleResponse>() {
                                @Override
                                public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().getCode() == 200) {
                                            for (Article a : response.body().getArticles()) {
                                                BookmarkedArticle slide = new BookmarkedArticle();
                                                slide.setDescription(a.getDescription());
                                                slide.setTitle(a.getTitle());
                                                slide.setId(a.getId());
                                                slide.setImgUrl(a.getImgUrl());
                                                slide.setSourceUrl(a.getSourceUrl());
                                                slide.setLikes(a.getLikes());
                                                slide.setCategory(a.getCategory());
                                                slide.setCreatedAt(a.getCreatedAt());
                                                viewModel.addBookmark(slide);
                                            }
                                        } else {
                                            failureTask();
                                        }
                                    } else {
                                        failureTask();
                                    }
                                    if (curr == bookmarks.size() - 1) {
                                        dialog.stopDialog();
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ArticleResponse> call, Throwable t) {
                                    failureTask();
                                }
                            });
                        }
                        if (bookmarks.size() == 0) {
                            dialog.stopDialog();
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        failureTask();
                    }
                } else {
                    failureTask();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                failureTask();
            }
        });
    }

    private void saveName(String firstName) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_BASIC_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIRST_NAME, firstName);
        editor.apply();
    }

    public void failureTask() {
        dialog.stopDialog();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LocalDataViewModel viewModel = new ViewModelProvider(this).get(LocalDataViewModel.class);
            viewModel.deleteAllBookmarks();
            viewModel.deleteAllLikes();
            FirebaseAuth.getInstance().signOut();
        }
        Toast.makeText(context, "Something went Wrong!", Toast.LENGTH_LONG).show();
    }
}
