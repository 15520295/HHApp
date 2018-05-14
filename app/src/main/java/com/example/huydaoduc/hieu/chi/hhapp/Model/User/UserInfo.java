package com.example.huydaoduc.hieu.chi.hhapp.Model.User;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class UserInfo implements Parcelable {

    private String uid;
    private String phoneNumber;
    private String name;
    private String yearOfBirth;
    private String photo;
    //todo : private Integer cancelPercent;

    private List<String> trips;

    public UserInfo() {

    }

    public UserInfo(String uid, String phoneNumber, String name, String yearOfBirth, String photo, List<String> trips) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.photo = photo;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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
        private String photo;
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

        public Builder setPhoto(String photo) {
            this.photo = photo;
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
            userInfo.setPhoto(photo);
            userInfo.setTrips(trips);
            return userInfo;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.name);
        dest.writeString(this.yearOfBirth);
        dest.writeString(this.photo);
        dest.writeStringList(this.trips);
    }

    protected UserInfo(Parcel in) {
        this.uid = in.readString();
        this.phoneNumber = in.readString();
        this.name = in.readString();
        this.yearOfBirth = in.readString();
        this.photo = in.readString();
        this.trips = in.createStringArrayList();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
