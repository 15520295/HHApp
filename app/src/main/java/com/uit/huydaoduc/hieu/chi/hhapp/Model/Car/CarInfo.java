package com.uit.huydaoduc.hieu.chi.hhapp.Model.Car;

public class CarInfo {

    private String carName;
    private String carId;
    private CarType carType;

    public CarInfo() {
    }

    public CarInfo(String carName, String carId, CarType carType) {
        this.carName = carName;
        this.carId = carId;
        this.carType = carType;
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
}
