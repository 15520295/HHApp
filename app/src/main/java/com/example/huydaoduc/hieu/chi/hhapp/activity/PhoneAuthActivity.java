package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneAuthActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    EditText et_phone_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        Init();
    }

    private void Init() {
        // Init Views
        et_phone_number = findViewById(R.id.et_phone_number);

        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Events
        et_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneAuthActivity.this, EnterPhoneNumberActivity.class);
                PhoneAuthActivity.this.startActivity(intent);
            }
        });
    }
}
