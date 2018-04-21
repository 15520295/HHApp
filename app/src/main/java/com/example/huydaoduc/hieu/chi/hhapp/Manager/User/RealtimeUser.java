package com.example.huydaoduc.hieu.chi.hhapp.Manager.User;

import android.location.Location;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.TimeManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.TimeUnit;

public class RealtimeUser {

    private String uid;
    private String location;              // current location
    private String lastTimeCheck;       // last time update Location
    private UserState state;
    private Float bearing;

    public RealtimeUser() {

    }

    public RealtimeUser(String uid, String location, String lastTimeCheck, UserState state, Float bearing) {
        this.uid = uid;
        this.location = location;
        this.lastTimeCheck = lastTimeCheck;
        this.state = state;
        this.bearing = bearing;
    }

    public RealtimeUser(String uid, Location location, UserState state) {
        this.uid = uid;
        this.location = LocationUtils.locaToStr(location);
        this.state = state;
        lastTimeCheck = TimeManager.getCurrentTimeAsString();
    }

    public LatLng func_getLatLngLocation() {
        return LocationUtils.strToLatLng(location);
    }

    public boolean func_isTimeOut(long timeOutSec) {
        long difference = TimeManager.getCurrentTimeAsDate().getTime() - TimeManager.strToDate(lastTimeCheck).getTime();
        long secondsPass = TimeUnit.MILLISECONDS.toSeconds(difference);
        if( secondsPass > timeOutSec)
            return true;
        return false;
    }

    // Getter Setter


    public Float getBearing() {
        return bearing;
    }

    public void setBearing(Float bearing) {
        this.bearing = bearing;
    }

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
}
