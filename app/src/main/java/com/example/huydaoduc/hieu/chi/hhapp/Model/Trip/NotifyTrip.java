package com.example.huydaoduc.hieu.chi.hhapp.Model.Trip;

import android.os.Parcel;
import android.os.Parcelable;

public class NotifyTrip implements Parcelable {
    private String tripUId;
    private Boolean isNotified;

    public NotifyTrip() {
    }

    public NotifyTrip(String tripUId, Boolean isNotified) {
        this.tripUId = tripUId;
        this.isNotified = isNotified;
    }

    public String getTripUId() {
        return tripUId;
    }

    public void setTripUId(String tripUId) {
        this.tripUId = tripUId;
    }

    public Boolean isNotified() {
        return isNotified;
    }

    public void setNotified(Boolean notified) {
        isNotified = notified;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tripUId);
        dest.writeValue(this.isNotified);
    }

    protected NotifyTrip(Parcel in) {
        this.tripUId = in.readString();
        this.isNotified = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<NotifyTrip> CREATOR = new Parcelable.Creator<NotifyTrip>() {
        @Override
        public NotifyTrip createFromParcel(Parcel source) {
            return new NotifyTrip(source);
        }

        @Override
        public NotifyTrip[] newArray(int size) {
            return new NotifyTrip[size];
        }
    };
}
