package com.uit.huydaoduc.hieu.chi.hhapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckInternetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        if (isNetworkAvailable(context, type)) {
            return;
        } else {
            Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show();
        }

    }

    public static boolean isNetworkAvailable(Context context, int[] typeNetworks) {
        try {
            ConnectivityManager cn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int typeNetwork : typeNetworks) {
                NetworkInfo info = cn.getNetworkInfo(typeNetwork);
                if (info != null && info.isConnectedOrConnecting()) {
                    return true;

                }
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
