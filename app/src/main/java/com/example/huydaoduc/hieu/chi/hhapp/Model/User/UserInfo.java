package com.example.huydaoduc.hieu.chi.hhapp.Model.User;

import android.net.Uri;

public class UserInfo {

    private String uid;
    private String phoneNumber;
    private String name;
    private String yearOfBirth;
    private Uri photoUri;
    private String carName;
    private String carId;
    private CarType carType;

    public UserInfo() {

    }

    public UserInfo(String uid, String phoneNumber, String name, String yearOfBirth, Uri photoUri, String carName, String carId, CarType carType) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.photoUri = photoUri;
        this.carName = carName;
        this.carId = carId;
        this.carType = carType;
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

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public static final class Builder {
        private String uid;
        private String phoneNumber;
        private String name;
        private String yearOfBirth;
        private Uri photoUri;
        private String carName;
        private String carId;

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

        public Builder setCarName(String carName) {
            this.carName = carName;
            return this;
        }

        public Builder setCarId(String carId) {
            this.carId = carId;
            return this;
        }

        public UserInfo build() {
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(uid);
            userInfo.setPhoneNumber(phoneNumber);
            userInfo.setName(name);
            userInfo.setYearOfBirth(yearOfBirth);
            userInfo.setPhotoUri(photoUri);
            userInfo.setCarName(carName);
            userInfo.setCarId(carId);
            return userInfo;
        }
    }
}
