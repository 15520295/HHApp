package com.uit.huydaoduc.hieu.chi.hhapp.Model.User;

import android.location.Location;

import com.uit.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.google.android.gms.maps.model.LatLng;

public class OnlineUser {

    private String uid;
    private String location;              // current location
    private String lastTimeCheck;       // last time update Location
    private UserState state;
    private Float bearing;

    public OnlineUser() {

    }


    public LatLng func_getLocation() {
        return LocationUtils.strToLatLng(location);
    }

    public boolean func_isTimeOut(long checkAmountSec) {
        long secondsPass = TimeUtils.getPassTime(lastTimeCheck);

        if( secondsPass > checkAmountSec)
            return true;
        return false;
    }


    public OnlineUser(String uid, Location location, UserState state) {
        this.uid = uid;
        this.location = LocationUtils.locaToStr(location);
        this.bearing = location.getBearing();
        this.state = state;
        lastTimeCheck = TimeUtils.getCurrentTimeAsString();
    }

    public OnlineUser(String uid, String location, String lastTimeCheck, UserState state, Float bearing) {
        this.uid = uid;
        this.location = location;
        this.lastTimeCheck = lastTimeCheck;
        this.state = state;
        this.bearing = bearing;
    }

    // Getter Setter


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLastTimeCheck() {
        return lastTimeCheck;
    }

    public void setLastTimeCheck(String lastTimeCheck) {
        this.lastTimeCheck = lastTimeCheck;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Float getBearing() {
        return bearing;
    }

    public void setBearing(Float bearing) {
        this.bearing = bearing;
    }
}
