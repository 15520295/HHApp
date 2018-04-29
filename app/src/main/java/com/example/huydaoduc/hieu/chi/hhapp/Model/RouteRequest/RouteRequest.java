package com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.NotifyTrip;

import java.util.Date;

public class RouteRequest {

    private String routeRequestUId;
    private String driverUId;

    private SavedPlace startPlace;       // LatLng
    private SavedPlace endPlace;
    private String summary;

    private String startTime;

    private Float percentDiscount;

    private RouteRequestState routeRequestState;
    private NotifyTrip notifyTrip;

    public RouteRequest() {

    }

    public float func_calcEstimateFare(float length) {
        return percentDiscount * length * Define.VND_PER_M;
    }

    public boolean func_isInThePass() {
        if(TimeUtils.compareWithNow(startTime) >= 0)
            return true;
        return false;
    }

//    public static RouteRequest func_createDriverRequestFromRoute(String routeRequestUId, String driverUId, SavedPlace startPlace, SavedPlace endPlace, Date startTime, Float percentDiscount) {
//        Leg leg = route.getLegs().get(0);
//
//        String startLoc = LocationUtils.latLngToStr(leg.getStartLocation());
//        String endLoc = LocationUtils.latLngToStr(leg.getEndLocation());
//        String startTimeStr = TimeUtils.dateToStr(startTime);
//
//        return new RouteRequest(routeRequestUId, driverUId, startLoc, endLoc , route.getSummary(), startTimeStr, percentDiscount,routeState, null);
//    }

    public static final class Builder {
        private String routeRequestUId;
        private String driverUId;
        private SavedPlace startPlace;       // LatLng
        private SavedPlace endPlace;
        private String summary;
        private String startTime;
        private Float percentDiscount;
        private RouteRequestState routeRequestState;
        private NotifyTrip notifyTrip;

        private Builder() {
        }

        public static Builder aRouteRequest(String routeRequestUId) {
            return new Builder()
                    .setRouteRequestUId(routeRequestUId)
                    .setRouteRequestState(RouteRequestState.FINDING_PASSENGER);
        }

        private Builder setRouteRequestUId(String routeRequestUId) {
            this.routeRequestUId = routeRequestUId;
            return this;
        }

        public Builder setDriverUId(String driverUId) {
            this.driverUId = driverUId;
            return this;
        }

        public Builder setStartPlace(SavedPlace startPlace) {
            this.startPlace = startPlace;
            return this;
        }

        public Builder setEndPlace(SavedPlace endPlace) {
            this.endPlace = endPlace;
            return this;
        }

        public Builder setSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder setStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setPercentDiscount(Float percentDiscount) {
            this.percentDiscount = percentDiscount;
            return this;
        }

        public Builder setRouteRequestState(RouteRequestState routeRequestState) {
            this.routeRequestState = routeRequestState;
            return this;
        }

        public Builder setNotifyTrip(NotifyTrip notifyTrip) {
            this.notifyTrip = notifyTrip;
            return this;
        }

        public RouteRequest build() {
            RouteRequest routeRequest = new RouteRequest();
            routeRequest.setRouteRequestUId(routeRequestUId);
            routeRequest.setDriverUId(driverUId);
            routeRequest.setStartPlace(startPlace);
            routeRequest.setEndPlace(endPlace);
            routeRequest.setSummary(summary);
            routeRequest.setStartTime(startTime);
            routeRequest.setPercentDiscount(percentDiscount);
            routeRequest.setRouteRequestState(routeRequestState);
            routeRequest.setNotifyTrip(notifyTrip);
            return routeRequest;
        }
    }


    public RouteRequest(String routeRequestUId, String driverUId, SavedPlace startPlace, SavedPlace endPlace, String summary, String startTime, Float percentDiscount, RouteRequestState routeRequestState, NotifyTrip notifyTrip) {
        this.routeRequestUId = routeRequestUId;
        this.driverUId = driverUId;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.summary = summary;
        this.startTime = startTime;
        this.percentDiscount = percentDiscount;
        this.routeRequestState = routeRequestState;
        this.notifyTrip = notifyTrip;
    }

    public String getRouteRequestUId() {
        return routeRequestUId;
    }

    public void setRouteRequestUId(String routeRequestUId) {
        this.routeRequestUId = routeRequestUId;
    }

    public String getDriverUId() {
        return driverUId;
    }

    public void setDriverUId(String driverUId) {
        this.driverUId = driverUId;
    }

    public SavedPlace getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(SavedPlace startPlace) {
        this.startPlace = startPlace;
    }

    public SavedPlace getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(SavedPlace endPlace) {
        this.endPlace = endPlace;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Float getPercentDiscount() {
        return percentDiscount;
    }

    public void setPercentDiscount(Float percentDiscount) {
        this.percentDiscount = percentDiscount;
    }

    public RouteRequestState getRouteRequestState() {
        return routeRequestState;
    }

    public void setRouteRequestState(RouteRequestState routeRequestState) {
        this.routeRequestState = routeRequestState;
    }

    public NotifyTrip getNotifyTrip() {
        return notifyTrip;
    }

    public void setNotifyTrip(NotifyTrip notifyTrip) {
        this.notifyTrip = notifyTrip;
    }


}
