package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class VerifyPhoneActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText n1, n2, n3, n4;
    Map<Integer,EditText> editTextMap;
    RelativeLayout rootLayout;
    private String verify_code;

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_activity_none, R.anim.anim_activity_none);
        setContentView(R.layout.activity_verify_phone);

        Init();
        AnimationIn();
    }

    private void Init() {
        // Init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Init views
        editTextMap = new HashMap<>();
        editTextMap.put(1,(EditText) findViewById(R.id.et_number1));
        editTextMap.put(2,(EditText) findViewById(R.id.et_number2));
        editTextMap.put(3,(EditText) findViewById(R.id.et_number3));
        editTextMap.put(4,(EditText) findViewById(R.id.et_number4));
        editTextMap.put(5,(EditText) findViewById(R.id.et_number5));
        editTextMap.put(6,(EditText) findViewById(R.id.et_number6));
        rootLayout = findViewById(R.id.rootLayout);

        // Events
        for (final Map.Entry<Integer,EditText> pair : editTextMap.entrySet()) {
            pair.getValue().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

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

            pair.getValue().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if(pair.getKey() != 1)
                        {
                            if(keyCode == KeyEvent.KEYCODE_DEL ) {
                                int i = pair.getKey() - 1;
                                editTextMap.get(i).requestFocus();
                            }
                        }
                    }
                    return false;
                }
            });
        }


        // Get Extra
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            verify_code = null;
            Snackbar.make(rootLayout, "Can't send the code.Please try again!", Snackbar.LENGTH_SHORT).show();
        } else {
            verify_code = extras.getString("verify_code");
        }
    }

    private void AnimationIn() {

    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(),task.getResult().toString(),Toast.LENGTH_LONG).show();
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
            //todo: check not input
            StringBuilder input_code = new StringBuilder();

            for (final Map.Entry<Integer,EditText> pair : editTextMap.entrySet())
            {
                input_code.append(pair.getValue().getText().toString());
            }


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
