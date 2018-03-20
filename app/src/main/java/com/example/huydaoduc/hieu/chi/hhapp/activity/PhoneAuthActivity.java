package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class PhoneAuthActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    EditText et_phone_number;
    TextView tv_connect_social;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        Init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        AnimationIn();
    }

    private void AnimationOut()
    {
        Animation anim_fadeOut = AnimationUtils.loadAnimation(this,R.anim.anim_fade_out);
        anim_fadeOut.reset();
        tv_connect_social.clearAnimation();
        tv_connect_social.startAnimation(anim_fadeOut);
    }

    private void AnimationIn()
    {
        Animation anim_fadeIn = AnimationUtils.loadAnimation(this,R.anim.anim_fade_in);
        anim_fadeIn.reset();
        tv_connect_social.clearAnimation();
        tv_connect_social.startAnimation(anim_fadeIn);
    }

    private void Init() {
        // Init Views
        et_phone_number = findViewById(R.id.et_phone_number);
        tv_connect_social = findViewById(R.id.tv_connect_social);
        tv_connect_social.setEnabled(false);

        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Events
        et_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationOut();
                Intent intent = new Intent(PhoneAuthActivity.this, EnterPhoneNumberActivity.class);
                PhoneAuthActivity.this.startActivity(intent);
            }
        });
    }
}
