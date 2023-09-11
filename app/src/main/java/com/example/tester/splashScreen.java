package com.example.tester;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.window.SplashScreen;

public class splashScreen extends AppCompatActivity {

    private ImageView img;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        img = findViewById(R.id.imageView);
        progressBar =findViewById(R.id.progressBar);

        Handler handler = new Handler();
        progressBar.setVisibility(View.VISIBLE);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(splashScreen.this,MainActivity.class));
            }
        };

        handler.postDelayed(runnable,5000);
    }
}