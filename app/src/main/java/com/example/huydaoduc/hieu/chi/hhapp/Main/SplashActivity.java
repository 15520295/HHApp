package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteManager.RouteRequestManagerActivity;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.example.huydaoduc.hieu.chi.hhapp.CheckInternetBroadcastReceiver;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkInternet();


    }


    private void checkInternet() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
                if (CheckInternetBroadcastReceiver.isNetworkAvailable(context, type)) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent startIntent = new Intent(SplashActivity.this, MainActivity.class);
                                SplashActivity.this.startActivity(startIntent);
                                SplashActivity.this.finish();
                            }
                        }, SPLASH_DISPLAY_LENGTH);
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
