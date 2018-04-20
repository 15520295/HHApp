package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.text.TextUtils;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.User.RealtimeUser;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.User.UserApp;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerManager {
    private GoogleMap mMap;

    public Marker pickupPlaceMarker, endPlaceMarker, driverMarker;

    public MarkerManager(GoogleMap mMap, GoogleMap.OnMarkerClickListener listener) {
        this.mMap = mMap;
        mMap.setOnMarkerClickListener(listener);
    }


    // apply singleton pattern
    public void draw_DriverMarker(UserApp driverInfo, RealtimeUser driverRealTime) {
        if(driverMarker != null)
            driverMarker.remove();

        driverMarker = mMap.addMarker(new MarkerOptions().position(LocationUtils.strToLatLng(driverRealTime.getLocation()))
                .title(driverInfo.getName())
                .anchor(0.5f,0.5f)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_car)));
    }

    public void draw_PickupPlaceMarker(SavedPlace pickupPlace) {
        if(pickupPlaceMarker != null)
            pickupPlaceMarker.remove();

        pickupPlaceMarker = mMap.addMarker(new MarkerOptions().position(pickupPlace.getLatLng())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_map_pin_40px)));
    }

    public void draw_EndPlaceMarker(SavedPlace endPlace) {
        if(endPlaceMarker != null)
            endPlaceMarker.remove();

        endPlaceMarker = mMap.addMarker(new MarkerOptions().position(endPlace.getLatLng())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_marker_40px)));
    }

    public void draw_EndPlaceMarker(LatLng endLoation) {
        if(endPlaceMarker != null)
            endPlaceMarker.remove();

        endPlaceMarker = mMap.addMarker(new MarkerOptions().position(endLoation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_marker_40px)));
    }

}
