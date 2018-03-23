package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.MainActivity;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterPassActivity extends AppCompatActivity {

    EditText et_password;
    TextView tv_error;
    RelativeLayout rootLayout;
    String user_password;
    private String uid;
    private DatabaseReference databaseReference;

    @Override
    public void finish() {
        super.finish();
        //todo: logout if not sign in
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pass);

        Init();

        //                                // Put the User to the FirebaseDatabase with User's information
        //                                databaseReference.child("users").child(uid).setValue(user);
        //todo: check password instantly
        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
        {
            canNotGetUser();
        }else
        {
            uid = bundle.getString("uid");
            // get user password
            databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                user_password = user.getPassword();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                canNotGetUser();
            }
        });
        }
    }

    private void Init() {
        // init Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // init views
        rootLayout = findViewById(R.id.rootLayout);
        et_password = findViewById(R.id.et_password);
        tv_error = findViewById(R.id.tv_error);

        // Events
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPassword();
            }
        });
    }

    private void checkPassword()
    {
        if(et_password.getText().toString().equals(user_password))
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void canNotGetUser()
    {
        Snackbar.make(rootLayout, "Can't get user information.Please try again!", Snackbar.LENGTH_SHORT)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),EnterPhoneNumberActivity.class);
                        EnterPassActivity.this.startActivity(intent);
                    }
                }).show();
    }

}
