package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.animation.TimeInterpolator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.Main.Home;
import com.example.huydaoduc.hieu.chi.hhapp.Main.MainActivity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class EnterPhoneNumberActivity extends AppCompatActivity {

    private static final String TAG = "EnterPhoneNumberAct";
    FloatingActionButton fab;
    private TextView tv_connect_social, tv_error;
    private EditText et_phone_number;
    private ProgressDialog dialog;

    private FirebaseAuth firebaseAuth;
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

        ConstraintLayout root = findViewById(R.id.root);

        ConstraintSet constraintSetTo = new ConstraintSet();
        constraintSetTo.clone(this,R.layout.activity_enter_phone_number);

        TransitionSet transitionSet = new TransitionSet(){
            {
                setDuration(500);
                setOrdering(ORDERING_TOGETHER);
                addTransition(new ChangeBounds() {
                    {
                        addTransition(new Fade(Fade.OUT));
                        addTransition(new ChangeBounds());
                        addTransition(new Fade(Fade.IN));
                        setInterpolator(new FastOutSlowInInterpolator());   // notice
                    }
                });

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
        overridePendingTransition(R.anim.anim_activity_none, R.anim.anim_fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideLoading();
    }

    private void AnimationIn()
    {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim_fade_out);
        animation.reset();
        tv_connect_social.clearAnimation();
        tv_connect_social.startAnimation(animation);
    }

    private void AnimationOut() {
        Animation fab_out_anim = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        fab_out_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openVerifyActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab_out_anim.reset();
        fab.clearAnimation();
        fab.startAnimation(fab_out_anim);
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

        // Init Views
        tv_connect_social = findViewById(R.id.tv_connect_social);
        tv_error = findViewById(R.id.tv_error);
        tv_error.setText("");
        et_phone_number = findViewById(R.id.et_phone_number);

        // Events
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_sms();
            }
        });

        (findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                                    final String uid = task.getResult().getUser().getUid();

                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            if (snapshot.hasChild(uid)) {
                                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                                EnterPhoneNumberActivity.this.startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), UpdateInfoActivity.class);
                                                EnterPhoneNumberActivity.this.startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        });

            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
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
                hideLoading();
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

    private void showLoading() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
    }

    private void hideLoading() {
        // hide showLoading dialog
        if (dialog != null) {
            if(dialog.isShowing())
                dialog.hide();
        }
    }

    public void send_sms()
    {
        // check invalid number
        if (TextUtils.isEmpty(et_phone_number.getText())) {
            tv_error.setText("Please enter your phone number");
            return;
        }
        showLoading();

        String user_number =  et_phone_number.getText().toString();
        if (user_number.charAt(0) == '0') {
            user_number = new StringBuilder(user_number).deleteCharAt(0).toString();
        }

        String number = "+84" + user_number;
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
        intent.putExtra("verify_code", verification_code);
        intent.putExtra("phone_number", et_phone_number.getText());
        this.startActivity(intent);
    }

    private void showKeyBoard() {
        et_phone_number.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_phone_number, InputMethodManager.SHOW_IMPLICIT);
    }

}
