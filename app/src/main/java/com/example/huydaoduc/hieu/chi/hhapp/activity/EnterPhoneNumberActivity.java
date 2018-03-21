package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class EnterPhoneNumberActivity extends AppCompatActivity {

    TextView tv_connect_social;
    EditText et_phone_number;
    RelativeLayout rootLayout;

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    String verification_code;


    @Override
    public void finish() {
        super.finish();

        // turn Activity transfer Animation off
        overridePendingTransition(R.anim.anim_activity_none, R.anim.anim_activity_none);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void AnimationIn()
    {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim_fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                et_phone_number.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_phone_number, InputMethodManager.SHOW_IMPLICIT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.reset();
        tv_connect_social.clearAnimation();
        tv_connect_social.startAnimation(animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_activity_none, R.anim.anim_activity_none);
        setContentView(R.layout.activity_enter_phone_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Init();
        AnimationIn();
    }

    private void Init() {
        // Init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Firebase Events
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Snackbar.make(rootLayout, "onVerificationCompleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Snackbar.make(rootLayout, "Fail:" + e.getMessage().toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                et_phone_number.setText(e.getMessage().toString());

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification_code = s;
                openVerifyActivity();
            }
        };

        // Init Views
        tv_connect_social = findViewById(R.id.tv_connect_social);
        et_phone_number = findViewById(R.id.et_phone_number);
        rootLayout = findViewById(R.id.rootLayout);

        // Events
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "fab click", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                send_sms();
            }
        });
    }

    public void send_sms()
    {
        String number = "+84" + et_phone_number.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );
    }

    private void openVerifyActivity()
    {
        Intent intent = new Intent(getApplicationContext(),VerifyPhoneActivity.class);
        intent.putExtra("verify_code",verification_code);
        this.startActivity(intent);
    }

}
