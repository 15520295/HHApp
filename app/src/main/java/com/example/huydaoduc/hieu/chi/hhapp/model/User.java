package com.example.huydaoduc.hieu.chi.hhapp.Model;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class User implements Parcelable {
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.<Uri>readParcelable(Uri.class.getClassLoader()));
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String mProviderId;
    private String mPhoneNumber;
    private String mName;
    private String mYearOfBirth;
    private String mLocation;
    private Uri mPhotoUri;
    public User() {
    }

    private User(String providerId,  String phoneNumber, String name, String yearOfBirth, String location, Uri photoUri) {
        mProviderId = providerId;
        mPhoneNumber = phoneNumber;
        mName = name;
        mYearOfBirth = yearOfBirth;
        mLocation = location;
        mPhotoUri = photoUri;
    }

    public static User getUser(Intent intent) {
        return intent.getParcelableExtra("extra_user");
    }

    public static User getUser(Bundle arguments) {
        return arguments.getParcelable("extra_user");
    }

    @NonNull
    public String getProviderId() {
        return mProviderId;
    }

    @NonNull
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @Nullable
    public String getYearOfBirth() {
        return mYearOfBirth;
    }

    @Nullable
    public String getLocation() {
        return mLocation;
    }

    @Nullable
    public Uri getPhotoUri() {
        return mPhotoUri;
    }

    public void setmProviderId(String mProviderId) {
        this.mProviderId = mProviderId;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmYearOfBirth(String mYearOfBirth) {
        this.mYearOfBirth = mYearOfBirth;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public void setmPhotoUri(Uri mPhotoUri) {
        this.mPhotoUri = mPhotoUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return mProviderId.equals(user.mProviderId)
                && (mPhoneNumber == null ? user.mPhoneNumber == null : mPhoneNumber.equals(user.mPhoneNumber))
                && (mName == null ? user.mName == null : mName.equals(user.mName))
                && (mYearOfBirth == null ? user.mYearOfBirth == null : mYearOfBirth.equals(user.mYearOfBirth))
                && (mLocation == null ? user.mLocation == null : mLocation.equals(user.mLocation))
                && (mPhotoUri == null ? user.mPhotoUri == null : mPhotoUri.equals(user.mPhotoUri));
    }

    @Override
    public int hashCode() {
        int result = mProviderId.hashCode();
        result = 31 * result + (mPhoneNumber == null ? 0 : mPhoneNumber.hashCode());
        result = 31 * result + (mName == null ? 0 : mName.hashCode());
        result = 31 * result + (mYearOfBirth == null ? 0 : mYearOfBirth.hashCode());
        result = 31 * result + (mLocation == null ? 0 : mLocation.hashCode());
        result = 31 * result + (mPhotoUri == null ? 0 : mPhotoUri.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "mProviderId='" + mProviderId + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mName='" + mName + '\'' +
                ", mYearOfBirth='" + mYearOfBirth + '\'' +
                ", mLocation='" + mLocation + '\'' +
                ", mPhotoUri=" + mPhotoUri +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mProviderId);
        dest.writeString(mPhoneNumber);
        dest.writeString(mName);
        dest.writeString(mYearOfBirth);
        dest.writeString(mLocation);
        dest.writeParcelable(mPhotoUri, flags);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static class Builder {
        private String mProviderId;
        private String mPhoneNumber;
        private String mName;
        private String mYearOfBirth;
        private String mLocation;
        private Uri mPhotoUri;

        public Builder( @NonNull String providerId) {
            mProviderId = providerId;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            mPhoneNumber = phoneNumber;
            return this;
        }

        public Builder setName(String name) {
            mName = name;
            return this;
        }

        public Builder setYearOfBirth(String yearOfBirth) {
            mYearOfBirth = yearOfBirth;
            return this;
        }

        public Builder setLocation(String location) {
            mLocation = location;
            return this;
        }

        public Builder setPhotoUri(Uri photoUri) {
            mPhotoUri = photoUri;
            return this;
        }

        public User build() {
            // put invalid check here

            return new User(mProviderId, mPhoneNumber, mName, mYearOfBirth, mLocation, mPhotoUri);
        }
    }
}
