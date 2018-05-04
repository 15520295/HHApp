package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.in;

public class AboutUser extends AppCompatActivity {

    CircleImageView circleEditView;
    ImageButton imageButton;
    public static TextView txtPhoneNumber, txtNameUser;
    public static String phone = "";
    public static String name = "";
    Uri selectedImage;

    private DatabaseReference mDatabase;
    private Dialog dialogChangeName;

    CircleImageView circleImageView;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    UserInfo userInfo;

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_user);

        Init();



        txtNameUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AboutUser.this, ChangeName.class);
                startActivity(intent);

            }
        });


//        FirebaseDatabase.getInstance().getReference("Users")
//                .child(MainActivity.id)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        MainActivity.nameUser = dataSnapshot.child("name").getValue().toString();
//                        txtNameUser.setText(MainActivity.nameUser);
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
        txtNameUser.setText(userInfo.getName());

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
                    startActivityForResult(pickPhoto, 0);


                } else {

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 1);

                }
            }
        });


        final AlertDialog dialog = builder.create();

        circleEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        Bitmap bitmap = decodeBase64(userInfo.getPhotoUri());
//        if (userInfo.getPhotoUri() != null)
//            circleImageView.setImageBitmap(bitmap);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    circleImageView.setImageURI(selectedImage);

                    //uploadImage();

                    Bitmap bitmap = ((BitmapDrawable) circleImageView.getDrawable()).getBitmap();
                    String encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
//
//                    Toast.makeText(AboutUser.this, encodedImage, Toast.LENGTH_LONG).show();
                    mDatabase.child("Users").child(getCurUid()).child("avatar").setValue(encodedImage);



                }
                break;

            case 1:

                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    circleImageView.setImageBitmap(bitmap);

                    String encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
//                    Toast.makeText(AboutUser.this, encodedImage, Toast.LENGTH_LONG).show();
                    mDatabase.child("Users").child(getCurUid()).child("avatar").setValue(encodedImage);


                }

                break;
        }

        FirebaseDatabase.getInstance().getReference("Users")
                .child(getCurUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                        userInfo.getPhotoUri() = dataSnapshot.child("avatar").getValue().toString();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public void Init() {
        txtPhoneNumber = findViewById(R.id.txtPhoneEditAcc);
        txtNameUser = findViewById(R.id.txtNameUser);
        circleImageView = findViewById(R.id.profile_image);
        circleEditView = findViewById(R.id.profile_edit);
        imageButton = findViewById(R.id.imageButton);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        phone = userInfo.getPhoneNumber();
        name = userInfo.getName();

        txtPhoneNumber.setText(phone);
        txtNameUser.setText(name);

    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
