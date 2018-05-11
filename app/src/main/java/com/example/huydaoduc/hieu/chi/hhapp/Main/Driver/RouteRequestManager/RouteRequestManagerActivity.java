package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.PassengerRequestInfoActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bingoogolapple.titlebar.BGATitleBar;


public class RouteRequestManagerActivity extends AppCompatActivity {

    private static final int CREATE_ROUTE_REQUEST_CODE = 1;
    BGATitleBar titleBar;
    RecyclerView rycv_route_request;
    FloatingActionButton fab_add_route;

    List<RouteRequest> routeRequests;

    DatabaseReference dbRefe;

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_request_manager);

        Init();

        Event();

        refreshList(true);
    }

    private void Init() {
        //view
        fab_add_route = findViewById(R.id.fab_add_route);

        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        // recycler
        initRecyclerView();

        routeRequests = new ArrayList<>();

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

        fab_add_route.setOnClickListener(e ->{
            Intent intent = new Intent(getApplicationContext(), CreateRouteActivity.class);
            RouteRequestManagerActivity.this.startActivityForResult(intent,CREATE_ROUTE_REQUEST_CODE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        handleResultCreateRoute(requestCode, resultCode, data);
    }

    //region -------------- Route request ----------------

    private void handleResultCreateRoute(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ROUTE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String routeRequestUId = data.getStringExtra("routeRequestUId");
                //Listen to Trip Notify - asynchronous with service
                dbRefe.child(Define.DB_ROUTE_REQUESTS).child(getCurUid())
                        .child(routeRequestUId).child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                NotifyTrip notifyTrip = dataSnapshot.getValue(NotifyTrip.class);

                                if(notifyTrip != null && !notifyTrip.isNotified())
                                {
                                    // update NotifyTrip value to notified
                                    notifyTrip.setNotified(true);
                                    dbRefe.child(Define.DB_ROUTE_REQUESTS).child(getCurUid())
                                            .child(routeRequestUId)
                                            .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP).setValue(notifyTrip);

                                    // change Route state
                                    dbRefe.child(Define.DB_ROUTE_REQUESTS).child(getCurUid())
                                            .child(routeRequestUId)
                                            .child(Define.DB_ROUTE_REQUESTS_ROUTE_REQUEST_STATE).setValue(RouteRequestState.FOUND_PASSENGER);

                                    // notify driver
                                    //todo: change to notify
                                    Toast.makeText(getApplicationContext(),"Found your passenger",Toast.LENGTH_LONG).show();
                                    refreshList(false);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                refreshList(false);
            }
        }
    }

    private void changeRouteRequestState(int position, String command) {
        if (command == null) {
            return;
        }

        RouteRequest routeRequest = routeRequests.get(position);
        RouteRequestState state = routeRequest.getRouteRequestState();

        if (! routeRequest.func_isInTheFuture()) {      // time out
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
        else if (state == RouteRequestState.FOUND_PASSENGER) {
            if (command.equals("Pause") || command.equals("Delete")) {
                new MaterialDialog.Builder(this)
                        .content(R.string.cancel_request_warning_driver)
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
            dbRefe.child(Define.DB_ROUTE_REQUESTS)
                    .child(routeRequest.getDriverUId())
                    .child(routeRequest.getRouteRequestUId())
                    .removeValue();

            refreshList(false);
            return;
        }

        if (state == RouteRequestState.FINDING_PASSENGER) {
            if (command.equals("Pause")) {
                new MaterialDialog.Builder(this)
                        .title(R.string.pause_request_title)
                        .content(R.string.pause_request_content_driver)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .titleColor(getResources().getColor(R.color.title_bar_background_color))
                        .positiveColor(getResources().getColor(R.color.title_bar_background_color))
                        .widgetColorRes(R.color.title_bar_background_color)
                        .buttonRippleColorRes(R.color.title_bar_background_color)
                        .onPositive((dialog, which) -> {
                            // change state on server
                            dbRefe.child(Define.DB_ROUTE_REQUESTS)
                                    .child(routeRequest.getDriverUId())
                                    .child(routeRequest.getRouteRequestUId())
                                    .child(Define.DB_ROUTE_REQUESTS_ROUTE_REQUEST_STATE)
                                    .setValue(RouteRequestState.PAUSE);

                            refreshList(false);
                        })
                        .show();
            }
        }
        if (state == RouteRequestState.PAUSE) {
            if (command.equals("Resume")) {
                // change state on server
                dbRefe.child(Define.DB_ROUTE_REQUESTS)
                        .child(routeRequest.getDriverUId())
                        .child(routeRequest.getRouteRequestUId())
                        .child(Define.DB_ROUTE_REQUESTS_ROUTE_REQUEST_STATE)
                        .setValue(RouteRequestState.FINDING_PASSENGER);

                refreshList(false);
                return;
            }
        }
    }

    //endregion


    //region -------------- Notify ----------------

    private void showPassengerInfoActivityItem(int position) {
        showLoadingPassengerRequestInfo("Loading passenger info");

        NotifyTrip notifyTrip = routeRequests.get(position).getNotifyTrip();
        if (notifyTrip == null) {

        }
        else
        {
            String tripUId = notifyTrip.getTripUId();

            DBManager.getTripById( tripUId, trip -> {
                // get Passenger Info
                DBManager.getUserById( trip.getPassengerUId(), userInfo -> {
                    // get Passenger Request
                    DBManager.getPassengerRequestById(trip.getPassengerRequestUId(), trip.getPassengerUId(), passengerRequest -> {

                        Intent intent = new Intent(RouteRequestManagerActivity.this, PassengerRequestInfoActivity.class);
                        intent.putExtra("trip", trip);
                        intent.putExtra("userInfo", userInfo);
                        intent.putExtra("passengerRequest", passengerRequest);

                        RouteRequestManagerActivity.this.startActivity(intent);

                        hideLoadingPassengerRequestInfo();
                    });
                });

            });
        }
    }

    private void notifyTripAndUpdateRequest(RouteRequest request) {
        NotifyTrip notifyTrip = request.getNotifyTrip();
        if(notifyTrip != null && !notifyTrip.isNotified())
        {
            // update NotifyTrip value to notified
            notifyTrip.setNotified(true);
            dbRefe.child(Define.DB_ROUTE_REQUESTS).child(request.getDriverUId())
                    .child(request.getRouteRequestUId())
                    .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP).setValue(notifyTrip);

            // change Route state
            dbRefe.child(Define.DB_ROUTE_REQUESTS).child(request.getDriverUId())
                    .child(request.getRouteRequestUId())
                    .child(Define.DB_ROUTE_REQUESTS_ROUTE_REQUEST_STATE).setValue(RouteRequestState.FOUND_PASSENGER);

            // notify driver
        }
        request.setNotifyTrip(notifyTrip);

    }

    MaterialDialog loadingPassengerInfo;

    private void showLoadingPassengerRequestInfo(String title) {
        loadingPassengerInfo = new MaterialDialog.Builder(this)
                .title(title)
                .content("Please wait...")
                .progress(true, 0)
                .titleColor(getResources().getColor(R.color.title_bar_background_color))
                .widgetColorRes(R.color.title_bar_background_color)
                .buttonRippleColorRes(R.color.title_bar_background_color).show();
    }

    private void hideLoadingPassengerRequestInfo() {
        if (loadingPassengerInfo != null)
            loadingPassengerInfo.dismiss();
    }
    //endregion


    //region -------------- Recycle View ----------------
    private void initRecyclerView() {
        rycv_route_request = findViewById(R.id.recycler_view_route_requests);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rycv_route_request.setLayoutManager(llm);
    }

    private void refreshList(boolean enableLoading) {
        if (enableLoading)
            startLoading();

        FirebaseDatabase.getInstance().getReference().child(Define.DB_ROUTE_REQUESTS)
                .child(getCurUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routeRequests.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RouteRequest request = postSnapshot.getValue(RouteRequest.class);

                    notifyTripAndUpdateRequest(request);

                    routeRequests.add(request);
                }

                Collections.reverse(routeRequests);

                // Route request Row click event
                RouteRequestAdapter adapter = new RouteRequestAdapter(routeRequests);
                adapter.setOnItemChildClickListener((adapter1, view, position) -> {
                    if (view.getId() == R.id.iv_menu) {
                        showTopRightMenuItem(position, view);
                    }
                    if (view.getId() == R.id.btn_request_state) {
                        showPassengerInfoActivityItem(position);

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

    private void showTopRightMenuItem(int position, View view) {
        TopRightMenu topRightMenu;
        topRightMenu = new TopRightMenu(RouteRequestManagerActivity.this);

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

                        changeRouteRequestState(position, command);
                    }
                })
                .showAsDropDown(view, -250, 0);	//带偏移量
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
}
