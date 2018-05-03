package com.example.huydaoduc.hieu.chi.hhapp.Model;

import android.support.annotation.Nullable;

import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.CarType;

public class PassengerRequest {

    /**
     * Get LatLng only for correction purpose
     */
    private String passengerUId;

    private SavedPlace pickUpSavePlace;
    private SavedPlace dropOffSavePlace;

    private String startTime;
    private CarType carType;
    @Nullable
    private String note;        // note for Driver
    private float percentOff;


    public PassengerRequest() {
    }

    public PassengerRequest(String passengerUId, SavedPlace pickUpSavePlace, SavedPlace dropOffSavePlace, String startTime, CarType carType, String note, float percentOff) {
        this.passengerUId = passengerUId;
        this.pickUpSavePlace = pickUpSavePlace;
        this.dropOffSavePlace = dropOffSavePlace;
        this.startTime = startTime;
        this.carType = carType;
        this.note = note;
        this.percentOff = percentOff;
    }

    public String getPassengerUId() {
        return passengerUId;
    }

    public void setPassengerUId(String passengerUId) {
        this.passengerUId = passengerUId;
    }

    public SavedPlace getPickUpSavePlace() {
        return pickUpSavePlace;
    }

    public void setPickUpSavePlace(SavedPlace pickUpSavePlace) {
        this.pickUpSavePlace = pickUpSavePlace;
    }

    public SavedPlace getDropOffSavePlace() {
        return dropOffSavePlace;
    }

    public void setDropOffSavePlace(SavedPlace dropOffSavePlace) {
        this.dropOffSavePlace = dropOffSavePlace;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    @Nullable
    public String getNote() {
        return note;
    }

    public void setNote(@Nullable String note) {
        this.note = note;
    }

    public float getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(float percentOff) {
        this.percentOff = percentOff;
    }


    public static final class Builder {
        private String passengerUId;
        private SavedPlace pickUpSavePlace;
        private SavedPlace dropOffSavePlace;
        private String startTime;
        private CarType carType;
        private String note;        // note for Driver
        private float percentOff;

        private Builder() {
        }

        public static Builder aPassengerRequest(String passengerUId) {
            return new Builder().setPassengerUId(passengerUId);
        }

        private Builder setPassengerUId(String passengerUId) {
            this.passengerUId = passengerUId;
            return this;
        }

        public Builder setPickUpSavePlace(SavedPlace pickUpSavePlace) {
            this.pickUpSavePlace = pickUpSavePlace;
            return this;
        }

        public Builder setDropOffSavePlace(SavedPlace dropOffSavePlace) {
            this.dropOffSavePlace = dropOffSavePlace;
            return this;
        }

        public Builder setStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setCarType(CarType carType) {
            this.carType = carType;
            return this;
        }

        public Builder setNote(String note) {
            this.note = note;
            return this;
        }

        public Builder setPercentOff(float percentOff) {
            this.percentOff = percentOff;
            return this;
        }

        public PassengerRequest build() {
            PassengerRequest passengerRequest = new PassengerRequest();
            passengerRequest.setPassengerUId(passengerUId);
            passengerRequest.setPickUpSavePlace(pickUpSavePlace);
            passengerRequest.setDropOffSavePlace(dropOffSavePlace);
            passengerRequest.setStartTime(startTime);
            passengerRequest.setCarType(carType);
            passengerRequest.setNote(note);
            passengerRequest.setPercentOff(percentOff);
            return passengerRequest;
        }
    }
}
