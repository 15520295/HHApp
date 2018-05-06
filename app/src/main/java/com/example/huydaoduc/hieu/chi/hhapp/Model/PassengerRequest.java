package com.example.huydaoduc.hieu.chi.hhapp.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;

public class PassengerRequest implements Parcelable {

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
    private Integer waitMinute;     // how long can passenger wait for driver begin from startTime
    private float percentOff;



    public PassengerRequest() {
    }

    public PassengerRequest(String passengerUId, SavedPlace pickUpSavePlace, SavedPlace dropOffSavePlace, String startTime, CarType carType, String note, Integer waitMinute, float percentOff) {
        this.passengerUId = passengerUId;
        this.pickUpSavePlace = pickUpSavePlace;
        this.dropOffSavePlace = dropOffSavePlace;
        this.startTime = startTime;
        this.carType = carType;
        this.note = note;
        this.waitMinute = waitMinute;
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

    public Integer getWaitMinute() {
        return waitMinute;
    }

    public void setWaitMinute(Integer waitMinute) {
        this.waitMinute = waitMinute;
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
        private Integer waitMinute;     // how long can passenger wait for driver begin from startTime
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

        public Builder setWaitMinute(Integer waitMinute) {
            this.waitMinute = waitMinute;
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
            passengerRequest.setWaitMinute(waitMinute);
            passengerRequest.setPercentOff(percentOff);
            return passengerRequest;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.passengerUId);
        dest.writeParcelable(this.pickUpSavePlace, flags);
        dest.writeParcelable(this.dropOffSavePlace, flags);
        dest.writeString(this.startTime);
        dest.writeInt(this.carType == null ? -1 : this.carType.ordinal());
        dest.writeString(this.note);
        dest.writeValue(this.waitMinute);
        dest.writeFloat(this.percentOff);
    }

    protected PassengerRequest(Parcel in) {
        this.passengerUId = in.readString();
        this.pickUpSavePlace = in.readParcelable(SavedPlace.class.getClassLoader());
        this.dropOffSavePlace = in.readParcelable(SavedPlace.class.getClassLoader());
        this.startTime = in.readString();
        int tmpCarType = in.readInt();
        this.carType = tmpCarType == -1 ? null : CarType.values()[tmpCarType];
        this.note = in.readString();
        this.waitMinute = (Integer) in.readValue(Integer.class.getClassLoader());
        this.percentOff = in.readFloat();
    }

    public static final Creator<PassengerRequest> CREATOR = new Creator<PassengerRequest>() {
        @Override
        public PassengerRequest createFromParcel(Parcel source) {
            return new PassengerRequest(source);
        }

        @Override
        public PassengerRequest[] newArray(int size) {
            return new PassengerRequest[size];
        }
    };
}
