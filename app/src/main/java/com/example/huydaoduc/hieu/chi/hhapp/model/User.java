package com.example.huydaoduc.hieu.chi.hhapp.model;

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
    private String mPassword;
    private String mPhoneNumber;
    private String mName;
    private String mDateOfBirth;
    private Uri mPhotoUri;
    public User() {
    }

    private User(String providerId, String password, String phoneNumber, String name, String dateOfBirth, Uri photoUri) {
        mProviderId = providerId;
        mPassword = password;
        mPhoneNumber = phoneNumber;
        mName = name;
        mDateOfBirth = dateOfBirth;
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
    public String getPassword() {
        return mPassword;
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getDateOfBirth() {
        return mDateOfBirth;
    }

    @Nullable
    public Uri getPhotoUri() {
        return mPhotoUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return mProviderId.equals(user.mProviderId)
                && (mPassword == null ? user.mPassword == null : mPassword.equals(user.mPassword))
                && (mPhoneNumber == null ? user.mPhoneNumber == null : mPhoneNumber.equals(user.mPhoneNumber))
                && (mName == null ? user.mName == null : mName.equals(user.mName))
                && (mDateOfBirth == null ? user.mDateOfBirth == null : mDateOfBirth.equals(user.mDateOfBirth))
                && (mPhotoUri == null ? user.mPhotoUri == null : mPhotoUri.equals(user.mPhotoUri));
    }

    @Override
    public int hashCode() {
        int result = mProviderId.hashCode();
        result = 31 * result + (mPassword == null ? 0 : mPassword.hashCode());
        result = 31 * result + (mPhoneNumber == null ? 0 : mPhoneNumber.hashCode());
        result = 31 * result + (mName == null ? 0 : mName.hashCode());
        result = 31 * result + (mDateOfBirth == null ? 0 : mDateOfBirth.hashCode());
        result = 31 * result + (mPhotoUri == null ? 0 : mPhotoUri.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "mProviderId='" + mProviderId + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mName='" + mName + '\'' +
                ", mDateOfBirth='" + mDateOfBirth + '\'' +
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
        dest.writeString(mPassword);
        dest.writeString(mPhoneNumber);
        dest.writeString(mName);
        dest.writeString(mDateOfBirth);
        dest.writeParcelable(mPhotoUri, flags);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static class Builder {
        private String mProviderId;
        private String mPassword;
        private String mPhoneNumber;
        private String mName;
        private String mDateOfBirth;
        private Uri mPhotoUri;

        public Builder( @NonNull String providerId,
                       @NonNull String password) {
            mProviderId = providerId;
            mPassword = password;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            mPhoneNumber = phoneNumber;
            return this;
        }

        public Builder setName(String name) {
            mName = name;
            return this;
        }

        public Builder setDateOfBirth(String dateOfBirth) {
            mDateOfBirth = dateOfBirth;
            return this;
        }

        public Builder setPhotoUri(Uri photoUri) {
            mPhotoUri = photoUri;
            return this;
        }

        public User build() {
            // put invalid check here

            return new User(mProviderId, mPassword, mPhoneNumber, mName, mDateOfBirth, mPhotoUri);
        }
    }
}
