package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.User.RealtimeUser;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.User.UserApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DBManager {
    public static void getUserById(String uid, final GetUserListener listener) {
        DatabaseReference onlineUserDB = FirebaseDatabase.getInstance().getReference(Define.DB_USERS);

        onlineUserDB.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserApp user = dataSnapshot.getValue(UserApp.class);
                        assert user != null;
                        listener.OnGetDataDone(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public interface GetUserListener {
        void OnGetDataDone(UserApp userApp);
    }

    public static void getRealtimeUserById(String uid, final GetRealtimeUserListener listener) {
        DatabaseReference onlineUserDB = FirebaseDatabase.getInstance().getReference(Define.DB_ONLINE_USERS);

        onlineUserDB
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RealtimeUser realtimeUser = dataSnapshot.getValue(RealtimeUser.class);
                        assert realtimeUser != null;
                        listener.OnGetDataDone(realtimeUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public interface GetRealtimeUserListener {
        void OnGetDataDone(RealtimeUser realtimeUser);
    }

}
