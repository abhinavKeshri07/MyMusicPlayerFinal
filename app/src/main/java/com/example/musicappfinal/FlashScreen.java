package com.example.musicappfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class FlashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
        class MyRunnable implements Runnable{
            @Override
            public void run() {
                startActivity(new Intent(FlashScreen.this, MainActivity.class));
                finish();
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(new MyRunnable(), 500);

    }

}

