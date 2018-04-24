package com.example.huydaoduc.hieu.chi.hhapp.Model;


public class Trip {

    private String tripUId;
    private String passengerUId;
    private String driverUId;
    private String tripTime;

    private PassengerRequest passengerRequest;
    private DriverRequest driverRequest;

    public Trip() {
    }

    public Trip(String tripUId, String passengerUId, String driverUId, String tripTime, PassengerRequest passengerRequest, DriverRequest driverRequest) {
        this.tripUId = tripUId;
        this.passengerUId = passengerUId;
        this.driverUId = driverUId;
        this.tripTime = tripTime;
        this.passengerRequest = passengerRequest;
        this.driverRequest = driverRequest;
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

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public PassengerRequest getPassengerRequest() {
        return passengerRequest;
    }

    public void setPassengerRequest(PassengerRequest passengerRequest) {
        this.passengerRequest = passengerRequest;
    }

    public DriverRequest getDriverRequest() {
        return driverRequest;
    }

    public void setDriverRequest(DriverRequest driverRequest) {
        this.driverRequest = driverRequest;
    }


    public static final class Builder {
        private String tripUId;
        private String passengerUId;
        private String driverUId;
        private String tripTime;
        private PassengerRequest passengerRequest;
        private DriverRequest driverRequest;

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

        public Builder setTripTime(String tripTime) {
            this.tripTime = tripTime;
            return this;
        }

        public Builder setPassengerRequest(PassengerRequest passengerRequest) {
            this.passengerRequest = passengerRequest;
            return this;
        }

        public Builder setDriverRequest(DriverRequest driverRequest) {
            this.driverRequest = driverRequest;
            return this;
        }

        public Trip build() {
            Trip trip = new Trip();
            trip.setTripUId(tripUId);
            trip.setPassengerUId(passengerUId);
            trip.setDriverUId(driverUId);
            trip.setTripTime(tripTime);
            trip.setPassengerRequest(passengerRequest);
            trip.setDriverRequest(driverRequest);
            return trip;
        }
    }
}
