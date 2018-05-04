package com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.NotifyTrip;

public class RouteRequest implements Parcelable {

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

    public boolean func_isInTheFuture() {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.routeRequestUId);
        dest.writeString(this.driverUId);
        dest.writeParcelable(this.startPlace, flags);
        dest.writeParcelable(this.endPlace, flags);
        dest.writeString(this.summary);
        dest.writeString(this.startTime);
        dest.writeValue(this.percentDiscount);
        dest.writeInt(this.routeRequestState == null ? -1 : this.routeRequestState.ordinal());
        dest.writeParcelable(this.notifyTrip, flags);
    }

    protected RouteRequest(Parcel in) {
        this.routeRequestUId = in.readString();
        this.driverUId = in.readString();
        this.startPlace = in.readParcelable(SavedPlace.class.getClassLoader());
        this.endPlace = in.readParcelable(SavedPlace.class.getClassLoader());
        this.summary = in.readString();
        this.startTime = in.readString();
        this.percentDiscount = (Float) in.readValue(Float.class.getClassLoader());
        int tmpRouteRequestState = in.readInt();
        this.routeRequestState = tmpRouteRequestState == -1 ? null : RouteRequestState.values()[tmpRouteRequestState];
        this.notifyTrip = in.readParcelable(NotifyTrip.class.getClassLoader());
    }

    public static final Parcelable.Creator<RouteRequest> CREATOR = new Parcelable.Creator<RouteRequest>() {
        @Override
        public RouteRequest createFromParcel(Parcel source) {
            return new RouteRequest(source);
        }

        @Override
        public RouteRequest[] newArray(int size) {
            return new RouteRequest[size];
        }
    };
}
