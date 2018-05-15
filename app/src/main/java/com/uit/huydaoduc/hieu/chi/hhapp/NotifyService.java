package com.uit.huydaoduc.hieu.chi.hhapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.uit.huydaoduc.hieu.chi.hhapp.Main.Driver.PassengerRequestInfoActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.MainActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class NotifyService extends Service {

    private static final String TAG = "Notify";

    private static final long UPDATE_INTERVAL = 1000 * 60 * 60 * 24;

    private Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // we shedule task "showNotification" to run everyday.
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        //showNotification();
                    }
                },
                0,
                UPDATE_INTERVAL);
    }


    public void showNotificationforDriver(UserInfo userInfo, Trip trip) {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_location_on)
                        .setContentTitle("Found Passenger SS")
                        .setContentText("Click to show info Passenger");

        Intent resultIntent = new Intent(getApplicationContext(), PassengerRequestInfoActivity.class);

        resultIntent.putExtra("trip", trip);
        resultIntent.putExtra("userInfo", userInfo);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

//                        Uri newSound= Uri.parse("android.resource://"
//                                + getPackageName() + "/" + R.raw.gaugau);
//                        mBuilder.setSound(newSound);

        int mNotificationId = 0;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public void showNotificationforPassenger(Context context, UserInfo driverInfo) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_location_on)
                        .setContentTitle(driverInfo.getName())
                        .setContentText(driverInfo.getPhoneNumber())
                        .setPriority(2);

        Intent resultIntent = new Intent(context, PassengerActivity.class);

        resultIntent.putExtra("userInfo", driverInfo);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

//                        Uri newSound= Uri.parse("android.resource://"
//                                + getPackageName() + "/" + R.raw.gaugau);
//                        mBuilder.setSound(newSound);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];
// Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
// Moves events into the expanded layout
        for (int i = 0; i < events.length; i++) {

            inboxStyle.addLine(events[i]);
        }
// Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        int mNotificationId = 1;
        // Gets an instance of the NotificationManager service
        Log.d(TAG, driverInfo.getName());
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void showNotificationAfterTime(Time time) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_location_on)
                        .setContentTitle("Found Driver SS")
                        .setContentText("Click to show info Driver")
                        .setPriority(2);

        Intent resultIntent = new Intent(getApplicationContext(), PassengerActivity.class);

        resultIntent.putExtra("userInfo", time);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

//                        Uri newSound= Uri.parse("android.resource://"
//                                + getPackageName() + "/" + R.raw.gaugau);
//                        mBuilder.setSound(newSound);

        int mNotificationId = 1;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
