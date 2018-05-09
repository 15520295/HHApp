package com.example.huydaoduc.hieu.chi.hhapp;

import android.util.Pair;

import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Define {
    // Map setup
    public static final int POLLING_FREQ_MILLI_SECONDS = 5000;
    public static final int FASTEST_UPDATE_FREQ_MILLI_SECONDS = 3000;

    // Search Activity - AutoComplete
    public static final int RADIUS_AUTO_COMPLETE = 1000;

    // Camera Manager
    public static final int MAP_BOUND_ROUTE_PADDING = 160;  /* In dp */
    public static final float MAP_BOUND_POINT_ZOOM = 14.5f;

    // --------- DataBase
    // Trips
    public static final String DB_TRIPS = "Trips";
    public static final String DB_TRIPS_TRIP_STATE = "tripState";
    public static final String DB_TRIPS_DRIVER_UID = "driverUId";

    //
    public static final String DB_USERS_INFO = "UsersInfo";

    // Online User
    public static final String DB_ONLINE_USERS = " OnlineUsers";
    public static final String DB_ONLINE_USERS_NOTIFY_TRIP = "notifyTrip";
    public static final String DB_ONLINE_USERS_STATE = "userState";


    //driver
    public static final String DB_ROUTE_REQUESTS = " RouteRequests";
    public static final String DB_ROUTE_REQUESTS_NOTIFY_TRIP = "notifyTrip";
    public static final String DB_ROUTE_REQUESTS_ROUTE_REQUEST_STATE = "routeRequestState";

//    public static final String DB_ROUTE_REQUESTS_TRIPUID = "tripUId";

    //passenger
//    public static final String DB_PASSENGER_REQUESTS = "PassengerRequests";



    public static final String DB_DRIVERS = " Drivers";

    // Online User
    public static final double ONLINE_USER_RADIUS_UPDATE = 200;   // meter
    public static Float VND_PER_M = 5000f;

    // Time out
    public static final long ONLINE_USER_TIMEOUT = 10*60;      // second
    public static final long DRIVER_REQUESTS_TIMEOUT = 10*60;


    // CarInfo
    public static final CarType DEFAULT_CAR_TYPE = CarType.BIKE;
    public static final HashMap<CarType,Integer> CAR_TYPE_ICON_MAP = new HashMap<CarType, Integer>() {{
        put(CarType.BIKE, R.drawable.ic_bike_20);
        put(CarType.CAR_4,R.drawable.ic_car_20);
        put(CarType.CAR_7,R.drawable.ic_car_20);
    }};

    //sale off
    public static float SALE_OFF_ESTIMATEFARE = 0.5f; //50%

}
