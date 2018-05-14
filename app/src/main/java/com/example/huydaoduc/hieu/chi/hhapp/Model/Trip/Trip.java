package com.example.huydaoduc.hieu.chi.hhapp.Model.Trip;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;

public class Trip implements Parcelable {

    private String tripUId;

    private TripType tripType;
    private TripState tripState;

    private String passengerUId;
    private String driverUId;

    private String passengerRequestUId;
    private String routeRequestUId;

    public Trip() {
    }

    public Trip(String tripUId, TripType tripType, TripState tripState, String passengerUId, String driverUId, String passengerRequestUId, String routeRequestUId) {
        this.tripUId = tripUId;
        this.tripType = tripType;
        this.tripState = tripState;
        this.passengerUId = passengerUId;
        this.driverUId = driverUId;
        this.passengerRequestUId = passengerRequestUId;
        this.routeRequestUId = routeRequestUId;
    }

    public String getTripUId() {
        return tripUId;
    }

    public void setTripUId(String tripUId) {
        this.tripUId = tripUId;
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

    public String getPassengerRequestUId() {
        return passengerRequestUId;
    }

    public void setPassengerRequestUId(String passengerRequestUId) {
        this.passengerRequestUId = passengerRequestUId;
    }

    public String getRouteRequestUId() {
        return routeRequestUId;
    }

    public void setRouteRequestUId(String routeRequestUId) {
        this.routeRequestUId = routeRequestUId;
    }

    public static final class Builder {
        private String tripUId;
        private TripType tripType;
        private TripState tripState;
        private String passengerUId;
        private String driverUId;
        private String passengerRequestUId;
        private String routeRequestUId;

        private Builder() {
        }

        public static Builder aTrip(String tripUId) {
            return new Builder().setTripUId(tripUId);
        }

        private Builder setTripUId(String tripUId) {
            this.tripUId = tripUId;
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

        public Builder setPassengerUId(String passengerUId) {
            this.passengerUId = passengerUId;
            return this;
        }

        public Builder setDriverUId(String driverUId) {
            this.driverUId = driverUId;
            return this;
        }

        public Builder setPassengerRequestUId(String passengerRequestUId) {
            this.passengerRequestUId = passengerRequestUId;
            return this;
        }

        public Builder setRouteRequestUId(String routeRequestUId) {
            this.routeRequestUId = routeRequestUId;
            return this;
        }

        public Trip build() {
            Trip trip = new Trip();
            trip.setTripUId(tripUId);
            trip.setTripType(tripType);
            trip.setTripState(tripState);
            trip.setPassengerUId(passengerUId);
            trip.setDriverUId(driverUId);
            trip.setPassengerRequestUId(passengerRequestUId);
            trip.setRouteRequestUId(routeRequestUId);
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
        dest.writeInt(this.tripType == null ? -1 : this.tripType.ordinal());
        dest.writeInt(this.tripState == null ? -1 : this.tripState.ordinal());
        dest.writeString(this.passengerUId);
        dest.writeString(this.driverUId);
        dest.writeString(this.passengerRequestUId);
        dest.writeString(this.routeRequestUId);
    }

    protected Trip(Parcel in) {
        this.tripUId = in.readString();
        int tmpTripType = in.readInt();
        this.tripType = tmpTripType == -1 ? null : TripType.values()[tmpTripType];
        int tmpTripState = in.readInt();
        this.tripState = tmpTripState == -1 ? null : TripState.values()[tmpTripState];
        this.passengerUId = in.readString();
        this.driverUId = in.readString();
        this.passengerRequestUId = in.readString();
        this.routeRequestUId = in.readString();
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
