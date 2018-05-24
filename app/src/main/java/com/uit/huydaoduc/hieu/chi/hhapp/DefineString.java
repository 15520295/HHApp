package com.uit.huydaoduc.hieu.chi.hhapp;


import android.content.Context;
import android.util.Pair;

import com.uit.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.R;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class DefineString {
    private static Context context;

    public static void setContext(Context context) {
        DefineString.context = context;
        initDefineString();
    }

    // Route Request State
    public static HashMap<RouteRequestState,String> ROUTE_REQUEST_STATE_MAP;

    // Passenger Request State
    public static HashMap<PassengerRequestState,String> PASSENGER_REQUEST_STATE_MAP;

    // Wait time
    public static Pair<String, Integer> DEFAULT_WAIT_TIME;
    public static LinkedHashMap<String,Integer> WAIT_TIME_MAP;

    // Car type
    public static LinkedHashMap<CarType,String> CAR_TYPE_MAP;

    // Note
    public static String NOTES_TO_DRIVER_TITLE;
    public static String NOTES_TO_DRIVER_HINT;

    private static void initDefineString() {
        ROUTE_REQUEST_STATE_MAP = new HashMap<RouteRequestState, String>() {{
            put(RouteRequestState.FINDING_PASSENGER, getStringById(R.string.finding_passenger));
            put(RouteRequestState.PAUSE, getStringById(R.string.pause_finding));
            put(RouteRequestState.FOUND_PASSENGER, getStringById(R.string.click_to_see_passenger_info));
            put(RouteRequestState.TIME_OUT, getStringById(R.string.time_out));
        }};
        PASSENGER_REQUEST_STATE_MAP = new HashMap<PassengerRequestState, String>() {{
            put(PassengerRequestState.FINDING_DRIVER, getStringById(R.string.finding_driver));
            put(PassengerRequestState.PAUSE, getStringById(R.string.pause_finding));
            put(PassengerRequestState.FOUND_DRIVER,getStringById(R.string.click_to_see_driver_info));
            put(PassengerRequestState.TIME_OUT, getStringById(R.string.time_out));
        }};
        DEFAULT_WAIT_TIME = new Pair<>(getStringById(R.string.how_long_can_you_wait), 10);
        WAIT_TIME_MAP = new LinkedHashMap<String, Integer>() {{
            put( getStringById(R.string.min_5) , 5);
            put( getStringById(R.string.min_10), 10);
            put( getStringById(R.string.min_20), 20);
            put( getStringById(R.string.min_30), 30);
        }};
        CAR_TYPE_MAP = new LinkedHashMap<CarType, String>() {{
            put(CarType.BIKE, getStringById(R.string.BIKE));
            put(CarType.CAR_4, getStringById(R.string.CAR_4));
            put(CarType.CAR_7, getStringById(R.string.CAR_7));
        }};

        NOTES_TO_DRIVER_TITLE = getStringById(R.string.note_to_driver);
        NOTES_TO_DRIVER_HINT = getStringById(R.string.note_to_driver_hint);
    }

    private static String getStringById(int id) {
        return context.getResources().getString(id);
    }




}
