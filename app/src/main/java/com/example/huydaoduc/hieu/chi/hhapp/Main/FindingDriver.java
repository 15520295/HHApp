package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.huydaoduc.hieu.chi.hhapp.R;

public class FindingDriver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_driver);

        Button close = findViewById(R.id.btnclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindingDriver.this.getSupportFragmentManager().popBackStack();
            }
        });
    }
}
