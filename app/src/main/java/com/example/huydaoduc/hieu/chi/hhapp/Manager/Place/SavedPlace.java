package com.example.huydaoduc.hieu.chi.hhapp.Manager.Place;

import com.google.android.gms.maps.model.LatLng;

public class SavedPlace {
    private String name;
    private String address;
    private String id;
    private LatLng latLng;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
