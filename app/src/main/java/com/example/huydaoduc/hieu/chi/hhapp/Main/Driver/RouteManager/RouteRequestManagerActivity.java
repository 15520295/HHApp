package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteManager;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.CreateRouteActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.titlebar.BGATitleBar;


public class RouteRequestManagerActivity extends AppCompatActivity {

    BGATitleBar titleBar;
    RecyclerView rv_route_request;
    FloatingActionButton fab_add_route;

    List<RouteRequest> routeRequests;


    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_request_manager);

        Init();

        Event();

        FirebaseDatabase.getInstance().getReference().child(Define.DB_ROUTE_REQUESTS)
                .child(getCurUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RouteRequest request = postSnapshot.getValue(RouteRequest.class);

                    routeRequests.add(request);
                }

                RouteRequestAdapter adapter = new RouteRequestAdapter(routeRequests);
                rv_route_request.setAdapter(adapter);

                findViewById(R.id.prb_loading_route).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Init() {
        //view
        fab_add_route = findViewById(R.id.fab_add_route);

        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        // recycler
        rv_route_request = findViewById(R.id.recycler_view_route_requests);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_route_request.setLayoutManager(llm);

        routeRequests = new ArrayList<>();
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
            RouteRequestManagerActivity.this.startActivity(intent);
        });
    }

}
