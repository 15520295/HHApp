package com.uit.huydaoduc.hieu.chi.hhapp.Main;

import android.content.Intent;

import com.uit.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.UpdateInfoActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurUserInfo {
    private static CurUserInfo curUserInfo;

    private static UserInfo userInfo;

    public static CurUserInfo getInstance() {
        if (curUserInfo == null) {
            curUserInfo = new CurUserInfo();
        }
        return curUserInfo;
    }

    public UserInfo getUserInfo() {
        if (userInfo == null) {
            FirebaseDatabase.getInstance().getReference().child(Define.DB_USERS_INFO)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userInfo = dataSnapshot.getValue(UserInfo.class);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        return userInfo;    // can be null
    }

    public UserInfo getUserInfo(GetUserInfoListener listener) {
        if (userInfo == null) {
            FirebaseDatabase.getInstance().getReference().child(Define.DB_USERS_INFO)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userInfo = dataSnapshot.getValue(UserInfo.class);

                            listener.OnGetDone(userInfo);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        return userInfo;
    }

    public interface GetUserInfoListener {
        void OnGetDone(UserInfo userInfo);
    }


    public void setUserInfo(UserInfo userInfo) {
        CurUserInfo.userInfo = userInfo;
    }
}
