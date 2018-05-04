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

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.PassengerRequestInfoActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.NotifyTrip;
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

                    NotifyTrip notifyTrip = routeRequests.get(position).getNotifyTrip();
                    if (notifyTrip == null) {

                    }
                    else
                    {
//                        if (view == adapter.getViewByPosition(position, R.id.btn_request_state)) {
//
//                        }
                        Intent intent = new Intent(RouteRequestManagerActivity.this, PassengerRequestInfoActivity.class);
                        intent.putExtra("tripUId", notifyTrip.getTripUId());
                        RouteRequestManagerActivity.this.startActivity(intent);
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
