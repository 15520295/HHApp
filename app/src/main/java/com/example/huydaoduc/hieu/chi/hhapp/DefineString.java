package com.example.huydaoduc.hieu.chi.hhapp;

import android.util.Pair;

import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class DefineString {
    // Route Request State
    public static final HashMap<RouteRequestState,String> ROUTE_REQUEST_STATE_MAP = new HashMap<RouteRequestState, String>() {{
        put(RouteRequestState.FINDING_PASSENGER,"Finding passenger...");
        put(RouteRequestState.PAUSE,"Pause finding");
        put(RouteRequestState.FOUND_PASSENGER,"Found your passenger");
    }};

    // Passenger Request State
    public static final HashMap<PassengerRequestState,String> PASSENGER_REQUEST_STATE_MAP = new HashMap<PassengerRequestState, String>() {{
        put(PassengerRequestState.FINDING_DRIVER,"Finding driver ...");
        put(PassengerRequestState.PAUSE,"Pause");
        put(PassengerRequestState.FOUND_DRIVER,"Found your driver");
    }};

    // Wait time
    public static final Pair<String, Integer> DEFAULT_WAIT_TIME = new Pair<>("How long can you wait the driver?", 5);
    public static final LinkedHashMap<String,Integer> WAIT_TIME_MAP = new LinkedHashMap<String, Integer>() {{
        put("5 minute", 5);
        put("10 minute", 10);
        put("20 minute", 20);
        put("30 minute", 30);
    }};

    // Car type
    public static final LinkedHashMap<CarType,String> CAR_TYPE_MAP = new LinkedHashMap<CarType, String>() {{
        put(CarType.BIKE, "BIKE");
        put(CarType.CAR_4,"4 SEAT CAR");
        put(CarType.CAR_7,"7 SEAT CAR");
    }};

    // Note
    public static final String NOTES_TO_DRIVER_TITLE = "Notes to Driver";
    public static final String NOTES_TO_DRIVER_HINT = "I'm standing at the entrance";


}
