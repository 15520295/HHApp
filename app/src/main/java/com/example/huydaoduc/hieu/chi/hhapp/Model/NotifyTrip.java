package com.example.huydaoduc.hieu.chi.hhapp.Model;

public class NotifyTrip {
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
}
