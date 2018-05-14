package com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
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
import com.google.gson.internal.LinkedHashTreeMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

//todo: resent code
public class VerifyPhoneActivity extends AppCompatActivity {
    private static final String TAG = "VerifyPhoneAct";
    private static boolean isTimerRunning = false;
    FloatingActionButton fab;
    private TextView tv_error, tv_phone_number, tv_resend_code;
    private Map<Integer,EditText> editTextMap;
    private RelativeLayout rootLayout;
    private FirebaseAuth firebaseAuth;

    private String verify_code;
    private String phone_number;
    PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private ProgressDialog dialog;


    @Override
    public void finish() {
        super.finish();
        hideLoading();
        overridePendingTransition(R.anim.anim_activity_none, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.slide_out_left);
        setContentView(R.layout.activity_verify_phone);

        Init();

        // Get Extra
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            verify_code = null;
            Snackbar.make(rootLayout, "Can't get user information.Please try again!", Snackbar.LENGTH_SHORT)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(),EnterPhoneNumberActivity.class);
                            VerifyPhoneActivity.this.startActivity(intent);
                            hideLoading();
                            finish();
                        }
                    }).show();
        } else {
            verify_code = extras.getString("verify_code");
            phone_number = extras.getString("phone_number");
            forceResendingToken = (PhoneAuthProvider.ForceResendingToken)extras.getParcelable("forceResendingToken");
        }

        String user_number =  phone_number;
        if (user_number.charAt(0) == '0') {
            user_number = new StringBuilder(user_number).deleteCharAt(0).toString();
        }

        phone_number = "+84" + user_number;
        tv_phone_number.setText(phone_number);

        AnimationIn();

        startTimer();
    }

    // region -------------- Resend code
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    private void startTimer() {
        // Init callback
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.w(TAG, "onVerificationCompleted");
                firebaseAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    final String uid = task.getResult().getUser().getUid();

                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child(Define.DB_USERS_INFO);
                                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            if (snapshot.hasChild(uid)) {
                                                Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                VerifyPhoneActivity.this.startActivity(intent);
                                                hideLoading();
                                                finish();
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), UpdateInfoActivity.class);
                                                VerifyPhoneActivity.this.startActivity(intent);
                                                hideLoading();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e(TAG, databaseError.getMessage());
                                        }
                                    });
                                }
                            }
                        });

            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Log.w(TAG, e.getMessage());

                if (e.getMessage().toLowerCase().contains("network")) {
                    tv_error.setText(R.string.cant_connect_network);
                }
                else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    tv_error.setText(R.string.cant_send_sms);
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    tv_error.setText(R.string.server_storage_overload);
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
                Log.d(TAG, "onCodeSent:" + verify_code);

                verify_code = s;
            }
        };

        // start Timer
        CountDownTimer countDownTimer;
        countDownTimer = new CountDownTimer(20*1000, 1000 - 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_resend_code.setText(String.format(getString(R.string.resend_code), millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                tv_resend_code.setText(R.string.click_to_resend_code);
                tv_resend_code.setOnClickListener( v ->{
                    resendVerificationCode(phone_number);
                    startTimer();
                });
            }
        };
        countDownTimer.start();
    }

    private void resendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                2,
                TimeUnit.MINUTES,
                this,
                mCallback,
                forceResendingToken);
    }
    // endregion


    private void Init() {
        // Init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Init views
        fab = (FloatingActionButton)findViewById (R.id.fab);

        tv_error = findViewById(R.id.tv_error);
        tv_error.setText("");

        tv_resend_code = findViewById(R.id.tv_resend_code);

        tv_phone_number = findViewById(R.id.tv_phone_number);

        editTextMap = new LinkedHashTreeMap<>();
        editTextMap.put(1,(EditText) findViewById(R.id.et_number1));
        editTextMap.put(2,(EditText) findViewById(R.id.et_number2));
        editTextMap.put(3,(EditText) findViewById(R.id.et_number3));
        editTextMap.put(4,(EditText) findViewById(R.id.et_number4));
        editTextMap.put(5,(EditText) findViewById(R.id.et_number5));
        editTextMap.put(6,(EditText) findViewById(R.id.et_number6));
        rootLayout = findViewById(R.id.root);

        // Events
        (findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (final Map.Entry<Integer,EditText> pair : editTextMap.entrySet())
        {
            pair.getValue().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(start == 0 && count == 0)
                    {
                        if(pair.getKey() != 1)
                        {
                            int i = pair.getKey() - 1;
                            editTextMap.get(i).requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!TextUtils.isEmpty(s.toString()))
                    {
                        if (pair.getKey() == 6) {
                            // If done for last digit then go and check
                            verifyPhoneNumber();
                        }
                        else
                            editTextMap.get(pair.getKey() + 1).requestFocus();
                    }
                }
            });

            pair.getValue().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)
                    {
                        // Select all when focus
                        EditText et = (EditText) v;
                        et.setSelectAllOnFocus(true);
                    }
                }
            });

            pair.getValue().setOnKeyListener(new View.OnKeyListener()
            {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    }
                    return false;
                }
            });
        }
    }

    private void AnimationIn() {
        Animation fab_button_anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        fab_button_anim.reset();
        fab.clearAnimation();
        fab.startAnimation(fab_button_anim);
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

    private void signInWithPhone(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in Success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            // Get User Firebase id

                            final String uid = task.getResult().getUser().getUid();

                            // Check if user has
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child(Define.DB_USERS_INFO);
                            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.hasChild(uid)) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        VerifyPhoneActivity.this.startActivity(intent);
                                        hideLoading();
                                        finish();
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), UpdateInfoActivity.class);
                                        VerifyPhoneActivity.this.startActivity(intent);
                                        hideLoading();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // Failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                tv_error.setText("The verification code entered was invalid. Please check a gain!");
                            }
                            hideLoading();
                        }
                    }
                });

    }

    public void verify(View view)       // from View
    {
        verifyPhoneNumber();
    }

    private void verifyPhoneNumber() {
        if (TextUtils.isEmpty(verify_code)) {
            // When verify code is null go back to EnterPhoneNumberActivity to sent the code again
            cannotVerify();
        }
        else {
            // check if put all the input code

            StringBuilder input_code = new StringBuilder();

            for (final Map.Entry<Integer,EditText> pair : editTextMap.entrySet())
            {
                //check not input
                if (TextUtils.isEmpty(pair.getValue().getText().toString())) {
                    tv_error.setText("Please enter the code");
                    return;
                }
                input_code.append(pair.getValue().getText().toString());
            }

            showLoading();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify_code, input_code.toString());
            signInWithPhone(credential);
        }

    }

    private void cannotVerify()
    {
        Snackbar.make(rootLayout, "Can't verify the code.Please try again!", Snackbar.LENGTH_LONG)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VerifyPhoneActivity.this.finish();
                    }
                })
                .show();
    }
}
