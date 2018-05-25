package com.uit.huydaoduc.hieu.chi.hhapp.Main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager.CreateRouteActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager.RouteRequestManagerActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ROUTE_REQUEST_MANAGER_REQUEST_CODE = 1;
    private static final int PASSENGER_ACTIVITY_MANAGER_REQUEST_CODE = 2;
    FloatingTextButton btn_passenger, btn_driver;
    private TextView txtdriver, txtpassenger;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim_driver);
        Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.anim_pass);
        txtdriver.startAnimation(animation);
        txtpassenger.startAnimation(animation1);

        addEven();

        checkPermissions();

        TextView tv_estimate_fare_note = findViewById(R.id.tv_estimate_fare_note);
        tv_estimate_fare_note.setSelected(true);

        firstLoadInfo();
    }

    private void firstLoadInfo() {

        CurUserInfo.GetUserInfoListener listener = new CurUserInfo.GetUserInfoListener(){
            @Override
            public void OnGetDone(UserInfo userInfo) {
                hideLoading();
            }
        };
        if (CurUserInfo.getInstance().getUserInfo(listener) == null) {
            showLoading(getString(R.string.get_your_info));
        }
    }



    private void addEven() {
        btn_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateRouteActivity.class);
                MainActivity.this.startActivityForResult(intent, ROUTE_REQUEST_MANAGER_REQUEST_CODE);
            }
        });

        btn_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                MainActivity.this.startActivityForResult(intent, PASSENGER_ACTIVITY_MANAGER_REQUEST_CODE);
            }
        });
    }

    private void initView() {
        btn_passenger = findViewById(R.id.btn_passenger);
        btn_driver = findViewById(R.id.btn_driver);
        txtdriver = findViewById(R.id.txtdriver);
        txtpassenger = findViewById(R.id.txtpassenger);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                Toast.makeText(getApplicationContext(), "Sorry, this device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }

    MaterialDialog loadingPassengerInfo;

    private void showLoading(String title) {
        loadingPassengerInfo = new MaterialDialog.Builder(this)
                .title(title)
                .content("Please wait...")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                .widgetColorRes(R.color.title_bar_background_color_blue)
                .buttonRippleColorRes(R.color.title_bar_background_color_blue).show();
    }

    private void hideLoading() {
        if (loadingPassengerInfo != null)
            loadingPassengerInfo.dismiss();
    }
}


