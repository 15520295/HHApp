package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat userDateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy");

    public static final SimpleDateFormat userTimeFormat = new SimpleDateFormat("HH:mm");

    public static String getCurrentTimeAsString() {
        Calendar c = Calendar.getInstance();
        return  userDateFormat.format(c.getTime());
    }

    public static Date getCurrentTimeAsDate() {
        Calendar c = Calendar.getInstance();
        return  c.getTime();
    }

    // Convert

    public static Date strToDate(String string) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(userDateFormat.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return cal.getTime();
    }

    public static String dateToStr(Date date) {
        return userDateFormat.format(date);
    }

    /**
     * User string mean a String of date that User can easily read
     */
    public static String dateToUserStr(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year,month,day);
        return userDateFormat.format(cal.getTime());
    }
    public static String curDateToUserStr() {
        Calendar cal = Calendar.getInstance();
        return userDateFormat.format(cal.getTime());
    }

    public static String timeToUserString(int hourOfDay, int minute) {
        return String.format("%02d:%02d",hourOfDay,minute);
    }
    public static String curTimeToUserString() {
        Calendar cal = Calendar.getInstance();
        return userTimeFormat.format(cal.getTime());
    }
    // Func


    /**
     * Calculate time have pass from checkT
     * @return second = now - lastTime
     */
    public static long getPassTime(String lastTime) {
        long difference = getCurrentTimeAsDate().getTime() - strToDate(lastTime).getTime();
        return TimeUnit.MILLISECONDS.toSeconds(difference);
    }


    public static boolean checkTimeOut(long checkAmountSec, String lastTimeCheck) {
        long secondsPass = TimeUtils.getPassTime(lastTimeCheck);

        if( secondsPass > checkAmountSec)
            return true;
        return false;
    }
}
