package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeName extends Activity {

    EditText editText;
    Button button;
    private DatabaseReference mDatabase;
    public static String newName="";

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

//todo: handle putExtra

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        editText = findViewById(R.id.edtchange);
        button = findViewById(R.id.btnchange);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int heigh = dm.heightPixels;
        getWindow().setLayout((int)(width*.8), (int)(heigh*.3));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(getCurUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                        userInfo.setName(dataSnapshot.child("name").getValue().toString());

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        editText.setText(userInfo.getName());



//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                newName = editText.getText().toString();
//                AboutUser.txtNameUser.setText(newName);
//                mDatabase.child("Users").child(MainActivity.id).child("name").setValue(newName);
//                DriverActivity.txtName.setText("Name: " + newName);
//                finish();
//
//
//            }
//
//        });


    }
}
