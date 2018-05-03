package com.example.huydaoduc.hieu.chi.hhapp.Framework;

import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.OnlineUser;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerManager {
    private GoogleMap mMap;

    public Marker pickupPlaceMarker, dropPlaceMarker, driverMarker;

    public MarkerManager(GoogleMap mMap, GoogleMap.OnMarkerClickListener listener) {
        this.mMap = mMap;
        mMap.setOnMarkerClickListener(listener);
    }


    // apply singleton pattern
    public void draw_DriverMarker(UserInfo driverInfo, OnlineUser onlineDriver) {
        if(driverMarker != null)
            driverMarker.remove();

        //todo: Huy doi` hien them thong tin duong di
        driverMarker = mMap.addMarker(new MarkerOptions().position(LocationUtils.strToLatLng(onlineDriver.getLocation()))
                .title("Driver:" + driverInfo.getName())
                .anchor(0.5f,0.5f)
                .rotation(onlineDriver.getBearing())
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_car)));
    }

    public void draw_PickupPlaceMarker(SavedPlace pickupPlace) {
        if(pickupPlaceMarker != null)
            pickupPlaceMarker.remove();

        pickupPlaceMarker = mMap.addMarker(new MarkerOptions().position(pickupPlace.func_getLatLngLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_pick_up_place_40px)));
    }

    public void draw_DropPlaceMarker(SavedPlace endPlace) {
        if(dropPlaceMarker != null)
            dropPlaceMarker.remove();

        dropPlaceMarker = mMap.addMarker(new MarkerOptions().position(endPlace.func_getLatLngLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_marker_40px)));
    }

    public void draw_DropPlaceMarker(LatLng endLoation) {
        if(dropPlaceMarker != null)
            dropPlaceMarker.remove();

        dropPlaceMarker = mMap.addMarker(new MarkerOptions().position(endLoation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_marker_40px)));
    }

}
