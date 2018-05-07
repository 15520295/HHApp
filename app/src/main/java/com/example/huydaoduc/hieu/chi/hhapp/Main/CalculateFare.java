package com.example.huydaoduc.hieu.chi.hhapp.Main;

import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;

import java.util.Date;

//todo: Huy lam
public class CalculateFare {
    public static float getEstimateFare(CarType carType, Date startTime, float distance, float duration) {

        if (carType == CarType.BIKE) {
            return getEstimateFare_Bike(startTime,distance,duration);
        } else if (carType == CarType.CAR_4) {
            return getEstimateFare_Car4(startTime,distance,duration);
        } else if (carType == CarType.CAR_7) {
            return getEstimateFare_Car7(startTime,distance,duration);
        }
        return 0;
    }

    private static float getEstimateFare_Car7(Date startTime, float distance, float duration) {
        return distance;
    }

    private static float getEstimateFare_Car4(Date startTime, float distance, float duration) {
        return distance/2;
    }

    private static float getEstimateFare_Bike(Date startTime, float distance, float duration) {

        return distance/3;
    }
}
