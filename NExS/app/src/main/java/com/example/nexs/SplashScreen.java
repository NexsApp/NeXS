package com.example.nexs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    public static final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler(getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                SplashScreen.this.finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
