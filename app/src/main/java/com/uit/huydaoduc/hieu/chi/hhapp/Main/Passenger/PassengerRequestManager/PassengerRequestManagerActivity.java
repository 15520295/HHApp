package com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerRequestManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.uit.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.UpdateInfoActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.DBManager;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.ImageUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.AboutApp;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.AboutUser;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.CurUserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bingoogolapple.titlebar.BGATitleBar;
import de.hdodenhof.circleimageview.CircleImageView;


public class PassengerRequestManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int CREATE_PASSENGER_REQUEST_CODE = 1;
    private static final int CALL_PERMISSION_REQUEST_CODE = 5;
    BGATitleBar titleBar;
    FloatingActionButton fab_add_request;

    List<PassengerRequest> passengerRequests;

    DatabaseReference dbRefe;

    UserInfo userInfo;

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshList(false);
    }


    @Override
    public void onBackPressed() {
        if(! drawer_layout.isDrawerOpen(GravityCompat.START))
            super.onBackPressed();
        else {
            drawer_layout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_request_manager);
        dbRefe = FirebaseDatabase.getInstance().getReference();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userInfo = null;
            } else {
                userInfo = extras.getParcelable("userInfo");
            }
        } else {
            userInfo = (UserInfo) savedInstanceState.getParcelable("userInfo");
        }
        if (userInfo == null) {
            dbRefe.child(Define.DB_USERS_INFO)
                    .child(getCurUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userInfo = dataSnapshot.getValue(UserInfo.class);
                            if (userInfo == null) {
                                Intent startIntent = new Intent(getApplicationContext(), UpdateInfoActivity.class);
                                PassengerRequestManagerActivity.this.startActivity(startIntent);
                                PassengerRequestManagerActivity.this.finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        Init();

        Event();

        PassengerRequest passengerRequest;
        RouteRequest routeRequest;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                passengerRequest= null;
                routeRequest = null;
            } else {
                passengerRequest= extras.getParcelable("passengerRequest");
                routeRequest= extras.getParcelable("routeRequest");
            }
        } else {
            passengerRequest= (PassengerRequest) savedInstanceState.getParcelable("passengerRequest");
            routeRequest= (RouteRequest) savedInstanceState.getParcelable("routeRequest");
        }

        if (routeRequest != null) {
            // show driver info
            DBManager.getUserById(routeRequest.getDriverUId(), (userInfo) ->
                    {
                        showNotificationforPassenger(getApplicationContext(), userInfo);
                        setUpDialogInfo(userInfo);
                        hideLoadingPassengerRequestInfo();
                    }
            );
        }
        if (passengerRequest != null) {
            listenToPassengerRequestNotify(passengerRequest);
            refreshList(true);
        }

        refreshList(true);
    }

    public void showNotificationforPassenger(Context context, UserInfo driverInfo) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_location_on)
                        .setContentTitle(driverInfo.getName())
                        .setContentText(driverInfo.getPhoneNumber())
                        .setPriority(2);

        Intent resultIntent = new Intent(context, PassengerRequestManagerActivity.class);

        resultIntent.putExtra("userInfo", driverInfo);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

