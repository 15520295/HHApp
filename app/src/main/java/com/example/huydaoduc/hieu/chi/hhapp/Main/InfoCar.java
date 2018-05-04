package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoCar extends AppCompatActivity {

    EditText edtCarInfo, edtCarNumber;
    Button btnSave;
    private DatabaseReference mDatabase;
    String carInfo = "", carNumber = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_car);

        edtCarInfo = findViewById(R.id.edtcarinfor);
        edtCarNumber = findViewById(R.id.edtcarnumber);
        btnSave = findViewById(R.id.btnsave);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        carInfo = edtCarInfo.getText().toString();
        carNumber = edtCarNumber.getText().toString();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carInfo.equals("")||carNumber.equals("")){
                    Toast.makeText(InfoCar.this, "Bạn chưa nhập đủ thông tin", Toast.LENGTH_LONG).show();
                }
                else {
                    mDatabase.child("Users").child(MainActivity.id).child("carinfo").setValue(carInfo);
                    mDatabase.child("Users").child(MainActivity.id).child("carnumber").setValue(carNumber);
                }
            }
        });








    }
}
