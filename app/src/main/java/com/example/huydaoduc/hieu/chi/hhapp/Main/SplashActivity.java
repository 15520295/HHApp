package com.example.huydaoduc.hieu.chi.hhapp.Main;

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
import android.widget.ImageView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.UpdateInfoActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ivSplash = findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim_acitivity_splash);
        ivSplash.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



        checkInternet();
    }
    private void checkInternet() {
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
                                            Intent startIntent = new Intent(SplashActivity.this, UpdateInfoActivity.class);
                                            SplashActivity.this.startActivity(startIntent);
                                            SplashActivity.this.finish();
                                        } else {
                                            // User is signed in
                                            CurUserInfo.getInstance().setUserInfo(userInfo);

                                            Intent startIntent = new Intent(SplashActivity.this, MainActivity.class);
                                            SplashActivity.this.startActivity(startIntent);
                                            SplashActivity.this.finish();
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
                                Intent startIntent = new Intent(SplashActivity.this, PhoneAuthActivity.class);
                                SplashActivity.this.startActivity(startIntent);
                                SplashActivity.this.finish();
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
                checkInternet();
            }
        });
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
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
