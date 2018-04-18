package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;

public class RouteRequest {

    private String startLocation;
    private String endLocation;
    private String summary;
    private String uid;

    public RouteRequest() {

    }

    public RouteRequest(String startAddress, String endAddress, String summary, String uid) {
        this.startLocation = startAddress;
        this.endLocation = endAddress;
        this.summary = summary;
        this.uid = uid;
    }

    public static RouteRequest getRouteRequestFromLeg(Route route, String uid) {
        Leg leg = route.getLegs().get(0);

        String startLoc = LocationUtils.latLngToString(leg.getStartLocation());
        String endLoc = LocationUtils.latLngToString(leg.getEndLocation());
        return new RouteRequest(startLoc, endLoc , route.getSummary(), uid);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }
}
