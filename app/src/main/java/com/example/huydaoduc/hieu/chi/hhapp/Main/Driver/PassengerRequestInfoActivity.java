package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver;

import android.os.Bundle;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


public class PassengerRequestInfoActivity extends SimpleMapActivity implements SimpleMapActivity.SimpleMapListener {

    String tripUId;
    Trip trip;

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
                tripUId = null;
            } else {
                tripUId = extras.getString("tripUId");
            }
        } else {
            tripUId = (String) savedInstanceState.getSerializable("tripUId");
        }

        if (tripUId == null) {
            finish();
        } else {

        }

        dbRefe.child(Define.DB_TRIPS).child(tripUId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trip = dataSnapshot.getValue(Trip.class);
                loadTripInfo(trip);
                onResponseReceived();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // setup map
        setupCheckRealtime = false;
        simpleMapListener = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PassengerRequestInfoActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        isFirstGetLocation = false;


    }

    TextView tv_passenger_name, tv_time_start, tv_start_address, tv_end_address;


    private void Init() {
        tv_passenger_name = findViewById(R.id.tv_passenger_name);
        tv_time_start = findViewById(R.id.tv_time_start);
        tv_start_address = findViewById(R.id.tv_pick_up_address);
        tv_end_address = findViewById(R.id.tv_drop_off_address);


        // init database
        dbRefe = FirebaseDatabase.getInstance().getReference();
    }

    private void Event() {

    }


    private void loadTripInfo(Trip trip) {
        PassengerRequest passengerRequest = trip.getPassengerRequest();

        DBManager.getUserById(trip.getPassengerUId(), userInfo -> {
            tv_passenger_name.setText(userInfo.getName());
        });

        Date passengerStartTime = trip.getTripFareInfo().func_getStartTimeAsDate();
        tv_time_start.setText(TimeUtils.dateToUserDateTimeStr(passengerStartTime));

        tv_start_address.setText(passengerRequest.getPickUpSavePlace().getPrimaryText());
        tv_end_address.setText(passengerRequest.getDropOffSavePlace().getPrimaryText());
    }

    @Override
    public void OnRealTimeLocationUpdate() {

    }

    @Override
    public void OnMapSetupDone() {
        onResponseReceived();

    }

    AtomicInteger receivedCount= new AtomicInteger();
    public void onResponseReceived() {
        if (receivedCount.incrementAndGet() == 2){
            if (trip != null) {
                SavedPlace pickUpSavePlace = trip.getPassengerRequest().getPickUpSavePlace();
                SavedPlace dropOffSavePlace = trip.getPassengerRequest().getDropOffSavePlace();

                markerManager.draw_PickupPlaceMarker(pickUpSavePlace);
                markerManager.draw_DropPlaceMarker(dropOffSavePlace);

                if (trip.getPassengerRequest().getPickUpSavePlace() != null && trip.getPassengerRequest().getDropOffSavePlace() != null) {
                    cameraManager.moveCam(pickUpSavePlace.func_getLatLngLocation(), dropOffSavePlace.func_getLatLngLocation());
                }
            }
        }
    }
}
