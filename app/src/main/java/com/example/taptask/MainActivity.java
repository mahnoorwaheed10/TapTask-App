package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
        }, SPLASH_DURATION_MS);
    }
}