package com.example.huydaoduc.hieu.chi.hhapp.Manager.Place;

import android.util.Log;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.google.android.gms.maps.model.LatLng;

public class SavedPlace {
    private String primaryText;
    private String address;
    private String id;
    private String location;        // LatLng

    public SavedPlace() { }

    public LatLng func_getLatLngLocation() {
        if (location == null) {
            Log.e("SavePlace", "location null");
            return new LatLng(0f, 0f);
        }
        return LocationUtils.strToLatLng(location);
    }

    // getter & setter

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
