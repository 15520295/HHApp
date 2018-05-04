package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class MainActivity extends AppCompatActivity {
    FloatingTextButton btn_rider, btn_driver;
    EditText edtEmail, edtPassword, edtName, edtPhone;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    public static String phoneNumber;
    public static String nameUser;
    public static String id;
    public static String avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        initView();
        loadInfor();

        addEven();
    }

    private void addEven() {

        btn_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DriverActivity.class);
                startActivity(intent);
            }
        });


        btn_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        btn_rider = findViewById(R.id.btnpass);
        btn_driver = findViewById(R.id.btndriver);
    }
    private void loadInfor(){

        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        nameUser = dataSnapshot.child("name").getValue().toString();
                        phoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                        avatar = dataSnapshot.child("avatar").getValue().toString();


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


    }

}


