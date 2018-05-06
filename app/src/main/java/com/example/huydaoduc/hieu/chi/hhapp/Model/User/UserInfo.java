package com.example.huydaoduc.hieu.chi.hhapp.Model.User;

import android.net.Uri;

import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarInfo;

import java.util.List;

public class UserInfo {

    private String uid;
    private String phoneNumber;
    private String name;
    private String yearOfBirth;
    private Uri photoUri;
    //todo : private Integer cancelPercent;

    private CarInfo carInfo;

    private List<String> trips;

    public UserInfo() {

    }

    public UserInfo(String uid, String phoneNumber, String name, String yearOfBirth, Uri photoUri, CarInfo carInfo, List<String> trips) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.photoUri = photoUri;
        this.carInfo = carInfo;
        this.trips = trips;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(CarInfo carInfo) {
        this.carInfo = carInfo;
    }

    public List<String> getTrips() {
        return trips;
    }

    public void setTrips(List<String> trips) {
        this.trips = trips;
    }


    public static final class Builder {
        private String uid;
        private String phoneNumber;
        private String name;
        private String yearOfBirth;
        private Uri photoUri;
        private CarInfo carInfo;
        private List<String> trips;

        private Builder() {
        }

        public static Builder anUserInfo(String uid) {
            return new Builder().setUid(uid);
        }

        private Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setYearOfBirth(String yearOfBirth) {
            this.yearOfBirth = yearOfBirth;
            return this;
        }

        public Builder setPhotoUri(Uri photoUri) {
            this.photoUri = photoUri;
            return this;
        }

        public Builder setCarInfo(CarInfo carInfo) {
            this.carInfo = carInfo;
            return this;
        }

        public Builder setTrips(List<String> trips) {
            this.trips = trips;
            return this;
        }

        public UserInfo build() {
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(uid);
            userInfo.setPhoneNumber(phoneNumber);
            userInfo.setName(name);
            userInfo.setYearOfBirth(yearOfBirth);
            userInfo.setPhotoUri(photoUri);
            userInfo.setCarInfo(carInfo);
            userInfo.setTrips(trips);
            return userInfo;
        }
    }
}
