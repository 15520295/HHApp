package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeManager {
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTimeAsString() {
        Calendar c = Calendar.getInstance();
        return  simpleDateFormat.format(c.getTime());
    }

    public static Date getCurrentTimeAsDate() {
        Calendar c = Calendar.getInstance();
        return  c.getTime();
    }

    public static Date strToDate(String string) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(simpleDateFormat.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return cal.getTime();
    }

    public static String dateToStr(Date date) {
        return simpleDateFormat.format(date);
    }
}
