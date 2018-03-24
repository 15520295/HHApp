package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeScroll;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.MainActivity;
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
    private ConstraintLayout root;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verification_code;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ConstrainAnimation();
        showKeyBoard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_activity_none, R.anim.anim_activity_none);
        setContentView(R.layout.activity_enter_phone_number_e);
        Init();
    }


    private void ConstrainAnimation() {
//        TransitionManager.beginDelayedTransition(mConstraintLayout);
//        if (mOld = !mOld) {
//            mConstraintSet1.applyTo(mConstraintLayout); // set new constraints
//        }  else {
//            mConstraintSet2.applyTo(mConstraintLayout); // set new constraints
//        }

//        TimeInterpolator timeInterpolator = new AnticipateOvershootInterpolator(1.0f);
//        Transition transition = null;
//
//
//        transition = new ChangeBounds();
//        transition.setInterpolator(timeInterpolator);
//        transition.setDuration(1000);

        ConstraintLayout root = findViewById(R.id.root);

        ConstraintSet constraintSetTo = new ConstraintSet();
        constraintSetTo.clone(this,R.layout.activity_enter_phone_number);

        TransitionSet transitionSet = new TransitionSet(){
            {
                setDuration(500);
                setOrdering(ORDERING_TOGETHER);
                addTransition(new TransitionSet() {
                    {
                        addTransition(new Fade(Fade.OUT));
                        addTransition(new ChangeBounds());
                        addTransition(new Fade(Fade.IN));
                    }
                });
                setInterpolator(new AnticipateOvershootInterpolator());

            }

            @Override
            public TransitionSet addListener(TransitionListener listener) {
                listener.onTransitionEnd(this);
                return super.addListener(listener);
            }
        };

        transitionSet.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                showKeyBoard();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

        TransitionManager.beginDelayedTransition(root, transitionSet);
        constraintSetTo.applyTo(root);

    }

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

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.reset();
        tv_connect_social.clearAnimation();
        tv_connect_social.startAnimation(animation);
    }



    private void Init() {
        ((TextView) findViewById(R.id.tv_connect_social))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConstrainAnimation();

                    }
                });
        // Init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Init Views
        tv_connect_social = findViewById(R.id.tv_connect_social);
        tv_error = findViewById(R.id.tv_error);
        et_phone_number = findViewById(R.id.et_password);
        root = findViewById(R.id.root);

        // Events
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    EnterPhoneNumberActivity.this.startActivity(intent);
                                }
                            }
                        });

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e.getMessage().toLowerCase().contains("network")) {
                    tv_error.setText("Can't connect to the network. Please check your connection");
                }
                else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    tv_error.setText("Failed to send sms to your number. Please Try again!");
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    tv_error.setText("The server storage quota has been exceeded. Please go back other time.");
                }
                else
                {
                    tv_error.setText(e.getMessage());
                }
                tv_error.setVisibility(View.VISIBLE);
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

    private void showKeyBoard() {
        et_phone_number.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_phone_number, InputMethodManager.SHOW_IMPLICIT);
    }

}
