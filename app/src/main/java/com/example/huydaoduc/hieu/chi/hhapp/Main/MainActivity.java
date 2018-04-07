package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Model.UserApp;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //khoi tao firebase
        initFirebase();

        addEven();
    }

    private void addEven() {
        showRegisterDialog();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
    }

    private void initView() {
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void showRegisterDialog() {

        final String email = "123@yahoo.com";
        final String password = "123456";
        final String name = "Dao Duc Huy";
        final String phone = "01234094736";


        //set button

        //register new user
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Save users to database
                        UserApp user = new UserApp();
                        user.setEmail(email);
                        user.setName(name);
                        user.setPassword(password);
                        user.setPhone(phone);

                        //user email to key
                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "login fail", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                });
    }

}


