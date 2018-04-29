package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.google.firebase.database.FirebaseDatabase;

public class CheckActivityCloseService extends Service {

    private String uid;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uid =  intent.getStringExtra("uid");

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        FirebaseDatabase.getInstance().getReference().child(Define.DB_ROUTE_REQUESTS).child(uid).removeValue();
        FirebaseDatabase.getInstance().getReference().child(Define.DB_ONLINE_USERS).child(uid).removeValue();

        super.onDestroy();
    }
}
