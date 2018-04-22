package com.example.huydaoduc.hieu.chi.hhapp.Manager;

public class RiderRequest {

    private String uid;
    private String pickUpLocation;
    private String dropLocation;
    private String postTime;

    public RiderRequest() {
    }

    public RiderRequest(String uid, String pickUpLocation, String dropLocation, String postTime) {
        this.uid = uid;
        this.pickUpLocation = pickUpLocation;
        this.dropLocation = dropLocation;
        this.postTime = postTime;
    }

    // Getter & Setter
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}
