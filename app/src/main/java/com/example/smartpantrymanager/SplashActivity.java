package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/** First screen shown when the app opens - branding + entry point into the app. */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button getStarted = findViewById(R.id.buttonGetStarted);
        getStarted.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish(); // don't keep Splash on the back stack
        });
    }
}