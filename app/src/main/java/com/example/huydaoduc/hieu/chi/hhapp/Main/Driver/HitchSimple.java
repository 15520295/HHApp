package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver;

import android.os.Bundle;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

public class HitchSimple extends SimpleMapActivity implements SimpleMapActivity.SimpleMapListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitch);

        simpleMapListener = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(HitchSimple.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // onMapReadyCallback
    }


    @Override
    public void OnRealTimeLocationUpdate() {

    }
}
