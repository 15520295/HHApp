package com.uit.huydaoduc.hieu.chi.hhapp.ActivitiesAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.MainActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UpdateInfoActivity extends AppCompatActivity {


    EditText et_name, et_yob;
    private FirebaseAuth firebaseAuth;
    private TextView tv_error;
    private FloatingActionButton fab;


    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        Init();
    }

    private void Init() {
        // Init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Init views
        fab = (FloatingActionButton)findViewById (R.id.fab);

        tv_error = findViewById(R.id.tv_error);
        tv_error.setText("");

        et_name = findViewById(R.id.et_name);

        et_yob = findViewById(R.id.et_yob);

        // Events
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid())
                {
                    showLoading();
                    updateInfo();

                    Intent intent = new Intent(UpdateInfoActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    UpdateInfoActivity.this.startActivity(intent);
                    UpdateInfoActivity.this.finish();
                }
            }
        });

    }

    private void updateInfo() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        String name = et_name.getText().toString();
        String yob = et_yob.getText().toString();

        UserInfo user = UserInfo.Builder.anUserInfo(uid)
                .setName(name)
                .setYearOfBirth(yob)
                .setPhoneNumber(firebaseAuth.getCurrentUser().getPhoneNumber())
                .build();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child(Define.DB_USERS_INFO);

        usersRef.child(uid).setValue(user);
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(et_name.getText().toString()) || TextUtils.isEmpty(et_yob.getText().toString())) {
            tv_error.setText(R.string.please_enter_your_information);
            return false;
        }
        int i = Integer.valueOf(et_yob.getText().toString());
        if (i <= 1900 && i > Calendar.getInstance().get(Calendar.YEAR)) {
            tv_error.setText(R.string.yob_not_correct);
            return false;
        }
        return true;
    }

    private void AnimationIn() {
        Animation fab_button_anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        fab_button_anim.reset();
        fab.clearAnimation();
        fab.startAnimation(fab_button_anim);
    }

    private void showLoading() {
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
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
}

