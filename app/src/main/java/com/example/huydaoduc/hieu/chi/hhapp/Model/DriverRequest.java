package com.example.huydaoduc.hieu.chi.hhapp.Model;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.TimeUtils;

public class DriverRequest {

    private String driverUId;
    private String startLocation;       // LatLng
    private String endLocation;
    private String summary;
    private String postTime;
    private Float percentDiscount;

    public DriverRequest() {

    }

    public float func_calcEstimateFare(float length) {
        return percentDiscount * length * Define.VND_PER_M;
    }

    public boolean func_isTimeOut(long checkAmountSec) {
        long secondsPass = TimeUtils.getPassTime(postTime);

        if( secondsPass > checkAmountSec)
            return true;
        return false;
    }

    public static DriverRequest func_createDriverRequestFromRoute(Route route, String uid, Float percentDiscount) {
        Leg leg = route.getLegs().get(0);

        String startLoc = LocationUtils.latLngToStr(leg.getStartLocation());
        String endLoc = LocationUtils.latLngToStr(leg.getEndLocation());

        String postTime = TimeUtils.getCurrentTimeAsString();

        return new DriverRequest(uid, startLoc, endLoc , route.getSummary(), postTime, percentDiscount);
    }


    public DriverRequest(String driverUId, String startLocation, String endLocation, String summary, String postTime, Float percentDiscount) {
        this.driverUId = driverUId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.summary = summary;
        this.postTime = postTime;
        this.percentDiscount = percentDiscount;
    }

    public String getDriverUId() {
        return driverUId;
    }

    public void setDriverUId(String driverUId) {
        this.driverUId = driverUId;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public Float getPercentDiscount() {
        return percentDiscount;
    }

    public void setPercentDiscount(Float percentDiscount) {
        this.percentDiscount = percentDiscount;
    }


}
