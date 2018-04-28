package com.example.huydaoduc.hieu.chi.hhapp;

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
    public static final String DB_ONLINE_USERS_TRIP_UID = "tripUId";
    public static final String DB_ONLINE_USERS_STATE = "userState";


    //driver
    public static final String DB_DRIVER_REQUESTS = " DriverRequests";
    //passenger
//    public static final String DB_PASSENGER_REQUESTS = "PassengerRequests";



    public static final String DB_DRIVERS = " Drivers";

    // Online User
    public static final double ONLINE_USER_RADIUS_UPDATE = 200;   // meter
    public static Float VND_PER_M = 5000f;

    // Time out
    public static final long ONLINE_USER_TIMEOUT = 10*60;      // second
    public static final long DRIVER_REQUESTS_TIMEOUT = 10*60;


    // Fare
    public static final float FARE_VND_PER_M = 5000;


}
