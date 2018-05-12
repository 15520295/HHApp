package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerRequestManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
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


public class PassengerRequestManagerActivity extends AppCompatActivity {

    private static final int CREATE_PASSENGER_REQUEST_CODE = 1;
    BGATitleBar titleBar;
    RecyclerView rycv_passenger_request;
    FloatingActionButton fab_add_request;

    List<PassengerRequest> passengerRequests;

    DatabaseReference dbRefe;

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_request_manager);

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
                        setUpDialogInfo(userInfo);
                        hideLoadingPassengerRequestInfo();
                    }
            );
        }
        if (passengerRequest != null) {
            listenToPassengerRequestNotify(passengerRequest);
        }

        refreshList(true);
    }

    private void Init() {
        //view
        fab_add_request = findViewById(R.id.fab_add_request);

        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        // recycler
        initRecyclerView();

        passengerRequests = new ArrayList<>();

        // init database
        dbRefe = FirebaseDatabase.getInstance().getReference();

        initRefeshLayout();

    }

    private void Event() {
        titleBar.setDelegate(new BGATitleBar.Delegate() {
            @Override
            public void onClickLeftCtv() {

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

                            // notify driver
                            //todo: notify Notification
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

        if (request.func_isInTheFuture()) {
            new MaterialDialog.Builder(this)
                    .content(R.string.request_out_of_date)
                    .positiveText(R.string.ok)
                    .titleColor(getResources().getColor(R.color.title_bar_background_color))
                    .positiveColor(getResources().getColor(R.color.title_bar_background_color))
                    .widgetColorRes(R.color.title_bar_background_color)
                    .buttonRippleColorRes(R.color.title_bar_background_color)
                    .show();

            return;
        }
        else if (state == PassengerRequestState.FOUND_DRIVER) {
            if (command.equals("Pause") || command.equals("Delete")) {
                new MaterialDialog.Builder(this)
                        .content(R.string.cancel_request_warning_passenger)
                        .positiveText(R.string.ok)
                        .titleColor(getResources().getColor(R.color.title_bar_background_color))
                        .positiveColor(getResources().getColor(R.color.title_bar_background_color))
                        .widgetColorRes(R.color.title_bar_background_color)
                        .buttonRippleColorRes(R.color.title_bar_background_color)
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
                        .titleColor(getResources().getColor(R.color.title_bar_background_color))
                        .positiveColor(getResources().getColor(R.color.title_bar_background_color))
                        .widgetColorRes(R.color.title_bar_background_color)
                        .buttonRippleColorRes(R.color.title_bar_background_color)
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

    }

    //region -------------- Show Driver Info --------------

    Dialog dialogInfo;

    /**
     * If Online User is in "D_RECEIVING_BOOKING_HH state" and NOT "time out" then get User info as marker
     */

    private void setUpDialogInfo(final UserInfo driverInfo) {
        MaterialFancyButton btnMessage, btnCall;
        TextView tvName, tvPhone;

        dialogInfo = new Dialog(PassengerRequestManagerActivity.this);
        dialogInfo.setContentView(R.layout.info_user);

        btnMessage = dialogInfo.findViewById(R.id.btn_messenger);
        btnCall = dialogInfo.findViewById(R.id.btn_call);
        tvName = dialogInfo.findViewById(R.id.tvName);
        tvPhone = dialogInfo.findViewById(R.id.tvPhone);

        tvName.setText(driverInfo.getName());
        tvPhone.setText("SDT: " + driverInfo.getPhoneNumber());

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Open Messenger", Toast.LENGTH_LONG).show();
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
                        ActivityCompat.requestPermissions(PassengerRequestManagerActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1001);
                    }
                }
            }
        });

        dialogInfo.show();
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
                .titleColor(getResources().getColor(R.color.title_bar_background_color))
                .widgetColorRes(R.color.title_bar_background_color)
                .buttonRippleColorRes(R.color.title_bar_background_color).show();
    }

    private void hideLoadingPassengerRequestInfo() {
        if (loadingPassengerInfo != null)
            loadingPassengerInfo.dismiss();
    }
}
