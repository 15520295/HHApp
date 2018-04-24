package com.example.huydaoduc.hieu.chi.hhapp.Model.User;

public enum UserState {
    OFFLINE,

    // for driver only
    D_RECEIVING_BOOKING_HH,        // driver wait for hh
    D_RECEIVING_BOOKING,

    D_WAITING_FOR_ACCEPT,


    // for passenger only
    P_FINDING_DRIVER

}
