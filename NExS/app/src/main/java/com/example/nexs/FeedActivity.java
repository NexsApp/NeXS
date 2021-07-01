package com.example.nexs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nexs.adapters.ViewPagerAdapter;
import com.example.nexs.models.Article;
import com.example.nexs.models.ArticleResponse;
import com.example.nexs.models.UserResponse;
import com.example.nexs.room.BookmarkedArticle;
import com.example.nexs.room.LikedArticle;
import com.example.nexs.room.viewmodel.LocalDataViewModel;
import com.example.nexs.utility.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager2;
    private final List<BookmarkedArticle> slideList = new ArrayList<>();
    private LocalDataViewModel viewModel;
    private boolean showBookmarks;
    private AlertDialog dialog;
    private ExecutorService executorService;
    private int seconds = 0;
    private final Set<Integer> set = new HashSet<>();
//    private final int LOW_NUMBER = 24;
    private final int HIGH_NUMBER = 29;
//    private final int LOW_TIME = 240;
    private final int HIGH_TIME = 300;
    private final int COINS_TO_ADD = 1;
    private int setNumber = 1;
    private final ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
//            Log.i("slide", position +" "+seconds);
            if (position != 0 && position % 30 == 0) {
                ++setNumber;
                return;
            }
            if (position >= (HIGH_NUMBER * setNumber - 5) && position <= HIGH_NUMBER * setNumber && !set.contains((position - 1)/HIGH_NUMBER)) {
                if (seconds >= (HIGH_TIME * setNumber - 60) && seconds <= HIGH_TIME * setNumber) {
//                    Log.i("slide", "coins");
                    Call<UserResponse> call = MainActivity.api.userAddCoin(MainActivity.token, COINS_TO_ADD);
                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (response.body().getCode() == 200) {
                                    dialog.show();
//                                    Log.i("slide", "coins added");
                                } else {
                                    set.remove((position - 1)/HIGH_NUMBER);
                                }
                            } else {
                                set.remove((position - 1) / HIGH_NUMBER);
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            set.remove((position - 1)/HIGH_NUMBER);
                        }
                    });
                    set.add((position - 1)/HIGH_NUMBER);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        viewPagerAdapter = new ViewPagerAdapter(slideList, this);
        viewPager2 = findViewById(R.id.viewPagerSlider);
        viewPager2.setPageTransformer(new DepthPageTransformer());
        viewPager2.setAdapter(viewPagerAdapter);
        setBackgroundWork();
        showBookmarks = shouldShowBookmarkOnly();
        boolean showById = shouldShowById();
        setViewModel();
        if (showById) {
            fetchAndShow();
        } else if (!showBookmarks) {
            createSlides();
        }
        createDialog();
    }

    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.view_coin_earned, findViewById(R.id.root), false);
        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setBackgroundWork() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please sign-in to get rewards", Toast.LENGTH_LONG).show();
            return;
        }
        viewPager2.registerOnPageChangeCallback(callback);
        executorService = Executors.newSingleThreadExecutor();
        //Starting Timer
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SystemClock.sleep(1000);
                    ++seconds;
                }
            }
        });
    }

    private void fetchAndShow() {
        LoadingDialog dialog = new LoadingDialog(this);
        dialog.showDialog();
        Call<ArticleResponse> call = MainActivity.api.articleGetById(getIntent().getStringExtra("articleId"));
        call.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                dialog.stopDialog();
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        viewPagerAdapter.lastTime = response.body().getArticles().get(response.body().getArticles().size() - 1).getCreatedAt();
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
                            slideList.add(slide);
                            viewPagerAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(FeedActivity.this, "Oops! Something went Wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FeedActivity.this, "Oops! Something went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                dialog.stopDialog();
                Toast.makeText(FeedActivity.this, "Oop! Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean shouldShowById() {
        Intent intent = getIntent();
        return intent.getBooleanExtra("showById", false);
    }

    private boolean shouldShowBookmarkOnly() {
        Intent intent = getIntent();
        return intent.getBooleanExtra("showBookmarks", false);
    }

    private void setViewModel() {
        viewModel = new ViewModelProvider(this).get(LocalDataViewModel.class);
        viewModel.getBookmarkedIds().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Set<String> set = new HashSet<>(strings);
                viewPagerAdapter.setBookmarkedIds(set);
                viewPagerAdapter.notifyItemChanged(viewPager2.getCurrentItem());
            }
        });
        viewModel.getLikes().observe(this, new Observer<List<LikedArticle>>() {
            @Override
            public void onChanged(List<LikedArticle> likedArticles) {
                Set<LikedArticle> set = new HashSet<>(likedArticles);
                viewPagerAdapter.setLikedIds(set);
                viewPagerAdapter.notifyItemChanged(viewPager2.getCurrentItem());
            }
        });
        if (showBookmarks) {
            viewModel.getBookmarks().observe(this, new Observer<List<BookmarkedArticle>>() {
                @Override
                public void onChanged(List<BookmarkedArticle> bookmarkedArticles) {
                    slideList.addAll(bookmarkedArticles);
                    viewPagerAdapter.notifyDataSetChanged();
                    viewModel.getBookmarks().removeObserver(this::onChanged);
                    for (int i = 0; i < bookmarkedArticles.size(); ++i) {
                        int curr = i;
                        MainActivity.api.articleGetById(bookmarkedArticles.get(i).getId()).enqueue(new Callback<ArticleResponse>() {
                            @Override
                            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body().getCode() == 200) {
                                        Article a = response.body().getArticles().get(0);
                                        if (!bookmarkedArticles.get(curr).getLikes().equals(a.getLikes())) {
                                            bookmarkedArticles.get(curr).setLikes(a.getLikes());
                                            viewPagerAdapter.notifyItemChanged(curr);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ArticleResponse> call, Throwable t) {

                            }
                        });
                    }
                }
            });
        }
    }

    private void createSlides() {
        for (Article a : MainActivity.articles) {
            BookmarkedArticle slide = new BookmarkedArticle();
            slide.setDescription(a.getDescription());
            slide.setTitle(a.getTitle());
            slide.setId(a.getId());
            slide.setImgUrl(a.getImgUrl());
            slide.setSourceUrl(a.getSourceUrl());
            slide.setLikes(a.getLikes());
            slide.setCategory(a.getCategory());
            slide.setCreatedAt(a.getCreatedAt());
            slideList.add(slide);
        }
    }

    private String getDate(Long createdAt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createdAt);
        return simpleDateFormat.format(calendar.getTime());
    }

    @RequiresApi(21)
    public static class DepthPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setTranslationZ(0f);
                view.setTranslationY(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationY(pageWidth * -position);
                // Move it behind the left page
                view.setTranslationZ(-1f);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

}