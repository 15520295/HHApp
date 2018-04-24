package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
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

    // Convert

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


    // Func


    /**
     * Calculate time have pass from checkT
     * @return second = now - lastTime
     */
    public static long getPassTime(String lastTime) {
        long difference = getCurrentTimeAsDate().getTime() - strToDate(lastTime).getTime();
        return TimeUnit.MILLISECONDS.toSeconds(difference);
    }
}
