package com.example.huydaoduc.hieu.chi.hhapp.Framework;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.OnlineUser;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DBManager {
    public static void getUserById(String uid, final GetUserListener listener) {
        DatabaseReference onlineUserDB = FirebaseDatabase.getInstance().getReference(Define.DB_USERS_INFO);

        onlineUserDB.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserInfo user = dataSnapshot.getValue(UserInfo.class);
                        assert user != null;
                        listener.OnGetDataDone(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public interface GetUserListener {
        void OnGetDataDone(UserInfo userInfo);
    }

    public static void getOnlineUserById(String uid, final GetOnlineUserListener listener) {
        DatabaseReference onlineUserDB = FirebaseDatabase.getInstance().getReference(Define.DB_ONLINE_USERS);

        onlineUserDB
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        OnlineUser onlineUser = dataSnapshot.getValue(OnlineUser.class);
                        assert onlineUser != null;
                        listener.OnGetDataDone(onlineUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public interface GetOnlineUserListener {
        void OnGetDataDone(OnlineUser onlineUser);
    }

    public static void getTripById(String tripUId, final GetTripListener listener) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(Define.DB_TRIPS);

        db
                .child(tripUId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Trip trip = dataSnapshot.getValue(Trip.class);
                        assert trip != null;
                        listener.OnGetDataDone(trip);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public interface GetTripListener {
        void OnGetDataDone(Trip trip);
    }

}
