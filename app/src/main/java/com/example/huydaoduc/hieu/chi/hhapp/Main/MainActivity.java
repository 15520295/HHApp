package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager.RouteRequestManagerActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerActivity;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FloatingTextButton btn_passenger, btn_driver;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        addEven();

        checkPermissions();

    }

    private void addEven() {

        btn_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RouteRequestManagerActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });


        btn_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_location_on)
                                .setContentTitle("Test notification")
                                .setContentText("Hi, This is Android Notification Detail!");

                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                /*resultIntent.putExtra(DBSQL.WORD_ID_KEY_PUT_EXTRA, dsWords.get(a).getId());
                resultIntent.putExtra(DBSQL.WORD_TEN_KEY_PUT_EXTRA, dsWords.get(a).getTen());
                resultIntent.putExtra(DBSQL.WORD_MOTA_KEY_PUT_EXTRA, dsWords.get(a).getMota());*/

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(uri);

//                        Uri newSound= Uri.parse("android.resource://"
//                                + getPackageName() + "/" + R.raw.gaugau);
//                        mBuilder.setSound(newSound);

                int mNotificationId = 155;
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());

                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void initView() {
        btn_passenger = findViewById(R.id.btn_passenger);
        btn_driver = findViewById(R.id.btn_driver);
    }

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;


    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            checkPlayServices();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPlayServices();
                }
                else
                {
                    Log.e(TAG, "Permissions denied");
                    //todo: handle this
                }
        }

    }

    private void checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode))
                GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, resultCode, PLAY_SERVICE_RES_REQUEST).show();
            else {
                Log.e(TAG, "This device is not supported");
            }
            // todo : handle this "This device is not supported
        }
    }

}