//                        Uri newSound= Uri.parse("android.resource://"
//                                + getPackageName() + "/" + R.raw.gaugau);
//                        mBuilder.setSound(newSound);

        int mNotificationId = 1;
        // Gets an instance of the NotificationManager service
        //Log.d(TAG, driverInfo.getName());
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    private void Init() {
        //view
        fab_add_request = findViewById(R.id.fab_add_request);

        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        // recycler
        initRecyclerView();

        initNavigation();

        passengerRequests = new ArrayList<>();

        // init database

        initRefeshLayout();

    }

    private void Event() {
        titleBar.setDelegate(new BGATitleBar.Delegate() {
            @Override
            public void onClickLeftCtv() {
                drawer_layout.openDrawer(GravityCompat.START);
            }

            @Override
            public void onClickTitleCtv() {

            }

            @Override
            public void onClickRightCtv() {

            }

            @Override
            public void onClickRightSecondaryCtv() {

            }
        });

        fab_add_request.setOnClickListener(e ->{
            Intent intent = new Intent(getApplicationContext(), PassengerActivity.class);
            PassengerRequestManagerActivity.this.startActivityForResult(intent,CREATE_PASSENGER_REQUEST_CODE);
        });

    }

    public void listenToPassengerRequestNotify(PassengerRequest passengerRequest) {
        //Listen to Trip Notify - asynchronous with service
        dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                .child(passengerRequest.getPassengerUId())
                .child(passengerRequest.getPassengerRequestUId())
                .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NotifyTrip notifyTrip = dataSnapshot.getValue(NotifyTrip.class);

                        if(notifyTrip != null && !notifyTrip.isNotified())
                        {
                            notifyTrip.setNotified(true);
                            dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                                    .child(passengerRequest.getPassengerUId())
                                    .child(passengerRequest.getPassengerRequestUId())
                                    .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
                                    .setValue(notifyTrip);

                            // todo: notification
                            showNotificationforPassenger(getApplicationContext(), userInfo);
                            Toast.makeText(getApplicationContext(),"Found your driver",Toast.LENGTH_LONG).show();
                            refreshList(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //region -------------- Init Recycle View + Event ----------------
    RecyclerView rycv_passenger_request;

    private void initRecyclerView() {
        rycv_passenger_request = findViewById(R.id.recycler_view_requests);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rycv_passenger_request.setLayoutManager(llm);
    }

    private void refreshList(boolean enableLoading) {
        if (enableLoading)
            startLoadingRecycler();

        FirebaseDatabase.getInstance().getReference()
                .child(Define.DB_PASSENGER_REQUESTS)
                .child(getCurUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        passengerRequests.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            PassengerRequest request = postSnapshot.getValue(PassengerRequest.class);

                            // notify Trip And Update Request State On Server;
                            NotifyTrip notifyTrip = request.getNotifyTrip();
                            // if trip is not notify then notify and update data on server
                            if(notifyTrip != null && ! notifyTrip.isNotified())
                            {
                                // update NotifyTrip value to notified
                                notifyTrip.setNotified(true);
                                dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                                        .child(request.getPassengerUId())
                                        .child(request.getPassengerRequestUId())
                                        .child(Define.DB_PASSENGER_REQUESTS_NOTIFY_TRIP)
                                        .setValue(notifyTrip);

                                // change Passenger Request state
                                dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                                        .child(request.getPassengerUId())
                                        .child(request.getPassengerRequestUId())
                                        .child(Define.DB_PASSENGER_REQUESTS_PASSENGER_REQUEST_STATE)
                                        .setValue(PassengerRequestState.FOUND_DRIVER);

                                // notify passenger
                                //todo : notify Notification
                                showNotificationforPassenger(getApplicationContext(), userInfo);
                            }
                            request.setNotifyTrip(notifyTrip);

                            passengerRequests.add(request);
                        }

                        Collections.reverse(passengerRequests);

                        // Passenger request Row Click event
                        PassengerRequestAdapter adapter = new PassengerRequestAdapter(passengerRequests);
                        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
                            if (view.getId() == R.id.iv_menu) {
                                showTopRightMenuItem(position, view);
                            }
                            if (view.getId() == R.id.btn_request_state) {
                                btnRequestStateClick(position);
                            }
                        });

                        rycv_passenger_request.setAdapter(adapter);
                        stopLoadingRecycler();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //region -------------- Row Click Event ----------------

    /**
     * Show driver info if found when click state button
     * @param position item in passengerRequests list
     */
    private void btnRequestStateClick(int position) {
        showLoadingPassengerRequestInfo(getString(R.string.loading_driver_info));

        PassengerRequest passengerRequest = passengerRequests.get(position);
        if (passengerRequest.getPassengerRequestState() == PassengerRequestState.FOUND_DRIVER) {
            String tripUId = passengerRequest.getNotifyTrip().getTripUId();

            DBManager.getTripById(tripUId, trip -> {
                // get Passenger Info
                DBManager.getUserById(trip.getDriverUId(), (userInfo) ->
                        {
                            setUpDialogInfo(userInfo);
                            hideLoadingPassengerRequestInfo();
                        }
                );
            });
        }
    }

    private void showTopRightMenuItem(int position, View view) {
        TopRightMenu topRightMenu;
        topRightMenu = new TopRightMenu(PassengerRequestManagerActivity.this);

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add( new MenuItem(R.drawable.ic_pause_20, getString(R.string.pause)));
        menuItems.add( new MenuItem(R.drawable.ic_delete_20, getString(R.string.delete)));
        menuItems.add( new MenuItem(R.drawable.ic_resume_20, getString(R.string.resume)));

        topRightMenu
                .setHeight(340)
                .setWidth(320)
                .showIcon(true)
                .dimBackground(true)
                .needAnimationStyle(true)
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)
                .addMenuList(menuItems)
                .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int menuPosition) {
                        String command = null;

                        if (menuPosition == 0) {
                            command = "Pause";
                        } else if (menuPosition == 1) {
                            command = "Delete";
                        } else if (menuPosition == 2) {
                            command = "Resume";
                        }

                        changeRequestState(position, command);
                    }
                })
                .showAsDropDown(view, -250, 0);	//带偏移量
    }

    //endregion

    //endregion

    //region -------------- Passenger request ----------------


    private void changeRequestState(int position, String command) {
        if (command == null) {
            return;
        }

        PassengerRequest request = passengerRequests.get(position);
        PassengerRequestState state = request.getPassengerRequestState();

        long passTime = TimeUtils.getPassTime(request.getTripFareInfo().getStartTime())/60;
        int waitMin = request.getWaitMinute();
        if (passTime > waitMin) {
            if (command == "Delete" && request.getPassengerRequestState() != PassengerRequestState.FOUND_DRIVER) {
                // change state on server
                dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                        .child(request.getPassengerUId())
                        .child(request.getPassengerRequestUId())
                        .removeValue();

                refreshList(false);
            } else {
                new MaterialDialog.Builder(this)
                        .content(R.string.request_out_of_date)
                        .positiveText(R.string.ok)
                        .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                        .positiveColor(getResources().getColor(R.color.title_bar_background_color_blue))
                        .widgetColorRes(R.color.title_bar_background_color_blue)
                        .buttonRippleColorRes(R.color.title_bar_background_color_blue)
                        .show();
            }

            return;
        }
        else if (state == PassengerRequestState.FOUND_DRIVER) {
            if (command.equals("Pause") || command.equals("Delete")) {
                new MaterialDialog.Builder(this)
                        .content(R.string.cancel_request_warning_passenger)
                        .positiveText(R.string.ok)
                        .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                        .positiveColor(getResources().getColor(R.color.title_bar_background_color_blue))
                        .widgetColorRes(R.color.title_bar_background_color_blue)
                        .buttonRippleColorRes(R.color.title_bar_background_color_blue)
                        .show();

                return;
            }
        }
        else if (command.equals("Delete")) {
            // change state on server
            dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                    .child(request.getPassengerUId())
                    .child(request.getPassengerRequestUId())
                    .removeValue();

            refreshList(false);
            return;
        }

        if (state == PassengerRequestState.FINDING_DRIVER) {
            if (command.equals("Pause")) {
                new MaterialDialog.Builder(this)
                        .title(R.string.pause_request_title)
                        .content(R.string.pause_request_content_passenger)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                        .positiveColor(getResources().getColor(R.color.title_bar_background_color_blue))
                        .widgetColorRes(R.color.title_bar_background_color_blue)
                        .buttonRippleColorRes(R.color.title_bar_background_color_blue)
                        .onPositive((dialog, which) -> {
                            // change state on server
                            dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                                    .child(request.getPassengerUId())
                                    .child(request.getPassengerRequestUId())
                                    .child(Define.DB_PASSENGER_REQUESTS_PASSENGER_REQUEST_STATE)
                                    .setValue(PassengerRequestState.PAUSE);

                            refreshList(false);
                        })
                        .show();
            }
        }
        if (state == PassengerRequestState.PAUSE) {
            if (command.equals("Resume")) {
                // change state on server
                dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                        .child(request.getPassengerUId())
                        .child(request.getPassengerRequestUId())
                        .child(Define.DB_PASSENGER_REQUESTS_PASSENGER_REQUEST_STATE)
                        .setValue(PassengerRequestState.FINDING_DRIVER);

                refreshList(false);
                return;
            }
        }
    }

    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        NavigationResultHandle(requestCode, resultCode, data);
    }

    //region -------------- Show Driver Info --------------

    Dialog dialogInfo;

    /**
     * If Online User is in "D_RECEIVING_BOOKING_HH state" and NOT "time out" then get User info as marker
     */

    private void setUpDialogInfo(final UserInfo driverInfo) {
        MaterialFancyButton btnMessage, btnCall;
        TextView tvName, tvPhone, tv_yob;
        CircleImageView civProfilePic;
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Font Grab Bike.ttf");

        dialogInfo = new Dialog(PassengerRequestManagerActivity.this);
        dialogInfo.setContentView(R.layout.info_user);

        btnMessage = dialogInfo.findViewById(R.id.btn_messenger);
        btnCall = dialogInfo.findViewById(R.id.btn_call);
        tvName = dialogInfo.findViewById(R.id.tvName);
        tvPhone = dialogInfo.findViewById(R.id.tvPhone);
        tv_yob = dialogInfo.findViewById(R.id.tv_yob);
        civProfilePic = dialogInfo.findViewById(R.id.civProfilePic);

        tvName.setTypeface(face);
        tvPhone.setTypeface(face);
        tv_yob.setTypeface(face);

        tvName.setText(driverInfo.getName());
        tvPhone.setText(driverInfo.getPhoneNumber());
        tv_yob.setText(driverInfo.getYearOfBirth());
        if (driverInfo.getPhoto() != null)
            civProfilePic.setImageBitmap(ImageUtils.base64ToBitmap(driverInfo.getPhoto()));

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + driverInfo.getPhoneNumber()));
                intent.putExtra("sms_body", "");
                startActivity(intent);
                dialogInfo.dismiss();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + driverInfo.getPhoneNumber()));
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {

                        PassengerRequestManagerActivity.this.startActivity(intent);
                        dialogInfo.dismiss();

                    } else {
                        ActivityCompat.requestPermissions(PassengerRequestManagerActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        });

        dialogInfo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.call_permission_granted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.call_permission_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //endregion


    //region -------------- Smart Refresh Layout ----------------

    SmartRefreshLayout smartRefreshLayout;

    private void initRefeshLayout() {
        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshList(true);
            }
        });
    }

    private void startLoadingRecycler() {
        smartRefreshLayout.autoRefresh();

    }

    private void stopLoadingRecycler() {
        smartRefreshLayout.finishRefresh(500);
    }

    //endregion

    // Animation

    MaterialDialog loadingPassengerInfo;
    private void showLoadingPassengerRequestInfo(String title) {
        loadingPassengerInfo = new MaterialDialog.Builder(this)
                .title(title)
                .content(R.string.please_wait)
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                .widgetColorRes(R.color.title_bar_background_color_blue)
                .buttonRippleColorRes(R.color.title_bar_background_color_blue).show();
    }

    private void hideLoadingPassengerRequestInfo() {
        if (loadingPassengerInfo != null)
            loadingPassengerInfo.dismiss();
    }

    //region Navigation Bar

    private static final int ABOUT_USER_REQUEST_CODE = 70;

    NavigationView navigationView;
    DrawerLayout drawer_layout;
    View headerLayout;

    private void initNavigation() {
        // Navigation bar
        drawer_layout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);

        updateNavUserInfo();

        RelativeLayout group_Avatar = headerLayout.findViewById(R.id.group_Avatar);
        group_Avatar.setOnClickListener(v ->{
            Intent i = new Intent(PassengerRequestManagerActivity.this, AboutUser.class);
            PassengerRequestManagerActivity.this.startActivityForResult(i, ABOUT_USER_REQUEST_CODE);
        });
    }

    public void updateNavUserInfo() {
        UserInfo userInfo = CurUserInfo.getInstance().getUserInfo();

        ImageView iv_Avatar = headerLayout.findViewById(R.id.iv_Avatar);
        TextView txtName = headerLayout.findViewById(R.id.txtName);
        TextView txtPhone = headerLayout.findViewById(R.id.txtPhone);

        if (userInfo.getPhoto() != null) {
            iv_Avatar.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getPhoto()));
        }
        txtName.setText(userInfo.getName());
        txtPhone.setText(userInfo.getPhoneNumber());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent i = new Intent(PassengerRequestManagerActivity.this, AboutApp.class);
            PassengerRequestManagerActivity.this.startActivity(i);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PassengerRequestManagerActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_share){

            Intent i = new Intent(Intent.ACTION_SEND);

            i.setType("text/plain");
            String shareBody = "https://play.google.com/store/apps/details?id=com.example.huydaoduc.hieu.chi.hhapp";
            String shareName = "SBike";
            i.putExtra(Intent.EXTRA_TEXT,shareBody);
            i.putExtra(Intent.EXTRA_SUBJECT,shareName);

            startActivity(Intent.createChooser(i, "Sharing"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void NavigationResultHandle(int requestCode, int resultCode, Intent data) {
        if (requestCode == ABOUT_USER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                updateNavUserInfo();
            }
        }
    }

    //endregion
}
