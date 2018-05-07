package com.example.huydaoduc.hieu.chi.hhapp.Model.Trip;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.DefineString;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Main.CalculateFare;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;

import java.util.Date;

public class TripFareInfo implements Parcelable {
    private CarType carType;

    private Float estimateFare;

    private Float duration;
    private Float distance;

    private String startTime;

    // Note: This for Null Exception
    public TripFareInfo() {
        estimateFare = 0f;
        duration = 0f;
        distance = 0f;
        carType = Define.DEFAULT_CAR_TYPE;
        startTime = TimeUtils.getCurrentTimeAsString();
    }

    public String func_getCarTypeText() {
        return DefineString.CAR_TYPE_MAP.get(carType);
    }

    public String func_getEstimateFareText() {
        return "VND " + estimateFare + "K";
    }

    public void func_RecalculateEstimateFare() {
        this.estimateFare = CalculateFare.getEstimateFare(carType,func_getStartTimeAsDate(),distance,duration)/1000;
    }

    public Date func_getStartTimeAsDate() {
        return TimeUtils.strToDate(startTime);
    }

    public String func_getDurationText() {
        return Integer.toString((int) (duration/60)) + " min";
    }

    public TripFareInfo(CarType carType, Date startTime, Float duration, Float distance) {
        this.carType = carType;
        this.startTime = TimeUtils.dateToStr(startTime);
        this.estimateFare = CalculateFare.getEstimateFare(carType,startTime,distance,duration)/1000;
        this.duration = duration;
        this.distance = distance;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public Float getEstimateFare() {
        return estimateFare;
    }

    public void setEstimateFare(Float estimateFare) {
        this.estimateFare = estimateFare;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.carType == null ? -1 : this.carType.ordinal());
        dest.writeValue(this.estimateFare);
        dest.writeValue(this.duration);
        dest.writeValue(this.distance);
        dest.writeString(this.startTime);
    }

    protected TripFareInfo(Parcel in) {
        int tmpCarType = in.readInt();
        this.carType = tmpCarType == -1 ? null : CarType.values()[tmpCarType];
        this.estimateFare = (Float) in.readValue(Float.class.getClassLoader());
        this.duration = (Float) in.readValue(Float.class.getClassLoader());
        this.distance = (Float) in.readValue(Float.class.getClassLoader());
        this.startTime = in.readString();
    }

    public static final Creator<TripFareInfo> CREATOR = new Creator<TripFareInfo>() {
        @Override
        public TripFareInfo createFromParcel(Parcel source) {
            return new TripFareInfo(source);
        }

        @Override
        public TripFareInfo[] newArray(int size) {
            return new TripFareInfo[size];
        }
    };
}
