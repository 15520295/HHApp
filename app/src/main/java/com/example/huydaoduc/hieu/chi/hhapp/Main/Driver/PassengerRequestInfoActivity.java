package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.Date;


public class PassengerRequestInfoActivity extends SimpleMapActivity implements SimpleMapActivity.SimpleMapListener {
    String tripUId;
    Trip trip;
    UserInfo userInfo = null;
    PassengerRequest passengerRequest = null;

    MaterialFancyButton btn_call, btn_messenger, btn_back;

    private DatabaseReference dbRefe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_request_info);

        Init();
        Event();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                trip = null;
                userInfo = null;
                passengerRequest = null;
            } else {
                trip = extras.getParcelable("trip");
                userInfo = extras.getParcelable("userInfo");
                passengerRequest = extras.getParcelable("passengerRequest");

            }
        } else {
            trip = (Trip) savedInstanceState.getParcelable("trip");
            userInfo = (UserInfo) savedInstanceState.getParcelable("userInfo");
            passengerRequest = (PassengerRequest) savedInstanceState.getParcelable("passengerRequest");
        }

        if (trip == null || userInfo == null || passengerRequest == null) {
            finish();
        } else {
            loadInfo(trip, userInfo, passengerRequest);
        }

        // setup map
        setupCheckRealtime = false;
        simpleMapListener = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PassengerRequestInfoActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        isFirstGetLocation = false;
    }

    TextView tv_passenger_name, tv_time_start, tv_estimate_fare, tv_start_address, tv_end_address, tv_note;


    private void Init() {
        tv_passenger_name = findViewById(R.id.tv_passenger_name);
        tv_time_start = findViewById(R.id.tv_time_start);
        tv_estimate_fare = findViewById(R.id.tv_estimate_fare);
        tv_start_address = findViewById(R.id.tv_pick_up_address);
        tv_end_address = findViewById(R.id.tv_drop_off_address);

        btn_call = findViewById(R.id.btn_call);
        btn_messenger = findViewById(R.id.btn_messenger);
        btn_back = findViewById(R.id.btn_back);

        // init database
        dbRefe = FirebaseDatabase.getInstance().getReference();
    }

    private void Event() {
        btn_call.setOnClickListener(v ->{
            // todo: btn_call
        });

        btn_messenger.setOnClickListener(v -> {
            // todo: btn_messenger
        });

        btn_back.setOnClickListener(v -> finish());
    }


    private void loadInfo(Trip trip, UserInfo userInfo, PassengerRequest passengerRequest) {

        tv_passenger_name.setText(userInfo.getName());

        Date passengerStartTime = passengerRequest.getTripFareInfo().func_getStartTimeAsDate();
        tv_time_start.setText(TimeUtils.dateToUserDateTimeStr(passengerStartTime));

        tv_estimate_fare.setText(passengerRequest.getTripFareInfo().func_getEstimateFareText());

        tv_start_address.setText(passengerRequest.getPickUpSavePlace().getPrimaryText());
        tv_end_address.setText(passengerRequest.getDropOffSavePlace().getPrimaryText());

        findViewById(R.id.tv_pick_up_address).setOnClickListener(v -> {
            if(v.isSelected())
                v.setSelected(false);
            else
                v.setSelected(true);
        });
        findViewById(R.id.tv_drop_off_address).setOnClickListener(v -> {
            if(v.isSelected())
                v.setSelected(false);
            else
                v.setSelected(true);
        });


        if (TextUtils.isEmpty(passengerRequest.getNote())) {
            findViewById(R.id.group_note).setVisibility(View.GONE);
        } else {
            tv_note = findViewById(R.id.tv_note);
            tv_note.setText(passengerRequest.getNote());
        }
    }

    @Override
    public void OnRealTimeLocationUpdate() {

    }

    @Override
    public void OnMapSetupDone() {
        mMap.getUiSettings().setAllGesturesEnabled(false);

        if (trip != null) {
            SavedPlace pickUpSavePlace = passengerRequest.getPickUpSavePlace();
            SavedPlace dropOffSavePlace = passengerRequest.getDropOffSavePlace();

            markerManager.draw_PickupPlaceMarker(pickUpSavePlace);
            markerManager.draw_DropPlaceMarker(dropOffSavePlace);

            if (passengerRequest.getPickUpSavePlace() != null && passengerRequest.getDropOffSavePlace() != null) {
                cameraManager.moveCam(pickUpSavePlace.func_getLatLngLocation(), dropOffSavePlace.func_getLatLngLocation());
            }
        }
    }
}
