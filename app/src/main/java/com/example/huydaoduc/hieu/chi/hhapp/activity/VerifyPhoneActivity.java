package com.example.huydaoduc.hieu.chi.hhapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.huydaoduc.hieu.chi.hhapp.R;

public class VerifyPhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_activity_none, R.anim.anim_activity_none);
        setContentView(R.layout.activity_verify_phon);
    }
}
