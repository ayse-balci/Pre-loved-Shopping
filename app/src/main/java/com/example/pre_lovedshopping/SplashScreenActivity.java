package com.example.pre_lovedshopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pre_lovedshopping.Activities.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

      //  imageView = (ImageView) findViewById(R.id.splashimg);
        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash_screen_transion);
        findViewById(R.id.starting_text).startAnimation(splashAnim);

        final Intent intent = new Intent(this, MainActivity.class);

        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}