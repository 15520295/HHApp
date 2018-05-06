package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;

public class CarTypeInfo {
    private CarType carType;
    private String estimateFareText;
    private String durationText;

    public CarTypeInfo(CarType carType, String estimateFareText, String durationText) {
        this.carType = carType;
        this.estimateFareText = estimateFareText;
        this.durationText = durationText;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public String getEstimateFareText() {
        return estimateFareText;
    }

    public void setEstimateFareText(String estimateFareText) {
        this.estimateFareText = estimateFareText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }
}
