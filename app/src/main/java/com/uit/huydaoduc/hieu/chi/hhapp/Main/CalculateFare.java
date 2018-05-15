package com.uit.huydaoduc.hieu.chi.hhapp.Main;

import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;

import java.util.Calendar;
import java.util.Date;

//todo: Huy lam
public class CalculateFare {
    public static float getEstimateFare(CarType carType, Date startTime, float distance, float duration) {
        // duration tinh theo giay

        if (carType == CarType.BIKE) {
            return getEstimateFare_Bike(startTime, distance, duration) * 1000 * Define.SALE_OFF_ESTIMATEFARE;
        } else if (carType == CarType.CAR_4) {
            return getEstimateFare_Car4(startTime, distance, duration) * 1000 * Define.SALE_OFF_ESTIMATEFARE;
        } else if (carType == CarType.CAR_7) {
            return getEstimateFare_Car7(startTime, distance, duration) * 1000 * Define.SALE_OFF_ESTIMATEFARE;
        }
        return 0;
    }

    private static float getEstimateFare_Car7(Date startTime, float distance, float duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int h = calendar.get(Calendar.HOUR_OF_DAY);

        float distance_km = distance / 1000;
        float duration_gio = duration / 60;

        if (h >= 0 && h <= 5) {
            float price = 11 * distance_km + 0.3f * duration_gio + 10;
            return price;
        } else {
            float price = 11 * distance_km + 0.3f * duration_gio;
            return price;
        }
    }

    private static float getEstimateFare_Car4(Date startTime, float distance, float duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int h = calendar.get(Calendar.HOUR_OF_DAY);

        float distance_km = distance / 1000;
        float duration_gio = duration / 60;

        if (h >= 0 && h <= 5) {
            float price = 9 * distance_km + 0.3f * duration_gio + 10;
            return price;
        } else {
            float price = 9 * distance_km + 0.3f * duration_gio;
            return price;
        }
    }

    private static float getEstimateFare_Bike(Date startTime, float distance, float duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int h = calendar.get(Calendar.HOUR_OF_DAY);

        float distance_km = distance / 1000;

        if (h >= 0 && h <= 5) {
            if (distance_km <= 2) {
                float price = 12 + 10;
                return price;
            } else {
                float price = 12 + (distance_km - 2) * 3.8f + 10;
                return price;
            }
        } else {
            if (distance_km <= 2) {
                float price = 12;
                return price;
            } else {
                float price = 12 + (distance_km - 2) * 3.8f;
                return price;
            }
        }

    }
}
