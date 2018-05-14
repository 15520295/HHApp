package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.huydaoduc.hieu.chi.hhapp.BuildConfig;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.ImageUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cn.bingoogolapple.titlebar.BGATitleBar;
import de.hdodenhof.circleimageview.CircleImageView;

public class AboutUser extends AppCompatActivity {

    private static final int GET_IMG_FROM_STORAGE_REQUEST_CODE = 0;
    private static final int GET_IMG_FROM_CAMERA_REQUEST_CODE = 1;
    CircleImageView circleEditView;
    RelativeLayout group_profile_image;
    public TextView txtPhoneNumber, tv_error;
    EditText et_NameUser;
    EditText et_Yob;
    public static String phone = "";
    public static String name = "";

    private DatabaseReference dbRefe;

    BGATitleBar titleBar;
    CircleImageView circleImageView;

    UserInfo userInfo;

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_user);

        userInfo = CurUserInfo.getInstance().getUserInfo();

        Init();

        titleBar.setDelegate(new BGATitleBar.Delegate() {
            @Override
            public void onClickLeftCtv() {
                finish();
            }

            @Override
            public void onClickTitleCtv() {

            }

            @Override
            public void onClickRightCtv() {
                showLoadingDialog(getString(R.string.loading_saving));
                if (TextUtils.isEmpty(et_NameUser.getText().toString()) || TextUtils.isEmpty(et_Yob.getText().toString())) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(R.string.input_info_request);
                    return;
                }
                userInfo.setName(et_NameUser.getText().toString());
                userInfo.setYearOfBirth(et_Yob.getText().toString());
                dbRefe.child(Define.DB_USERS_INFO)
                        .child(getCurUid())
                        .setValue(userInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    hideLoadingDialog();
                                    CurUserInfo.getInstance().setUserInfo(userInfo);
                                    AboutUser.this.setResult(Activity.RESULT_OK);
                                    AboutUser.this.finish();
                                }
                                else {
                                    onClickRightCtv();
                                }
                            }
                        });
            }

            @Override
            public void onClickRightSecondaryCtv() {

            }
        });


        et_NameUser.setText(userInfo.getName());

        // avatar
        if (userInfo.getPhoto() != null) {
            Bitmap bitmap = ImageUtils.base64ToBitmap(userInfo.getPhoto());
            circleImageView.setImageBitmap(bitmap);
        }

        // Import avatar from Camera or Storage
        final String[] items = new String[]{"From SD Card", "From Camera"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    dialog.dismiss();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    AboutUser.this.startActivityForResult(pickPhoto, GET_IMG_FROM_STORAGE_REQUEST_CODE);


                } else {
                    dialog.dismiss();
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, GET_IMG_FROM_CAMERA_REQUEST_CODE);

//                    checkPermission();
                }
            }
        });

        final AlertDialog dialog = builder.create();

        group_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }

    public void Init() {
        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        txtPhoneNumber = findViewById(R.id.txtPhoneEditAcc);
        tv_error = findViewById(R.id.tv_error);
        et_NameUser = findViewById(R.id.et_NameUser);
        et_Yob = findViewById(R.id.et_Yob);
        circleImageView = findViewById(R.id.profile_image);
        circleEditView = findViewById(R.id.profile_edit);
        group_profile_image = findViewById(R.id.group_profile_image);
        dbRefe = FirebaseDatabase.getInstance().getReference();

        phone = userInfo.getPhoneNumber();
        name = userInfo.getName();

        txtPhoneNumber.setText(phone);
        et_NameUser.setText(name);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GET_IMG_FROM_STORAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    circleImageView.setImageURI(uri);
                    String base64Photo = ImageUtils.bitmapToBase64(Objects.requireNonNull(ImageUtils.uriToBitmap(getApplicationContext(), uri)));
                    userInfo.setPhoto(base64Photo);
                }
                break;

            case GET_IMG_FROM_CAMERA_REQUEST_CODE:
            {
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    circleImageView.setImageBitmap(bitmap);
                    String base64Photo = ImageUtils.bitmapToBase64(bitmap);
                    userInfo.setPhoto(base64Photo);
                }
                break;

            }
        }
    }

    //region Loading Dialog
    MaterialDialog loadingPassengerInfo;
    private void showLoadingDialog(String title) {
        loadingPassengerInfo = new MaterialDialog.Builder(this)
                .title(title)
                .content("Please wait...")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.title_bar_background_color))
                .widgetColorRes(R.color.title_bar_background_color)
                .buttonRippleColorRes(R.color.title_bar_background_color).show();
    }

    private void hideLoadingDialog() {
        if (loadingPassengerInfo != null)
            loadingPassengerInfo.dismiss();
    }
    //endregion

}
