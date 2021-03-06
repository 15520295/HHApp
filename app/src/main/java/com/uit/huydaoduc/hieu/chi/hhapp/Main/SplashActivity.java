package com.uit.huydaoduc.hieu.chi.hhapp.Main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.animation.Animation;

import com.uit.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.UpdateInfoActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.DefineString;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.uit.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private BroadcastReceiver broadcastReceiver;
    private ImageView ivSplash;

    private int animationRepeatCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        animationRepeatCount = 0;

        setContentView(R.layout.activity_splash);
        ivSplash = findViewById(R.id.imageView);
        Animation animation = new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                this.onAnimationStart(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animationRepeatCount++;

                if (animationRepeatCount == 4) {
                    Toast.makeText(getApplicationContext(),getString(R.string.slow_network),Toast.LENGTH_LONG).show();
                }
                if (startIntent != null) {
                    ivSplash.clearAnimation();
                    SplashActivity.this.startActivity(startIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    SplashActivity.this.finish();
                }
            }
        });
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setFillEnabled(true);
        ivSplash.startAnimation(animation);

        DefineString.setContext(getApplicationContext());


        checkInternetAndGetUserInfo();
    }

    Intent startIntent;

    private void checkInternetAndGetUserInfo() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // check if user input their info
                        FirebaseDatabase.getInstance().getReference()
                                .child(Define.DB_USERS_INFO)
                                .child(user.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                                        if (userInfo == null) {
                                            // signed in but don't have info
                                            startIntent = new Intent(SplashActivity.this, UpdateInfoActivity.class);

                                        } else {
                                            // User is signed in
                                            CurUserInfo.getInstance().setUserInfo(userInfo);
                                            startIntent = new Intent(SplashActivity.this, MainActivity.class);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    } else {
                        // No user is signed in
                        /* New Handler to start the Menu-Activity
                         * and close this Splash-Screen after some seconds.*/
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startIntent = new Intent(SplashActivity.this, PhoneAuthActivity.class);
                            }
                        }, SPLASH_DISPLAY_LENGTH);
                    }
                    return;
                } else {
                    showAlertDialog();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void showAlertDialog() {
        Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Your Phone isn't Connected");
        builder.setCancelable(false);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkInternetAndGetUserInfo();
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                SplashActivity.this.finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
