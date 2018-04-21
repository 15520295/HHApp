package com.example.huydaoduc.hieu.chi.hhapp;

public class Define {
    // Map setup
    public static final int POLLING_FREQ_MILLI_SECONDS = 5000;
    public static final int FASTEST_UPDATE_FREQ_MILLI_SECONDS = 5000;

    // Search Activity - AutoComplete
    public static final int RADIUS_AUTO_COMPLETE = 1000;

    // DataBase
    public static final String DB_USERS = "Users";
    public static final String DB_ONLINE_USERS = " OnlineUsers";
    public static final String DB_DRIVERS = " Drivers";
    public static final String DB_ROUTEREQUESTS = " RouteRequests";

    // Realtime User
    public static final long REALTIME_USER_TIMEOUT = 3*30;      // second
    public static final double REALTIME_USER_RADIUS_UPDATE = 200;   // meter
}
