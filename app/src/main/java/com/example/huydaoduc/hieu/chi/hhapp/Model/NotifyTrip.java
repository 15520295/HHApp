package com.example.huydaoduc.hieu.chi.hhapp.Model;

public class NotifyTrip {
    private String tripUId;
    private String lastTimeCheck;

    public NotifyTrip() {
    }

    public NotifyTrip(String tripUId, String lastTimeCheck) {
        this.tripUId = tripUId;
        this.lastTimeCheck = lastTimeCheck;
    }

    public String getTripUId() {
        return tripUId;
    }

    public void setTripUId(String tripUId) {
        this.tripUId = tripUId;
    }

    public String getLastTimeCheck() {
        return lastTimeCheck;
    }

    public void setLastTimeCheck(String lastTimeCheck) {
        this.lastTimeCheck = lastTimeCheck;
    }
}
