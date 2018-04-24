package com.example.huydaoduc.hieu.chi.hhapp.Model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.CarType;

public class PassengerRequest {

    /**
     * Get LatLng only for correction purpose
     */
    private String passengerUId;

    private SavedPlace pickUpSavePlace;
    private SavedPlace dropOffSavePlace;

    private String postTime;
    private CarType carType;
    @Nullable
    private String note;        // note for Driver


    public PassengerRequest() {
    }

    public PassengerRequest(String passengerUId, SavedPlace pickUpSavePlace, SavedPlace dropOffSavePlace, String postTime, CarType carType, String note) {
        this.passengerUId = passengerUId;
        this.pickUpSavePlace = pickUpSavePlace;
        this.dropOffSavePlace = dropOffSavePlace;
        this.postTime = postTime;
        this.carType = carType;
        this.note = note;
    }

    // Getter & Setter
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

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
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


    public static final class Builder {
        private String uid;
        private SavedPlace pickUpSavePlace;
        private SavedPlace dropOffSavePlace;
        private String postTime;
        private CarType carType;
        private String note;        // note for Driver

        private Builder() {
        }

        public static Builder aPassengerRequest(@NonNull String uid) {
            return new Builder().setUid(uid);
        }

        private Builder setUid(String uid) {
            this.uid = uid;
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

        public Builder setPostTime(String postTime) {
            this.postTime = postTime;
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

        public PassengerRequest build() {
            PassengerRequest passengerRequest = new PassengerRequest();
            passengerRequest.setPickUpSavePlace(pickUpSavePlace);
            passengerRequest.setDropOffSavePlace(dropOffSavePlace);
            passengerRequest.setPostTime(postTime);
            passengerRequest.setCarType(carType);
            passengerRequest.setNote(note);
            return passengerRequest;
        }
    }
}
