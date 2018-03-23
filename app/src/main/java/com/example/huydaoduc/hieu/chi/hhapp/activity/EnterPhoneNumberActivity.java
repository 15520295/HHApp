package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class EnterPhoneNumberActivity extends AppCompatActivity {

    private static final String TAG = "EnterPhoneNumberAct";
    private TextView tv_connect_social, tv_error;
    private EditText et_phone_number;
    private RelativeLayout rootLayout;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verification_code;


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
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Init Views
        tv_connect_social = findViewById(R.id.tv_connect_social);
        tv_error = findViewById(R.id.tv_error);
        et_phone_number = findViewById(R.id.et_password);
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

        // Firebase Events
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.w(TAG, "onVerificationCompleted");
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                // ---> Sign In right away and sent to uid to EnterPass
                firebaseAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    // Get User Firebase id
                                    String uid = task.getResult().getUser().getUid();

                                    Intent intent = new Intent(getApplicationContext(),EnterPassActivity.class);
                                    intent.putExtra("uid",uid);
                                    EnterPhoneNumberActivity.this.startActivity(intent);
                                }
                            }
                        });

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                tv_error.setText(e.getMessage());
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    tv_error.setText("Failed to send sms to your number. Please Try again!");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    tv_error.setText("The server storage quota has been exceeded. Please go back other time.");
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verification_code);

                // Save verification ID and resending token so we can use them later
                verification_code = s;

                openVerifyActivity();
            }
        };
    }

    public void send_sms()
    {
        //todo: check invalid number
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
