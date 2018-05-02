package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.huydaoduc.hieu.chi.hhapp.R;

public class MainActivity extends AppCompatActivity {
    Button btn_rider, btn_driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        addEven();
    }

    private void addEven() {

        btn_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DriverActivity.class);
                startActivity(intent);
            }
        });


        btn_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        btn_rider = findViewById(R.id.btn_rider);
        btn_driver = findViewById(R.id.btn_driver);
    }

}


