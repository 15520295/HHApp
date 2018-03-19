package com.example.huydaoduc.hieu.chi.hhapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "login_email::";

    EditText et_email;
    EditText et_pass;
    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Init();
    }

    void Init()
    {
        // init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        // init Views
        rootLayout = findViewById(R.id.rootLayout);
        et_email = findViewById(R.id.et_email_address);
        et_pass = findViewById(R.id.et_password);


    }

    public void signup(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void login(View view) {
        // Check validation
        if(TextUtils.isEmpty(et_email.getText().toString()))
        {
            Snackbar.make(rootLayout,"Please enter email address", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(et_pass.getText().toString()))
        {
            Snackbar.make(rootLayout,"Please enter password", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(et_pass.getText().toString().length() < 6)
        {
            Snackbar.make(rootLayout,"Password too short", Snackbar.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(et_email.getText().toString(), et_pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = auth.getCurrentUser();
//                            updateUI(user);
                            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            Toast.makeText(getApplicationContext(),"Fail",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void logout()
    {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }
}
