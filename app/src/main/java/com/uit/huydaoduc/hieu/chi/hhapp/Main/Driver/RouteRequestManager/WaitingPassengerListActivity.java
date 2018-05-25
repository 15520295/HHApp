package com.uit.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.uit.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.DBManager;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.ImageUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.AboutApp;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.AboutUser;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.CurUserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Driver.PassengerRequestInfoActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.MainActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripFareInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripType;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bingoogolapple.titlebar.BGATitleBar;


public class WaitingPassengerListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "WaitingPassengerListAct";
    BGATitleBar titleBar;
    RecyclerView rycv_route_request;

    List<PassengerRequest> passengerRequests;

    ValueEventListener passengerRequestListener;

    DatabaseReference dbRefe;

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onBackPressed() {
        if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            WaitingPassengerListActivity.this.startActivity(intent);
            WaitingPassengerListActivity.this.finish();
        }
        else {
            drawer_layout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(passengerRequestListener != null)
            dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                    .removeEventListener(passengerRequestListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList(false);
        if(passengerRequestListener != null)
            dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                    .addValueEventListener(passengerRequestListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_passenger_list);
        // init database
        dbRefe = FirebaseDatabase.getInstance().getReference();

        Init();

        Event();

        passengerRequestListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refreshList(true);
                Log.d(TAG, "passengerRequestListener - onDataChange");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                .addValueEventListener(passengerRequestListener);
    }

    private void Init() {
        //view
        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        initNavigation();
        // recycler
        initRecyclerView();

        passengerRequests = new ArrayList<>();

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

        ((TextView) findViewById(R.id.tv_tab_passenger_list)).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RouteRequestManagerActivity.class);
            WaitingPassengerListActivity.this.startActivity(intent);
            WaitingPassengerListActivity.this.overridePendingTransition(R.anim.anim_activity_none, R.anim.anim_activity_none);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        NavigationResultHandle(requestCode, resultCode, data);
    }

    //region -------------- Notify Passenger & show Passenger Request Info ----------------

    private void showPassengerInfoActivityItem(PassengerRequest passengerRequest, Trip trip) {
        showLoadingPassengerRequestInfo(getResources().getString(R.string.loading_passenger_info));

        DBManager.getUserById(trip.getPassengerUId(), userInfo -> {

            Intent intent = new Intent(WaitingPassengerListActivity.this, PassengerRequestInfoActivity.class);
            intent.putExtra("trip", trip);
            intent.putExtra("userInfo", userInfo);
            intent.putExtra("passengerRequest", passengerRequest);

            WaitingPassengerListActivity.this.startActivity(intent);

            hideLoadingPassengerRequestInfo();
        });
    }

    MaterialDialog loadingPassengerInfo;

    private void showLoadingPassengerRequestInfo(String title) {
        loadingPassengerInfo = new MaterialDialog.Builder(this)
                .title(title)
                .content(getResources().getString(R.string.please_wait))
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                .widgetColorRes(R.color.title_bar_background_color_blue)
                .buttonRippleColorRes(R.color.title_bar_background_color_blue).show();
    }

    private void hideLoadingPassengerRequestInfo() {
        if (loadingPassengerInfo != null)
            loadingPassengerInfo.dismiss();
    }

    //endregion

    //region -------------- Accepting trip ----------------

    private void acceptingPassengerRequest(PassengerRequest passengerRequest) {
        // Create a same RouteRequest with PassengerRequest Info
        String routeRequestUId = dbRefe.child(Define.DB_ROUTE_REQUESTS).child(getCurUid()).push().getKey();
        String tripUId = dbRefe.child(Define.DB_TRIPS).push().getKey();

        //todo: edit this
        RouteRequest routeRequest = RouteRequest.Builder
                .aRouteRequest(routeRequestUId)
                .setDriverUId(getCurUid())
                .setRouteRequestState(RouteRequestState.FOUND_PASSENGER)
                .setCarType(passengerRequest.getTripFareInfo().getCarType())
                .setStartPlace(passengerRequest.getPickUpSavePlace())
                .setEndPlace(passengerRequest.getDropOffSavePlace())
                .setStartTime(passengerRequest.getTripFareInfo().getStartTime())
                .setSummary("Summary")
                .setNotifyTrip(new NotifyTrip(tripUId, true))
                .setPercentDiscount(0f)
                .build();

        dbRefe.child(Define.DB_ROUTE_REQUESTS)
                .child(getCurUid())
                .child(routeRequestUId)
                .setValue(routeRequest);

        // up trip to server

        Trip trip = Trip.Builder.aTrip(tripUId)
                .setDriverUId(getCurUid())
                .setTripState(TripState.ACCEPTED)
                .setTripType(TripType.HH)
                .setRouteRequestUId(routeRequest.getRouteRequestUId())
                .build();

        trip.setRouteRequestUId(passengerRequest.getPassengerRequestUId());
        trip.setTripState(TripState.ACCEPTED);
        trip.setPassengerUId(passengerRequest.getPassengerUId());
        trip.setPassengerRequestUId(passengerRequest.getPassengerRequestUId());
        dbRefe.child(Define.DB_TRIPS)
                .child(trip.getTripUId())
                .setValue(trip);

        // notify passenger and change passenger state
        passengerRequest.setPassengerRequestState(PassengerRequestState.FOUND_DRIVER);
        passengerRequest.setNotifyTrip(new NotifyTrip(trip.getTripUId(), false));
        dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                .child(passengerRequest.getPassengerUId())
                .child(passengerRequest.getPassengerRequestUId())
                .setValue(passengerRequest);

        // show Passenger Request Info
        showPassengerInfoActivityItem(passengerRequest, trip);

        refreshList(false);
    }

    //endregion

    //region -------------- Recycle View & Route request Row click event----------------
    private void initRecyclerView() {
        rycv_route_request = findViewById(R.id.recycler_view_requests);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rycv_route_request.setLayoutManager(llm);
    }

    private void refreshList(boolean enableLoading) {
        if (enableLoading)
            startLoading();

        FirebaseDatabase.getInstance().getReference()
                .child(Define.DB_PASSENGER_REQUESTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                passengerRequests.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot requestSnapshot : postSnapshot.getChildren()) {
                        PassengerRequest request = requestSnapshot.getValue(PassengerRequest.class);

                        // Only get trip that finding driver
                        if (request.getPassengerRequestState() == PassengerRequestState.FINDING_DRIVER
                                && !request.getPassengerUId().equals(getCurUid())) {

                            long passTime = TimeUtils.getPassTime(request.getTripFareInfo().getStartTime())/60;
                            int waitMin = request.getWaitMinute();
                            if (passTime < waitMin) {
                                passengerRequests.add(request);
                            }
                        }
                    }
                }

                Collections.reverse(passengerRequests);

                // Route request Row click event
                WaitingPassengerRequestAdapter adapter = new WaitingPassengerRequestAdapter(passengerRequests);
                adapter.setOnItemChildClickListener((adapter1, view, position) -> {
                    if (view.getId() == R.id.btn_accept_trip) {
                        if ((loadingPassengerInfo != null && !loadingPassengerInfo.isShowing()) || loadingPassengerInfo == null) {
                            acceptingPassengerRequest(passengerRequests.get(position));
                        }
                    }
                });

                rycv_route_request.setAdapter(adapter);
                stopLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void startLoading() {
        smartRefreshLayout.autoRefresh();

    }

    private void stopLoading() {
        smartRefreshLayout.finishRefresh(500);
    }

    //endregion

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
        group_Avatar.setOnClickListener(v -> {
            Intent i = new Intent(WaitingPassengerListActivity.this, AboutUser.class);
            WaitingPassengerListActivity.this.startActivityForResult(i, ABOUT_USER_REQUEST_CODE);
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
            Intent i = new Intent(WaitingPassengerListActivity.this, AboutApp.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            WaitingPassengerListActivity.this.startActivity(i);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(WaitingPassengerListActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

            Intent i = new Intent(Intent.ACTION_SEND);

            i.setType("text/plain");
            String shareBody = "https://play.google.com/store/apps/details?id=com.example.huydaoduc.hieu.chi.hhapp";
            String shareName = "SBike";
            i.putExtra(Intent.EXTRA_TEXT, shareBody);
            i.putExtra(Intent.EXTRA_SUBJECT, shareName);

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
