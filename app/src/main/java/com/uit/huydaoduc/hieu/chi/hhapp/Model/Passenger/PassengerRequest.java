package com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripFareInfo;

import java.util.Date;

public class PassengerRequest implements Parcelable {

    /**
     * Get LatLng only for correction purpose
     */
    private String passengerRequestUId;
    private String passengerUId;

    private PassengerRequestState passengerRequestState;

    private SavedPlace pickUpSavePlace;
    private SavedPlace dropOffSavePlace;

    private TripFareInfo tripFareInfo;

    private NotifyTrip notifyTrip;

    @Nullable
    private String note;        // note for Driver
    private Integer waitMinute;     // how long can passenger wait for driver begin from startTime
    private float percentOff;

    public PassengerRequest() {
    }

    public boolean func_isInTheFuture() {                   // mean time out
        if(TimeUtils.compareWithNow(tripFareInfo.getStartTime()) >= 0)
            return true;
        return false;
    }

    public Date func_getStartTimeAsDate() {                   // mean time out
        if (tripFareInfo == null) {
            return null;
        }
        return tripFareInfo.func_getStartTimeAsDate();
    }

    public PassengerRequest(String passengerRequestUId, String passengerUId, PassengerRequestState passengerRequestState, SavedPlace pickUpSavePlace, SavedPlace dropOffSavePlace, TripFareInfo tripFareInfo, NotifyTrip notifyTrip, String note, Integer waitMinute, float percentOff) {
        this.passengerRequestUId = passengerRequestUId;
        this.passengerUId = passengerUId;
        this.passengerRequestState = passengerRequestState;
        this.pickUpSavePlace = pickUpSavePlace;
        this.dropOffSavePlace = dropOffSavePlace;
        this.tripFareInfo = tripFareInfo;
        this.notifyTrip = notifyTrip;
        this.note = note;
        this.waitMinute = waitMinute;
        this.percentOff = percentOff;
    }

    public String getPassengerRequestUId() {
        return passengerRequestUId;
    }

    public void setPassengerRequestUId(String passengerRequestUId) {
        this.passengerRequestUId = passengerRequestUId;
    }

    public String getPassengerUId() {
        return passengerUId;
    }

    public void setPassengerUId(String passengerUId) {
        this.passengerUId = passengerUId;
    }

    public PassengerRequestState getPassengerRequestState() {
        return passengerRequestState;
    }

    public void setPassengerRequestState(PassengerRequestState passengerRequestState) {
        this.passengerRequestState = passengerRequestState;
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

    public TripFareInfo getTripFareInfo() {
        return tripFareInfo;
    }

    public void setTripFareInfo(TripFareInfo tripFareInfo) {
        this.tripFareInfo = tripFareInfo;
    }

    public NotifyTrip getNotifyTrip() {
        return notifyTrip;
    }

    public void setNotifyTrip(NotifyTrip notifyTrip) {
        this.notifyTrip = notifyTrip;
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
        private String passengerRequestUId;
        private String passengerUId;
        private PassengerRequestState passengerRequestState;
        private SavedPlace pickUpSavePlace;
        private SavedPlace dropOffSavePlace;
        private TripFareInfo tripFareInfo;
        private NotifyTrip notifyTrip;
        private String note;        // note for Driver
        private Integer waitMinute;     // how long can passenger wait for driver begin from startTime
        private float percentOff;

        private Builder() {
        }

        public static Builder aPassengerRequest(String passengerRequestUId) {
            return new Builder().setPassengerRequestUId(passengerRequestUId);
        }

        private Builder setPassengerRequestUId(String passengerRequestUId) {
            this.passengerRequestUId = passengerRequestUId;
            return this;
        }

        public Builder setPassengerUId(String passengerUId) {
            this.passengerUId = passengerUId;
            return this;
        }

        public Builder setPassengerRequestState(PassengerRequestState passengerRequestState) {
            this.passengerRequestState = passengerRequestState;
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

        public Builder setTripFareInfo(TripFareInfo tripFareInfo) {
            this.tripFareInfo = tripFareInfo;
            return this;
        }

        public Builder setNotifyTrip(NotifyTrip notifyTrip) {
            this.notifyTrip = notifyTrip;
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
            passengerRequest.setPassengerRequestUId(passengerRequestUId);
            passengerRequest.setPassengerUId(passengerUId);
            passengerRequest.setPassengerRequestState(passengerRequestState);
            passengerRequest.setPickUpSavePlace(pickUpSavePlace);
            passengerRequest.setDropOffSavePlace(dropOffSavePlace);
            passengerRequest.setTripFareInfo(tripFareInfo);
            passengerRequest.setNotifyTrip(notifyTrip);
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
        dest.writeString(this.passengerRequestUId);
        dest.writeString(this.passengerUId);
        dest.writeInt(this.passengerRequestState == null ? -1 : this.passengerRequestState.ordinal());
        dest.writeParcelable(this.pickUpSavePlace, flags);
        dest.writeParcelable(this.dropOffSavePlace, flags);
        dest.writeParcelable(this.tripFareInfo, flags);
        dest.writeParcelable(this.notifyTrip, flags);
        dest.writeString(this.note);
        dest.writeValue(this.waitMinute);
        dest.writeFloat(this.percentOff);
    }

    protected PassengerRequest(Parcel in) {
        this.passengerRequestUId = in.readString();
        this.passengerUId = in.readString();
        int tmpPassengerRequestState = in.readInt();
        this.passengerRequestState = tmpPassengerRequestState == -1 ? null : PassengerRequestState.values()[tmpPassengerRequestState];
        this.pickUpSavePlace = in.readParcelable(SavedPlace.class.getClassLoader());
        this.dropOffSavePlace = in.readParcelable(SavedPlace.class.getClassLoader());
        this.tripFareInfo = in.readParcelable(TripFareInfo.class.getClassLoader());
        this.notifyTrip = in.readParcelable(NotifyTrip.class.getClassLoader());
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
