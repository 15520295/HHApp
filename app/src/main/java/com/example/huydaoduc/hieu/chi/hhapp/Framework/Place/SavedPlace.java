package com.example.huydaoduc.hieu.chi.hhapp.Framework.Place;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.google.android.gms.maps.model.LatLng;

public class SavedPlace implements Parcelable {
    private String primaryText;
    private String address;
    private String id;
    private String location;        // LatLng

    public SavedPlace() { }

    public LatLng func_getLatLngLocation() {
        if (location == null) {
            Log.e("SavePlace", "location null");
            return new LatLng(0f, 0f);
        }
        return LocationUtils.strToLatLng(location);
    }

    // getter & setter

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.primaryText);
        dest.writeString(this.address);
        dest.writeString(this.id);
        dest.writeString(this.location);
    }

    protected SavedPlace(Parcel in) {
        this.primaryText = in.readString();
        this.address = in.readString();
        this.id = in.readString();
        this.location = in.readString();
    }

    public static final Parcelable.Creator<SavedPlace> CREATOR = new Parcelable.Creator<SavedPlace>() {
        @Override
        public SavedPlace createFromParcel(Parcel source) {
            return new SavedPlace(source);
        }

        @Override
        public SavedPlace[] newArray(int size) {
            return new SavedPlace[size];
        }
    };
}
