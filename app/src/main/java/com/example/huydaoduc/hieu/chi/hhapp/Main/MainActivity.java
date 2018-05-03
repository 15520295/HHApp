package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.DriverActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteManager.RouteRequestManagerActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button btn_rider, btn_driver;


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


        btn_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void initView() {
        btn_rider = findViewById(R.id.btn_rider);
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


