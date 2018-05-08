package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteManager;

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
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
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
    CircleProgressBar loadingProgress;

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

        refreshList();
    }

    private void Init() {
        //view
        fab_add_route = findViewById(R.id.fab_add_route);

        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        loadingProgress = findViewById(R.id.prb_loading_route);
        loadingProgress.setVisibility(View.GONE);


        // recycler
        rycv_route_request = findViewById(R.id.recycler_view_route_requests);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rycv_route_request.setLayoutManager(llm);

        routeRequests = new ArrayList<>();

        // init database
        dbRefe = FirebaseDatabase.getInstance().getReference();
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

    private void handleResultCreateRoute(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ROUTE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // add listener//todo: redo
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
                                    Toast.makeText(getApplicationContext(),"Found your driver",Toast.LENGTH_LONG).show();
                                    refreshList();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                refreshList();
            }
        }
    }

    private void refreshList() {
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

                // btn_request_state click event
                RouteRequestAdapter adapter = new RouteRequestAdapter(routeRequests);
                adapter.setOnItemChildClickListener((adapter1, view, position) -> {
                    if (view.getId() == R.id.iv_menu) {
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
                    if (view.getId() == R.id.btn_request_state) {
                        NotifyTrip notifyTrip = routeRequests.get(position).getNotifyTrip();
                        if (notifyTrip == null) {

                        }
                        else
                        {
                            Intent intent = new Intent(RouteRequestManagerActivity.this, PassengerRequestInfoActivity.class);
                            intent.putExtra("tripUId", notifyTrip.getTripUId());
                            RouteRequestManagerActivity.this.startActivity(intent);
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

    private void changeRouteRequestState(int position, String command) {
        if (command == null) {
            return;
        }

        RouteRequest routeRequest = routeRequests.get(position);
        RouteRequestState state = routeRequest.getRouteRequestState();

        if (state == RouteRequestState.TIME_OUT) {
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
                        .content(R.string.cancel_request_warning)
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

            refreshList();

            return;
        }


        if (state == RouteRequestState.FINDING_PASSENGER) {
            if (command.equals("Pause")) {
                new MaterialDialog.Builder(this)
                        .title(R.string.pause_request_title)
                        .content(R.string.pause_request_content)
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

                            refreshList();
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

                refreshList();
            }
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

    private void startLoading() {
        loadingProgress.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        loadingProgress.setVisibility(View.GONE);
    }


}
