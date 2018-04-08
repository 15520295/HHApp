package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.huydaoduc.hieu.chi.hhapp.MainActivity;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent startIntent = new Intent(SplashActivity.this,MainActivity.class);
                    SplashActivity.this.startActivity(startIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            // No user is signed in
            /* New Handler to start the Menu-Activity
            * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent startIntent = new Intent(SplashActivity.this,PhoneAuthActivity.class);
                    SplashActivity.this.startActivity(startIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }



    }

}
