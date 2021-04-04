package com.example.nexs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nexs.utility.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {

    private Context context;
    private EditText phone, otp;
    private Button send;
    private LoadingDialog dialog;
    private String verID;
    private String firstName, middleName, lastName;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            dialog.stopDialog();
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(authResultOnCompleteListener);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            dialog.stopDialog();
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            dialog.stopDialog();
            phone.setEnabled(false);
            send.setVisibility(View.INVISIBLE);
            send.setEnabled(false);
            otp.setVisibility(View.VISIBLE);
            otp.requestFocus();
            verID = s;
        }
    };

    OnCompleteListener<AuthResult> authResultOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            dialog.stopDialog();
            if (task.isSuccessful()) {
                Intent intent = new Intent(context, MainActivity.class);
                //SEND DETAILS TO SERVER THEN GOTO MAINACTIVITY
                startActivity(intent);
                PhoneAuth.this.finish();
            } else {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        setReferences();
        getIntentData();
        setListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        firstName = intent.getStringExtra(NameActivity.FIRST_NAME);
        middleName = intent.getStringExtra(NameActivity.MIDDLE_NAME);
        lastName = intent.getStringExtra(NameActivity.LAST_NAME);
    }

    private void setReferences() {
        context = this;
        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        send = findViewById(R.id.send);
        dialog = new LoadingDialog(context);
    }

    private void setListeners() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText() == null)
                    return;
                if (phone.getText().toString().trim().equals(""))
                    return;
                dialog.showDialog();
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                        .setPhoneNumber(phone.getText().toString())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(PhoneAuth.this)
                        .setCallbacks(phoneAuthCallback)
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
                hideKeyboard();
            }
        });

        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (otp.getText().toString().length() == 6) {
                    hideKeyboard();
                    dialog.showDialog();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verID, otp.getText().toString().trim());
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(authResultOnCompleteListener);
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(otp.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}