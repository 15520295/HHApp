package com.example.huydaoduc.hieu.chi.hhapp.Model.Trip;


import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.PassengerRequest;

public class Trip {

    private String tripUId;
    private String passengerUId;
    private String driverUId;

    private TripStyle tripStyle;
    private TripState tripState;

    private String startTime;           // when pick up
    private String endTime;             // when drop off

    private Float tripDistance;
    private Float tripDuration;

    private Float estimateFare;
    private Float finalFare;

    private PassengerRequest passengerRequest;
    private RouteRequest routeRequest;

    public Trip() {
    }

    public Trip(String tripUId, String passengerUId, String driverUId, TripStyle tripStyle, TripState tripState, String startTime, String endTime, Float tripDistance, Float tripDuration, Float estimateFare, Float finalFare, PassengerRequest passengerRequest, RouteRequest routeRequest) {
        this.tripUId = tripUId;
        this.passengerUId = passengerUId;
        this.driverUId = driverUId;
        this.tripStyle = tripStyle;
        this.tripState = tripState;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tripDistance = tripDistance;
        this.tripDuration = tripDuration;
        this.estimateFare = estimateFare;
        this.finalFare = finalFare;
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

    public TripStyle getTripStyle() {
        return tripStyle;
    }

    public void setTripStyle(TripStyle tripStyle) {
        this.tripStyle = tripStyle;
    }

    public TripState getTripState() {
        return tripState;
    }

    public void setTripState(TripState tripState) {
        this.tripState = tripState;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Float getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(Float tripDistance) {
        this.tripDistance = tripDistance;
    }

    public Float getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(Float tripDuration) {
        this.tripDuration = tripDuration;
    }

    public Float getEstimateFare() {
        return estimateFare;
    }

    public void setEstimateFare(Float estimateFare) {
        this.estimateFare = estimateFare;
    }

    public Float getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(Float finalFare) {
        this.finalFare = finalFare;
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
        private TripStyle tripStyle;
        private TripState tripState;
        private String startTime;
        private String endTime;
        private Float tripDistance;
        private Float tripDuration;
        private Float estimateFare;
        private Float finalFare;
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

        public Builder setTripStyle(TripStyle tripStyle) {
            this.tripStyle = tripStyle;
            return this;
        }

        public Builder setTripState(TripState tripState) {
            this.tripState = tripState;
            return this;
        }

        public Builder setStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setEndTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder setTripDistance(Float tripDistance) {
            this.tripDistance = tripDistance;
            return this;
        }

        public Builder setTripDuration(Float tripDuration) {
            this.tripDuration = tripDuration;
            return this;
        }

        public Builder setEstimateFare(Float estimateFare) {
            this.estimateFare = estimateFare;
            return this;
        }

        public Builder setFinalFare(Float finalFare) {
            this.finalFare = finalFare;
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
            trip.setTripStyle(tripStyle);
            trip.setTripState(tripState);
            trip.setStartTime(startTime);
            trip.setEndTime(endTime);
            trip.setTripDistance(tripDistance);
            trip.setTripDuration(tripDuration);
            trip.setEstimateFare(estimateFare);
            trip.setFinalFare(finalFare);
            trip.setPassengerRequest(passengerRequest);
            trip.setRouteRequest(routeRequest);
            return trip;
        }
    }
}
