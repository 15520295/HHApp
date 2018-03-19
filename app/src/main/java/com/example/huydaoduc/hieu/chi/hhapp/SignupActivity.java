package com.example.huydaoduc.hieu.chi.hhapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

//import com.md.splashloginsignup.databinding.ActivitySignupBinding;

import com.example.huydaoduc.hieu.chi.hhapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "signup_email::";
    EditText et_fullname;
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
        setContentView(R.layout.activity_signup);

        Init();
    }

    private void Init() {
        // init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        // init Views
        rootLayout = findViewById(R.id.rootLayout);
        et_fullname = findViewById(R.id.et_full_name);
        et_email = findViewById(R.id.et_email_address);
        et_pass = findViewById(R.id.et_password);
    }


    public void signup_email(View view) {
        // Check validation
        if(TextUtils.isEmpty(et_fullname.getText().toString()))
        {
            Snackbar.make(rootLayout,"Please enter your name", Snackbar.LENGTH_SHORT).show();
            return;
        }
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

        auth.createUserWithEmailAndPassword(et_email.getText().toString(), et_pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Authentication succeeded.", Toast.LENGTH_SHORT).show();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }

    public void login_email(View view) {
        startActivity(new Intent(this, LoginActivity.class));
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
