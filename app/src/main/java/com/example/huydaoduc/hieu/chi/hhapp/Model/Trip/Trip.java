package com.example.huydaoduc.hieu.chi.hhapp.Model.Trip;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.PassengerRequest;

public class Trip implements Parcelable {

    private String tripUId;
    private String passengerUId;
    private String driverUId;

    private TripType tripType;
    private TripState tripState;

    private TripFareInfo tripFareInfo;

    private PassengerRequest passengerRequest;
    private RouteRequest routeRequest;

    public Trip() {
    }

    public Trip(String tripUId, String passengerUId, String driverUId, TripType tripType, TripState tripState, TripFareInfo tripFareInfo, PassengerRequest passengerRequest, RouteRequest routeRequest) {
        this.tripUId = tripUId;
        this.passengerUId = passengerUId;
        this.driverUId = driverUId;
        this.tripType = tripType;
        this.tripState = tripState;
        this.tripFareInfo = tripFareInfo;
        this.passengerRequest = passengerRequest;
        this.routeRequest = routeRequest;
    }

    public String getTripUId() {
        return tripUId;
    }

    public void setTripUId(String tripUId) {
        this.tripUId = tripUId;
    }

    public String getPassengerUId() {
        return passengerUId;
    }

    public void setPassengerUId(String passengerUId) {
        this.passengerUId = passengerUId;
    }

    public String getDriverUId() {
        return driverUId;
    }

    public void setDriverUId(String driverUId) {
        this.driverUId = driverUId;
    }

    public TripType getTripType() {
        return tripType;
    }

    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    public TripState getTripState() {
        return tripState;
    }

    public void setTripState(TripState tripState) {
        this.tripState = tripState;
    }

    public TripFareInfo getTripFareInfo() {
        return tripFareInfo;
    }

    public void setTripFareInfo(TripFareInfo tripFareInfo) {
        this.tripFareInfo = tripFareInfo;
    }

    public PassengerRequest getPassengerRequest() {
        return passengerRequest;
    }

    public void setPassengerRequest(PassengerRequest passengerRequest) {
        this.passengerRequest = passengerRequest;
    }

    public RouteRequest getRouteRequest() {
        return routeRequest;
    }

    public void setRouteRequest(RouteRequest routeRequest) {
        this.routeRequest = routeRequest;
    }

    public static final class Builder {
        private String tripUId;
        private String passengerUId;
        private String driverUId;
        private TripType tripType;
        private TripState tripState;
        private TripFareInfo tripFareInfo;
        private PassengerRequest passengerRequest;
        private RouteRequest routeRequest;

        private Builder() {
        }

        public static Builder aTrip(String tripUId) {
            return new Builder().setTripUId(tripUId);
        }


        private Builder setTripUId(String tripUId) {
            this.tripUId = tripUId;
            return this;
        }

        public Builder setPassengerUId(String passengerUId) {
            this.passengerUId = passengerUId;
            return this;
        }

        public Builder setDriverUId(String driverUId) {
            this.driverUId = driverUId;
            return this;
        }

        public Builder setTripType(TripType tripType) {
            this.tripType = tripType;
            return this;
        }

        public Builder setTripState(TripState tripState) {
            this.tripState = tripState;
            return this;
        }

        public Builder setTripFareInfo(TripFareInfo tripFareInfo) {
            this.tripFareInfo = tripFareInfo;
            return this;
        }

        public Builder setPassengerRequest(PassengerRequest passengerRequest) {
            this.passengerRequest = passengerRequest;
            return this;
        }

        public Builder setRouteRequest(RouteRequest routeRequest) {
            this.routeRequest = routeRequest;
            return this;
        }

        public Trip build() {
            Trip trip = new Trip();
            trip.setTripUId(tripUId);
            trip.setPassengerUId(passengerUId);
            trip.setDriverUId(driverUId);
            trip.setTripType(tripType);
            trip.setTripState(tripState);
            trip.setTripFareInfo(tripFareInfo);
            trip.setPassengerRequest(passengerRequest);
            trip.setRouteRequest(routeRequest);
            return trip;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tripUId);
        dest.writeString(this.passengerUId);
        dest.writeString(this.driverUId);
        dest.writeInt(this.tripType == null ? -1 : this.tripType.ordinal());
        dest.writeInt(this.tripState == null ? -1 : this.tripState.ordinal());
        dest.writeParcelable(this.tripFareInfo, flags);
        dest.writeParcelable(this.passengerRequest, flags);
        dest.writeParcelable(this.routeRequest, flags);
    }

    protected Trip(Parcel in) {
        this.tripUId = in.readString();
        this.passengerUId = in.readString();
        this.driverUId = in.readString();
        int tmpTripType = in.readInt();
        this.tripType = tmpTripType == -1 ? null : TripType.values()[tmpTripType];
        int tmpTripState = in.readInt();
        this.tripState = tmpTripState == -1 ? null : TripState.values()[tmpTripState];
        this.tripFareInfo = in.readParcelable(TripFareInfo.class.getClassLoader());
        this.passengerRequest = in.readParcelable(PassengerRequest.class.getClassLoader());
        this.routeRequest = in.readParcelable(RouteRequest.class.getClassLoader());
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel source) {
            return new Trip(source);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}
