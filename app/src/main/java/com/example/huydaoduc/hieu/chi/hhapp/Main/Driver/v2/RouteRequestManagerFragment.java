package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.v2;

import android.app.Activity;
import android.content.Context;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.PassengerRequestInfoActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager.CreateRouteActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager.RouteRequestAdapter;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.NotifyService;
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

public class RouteRequestManagerFragment extends Fragment {
    private OnRouteRequestManagerFragmentListener mListener;
    public interface OnRouteRequestManagerFragmentListener {
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRouteRequestManagerFragmentListener) {
            mListener = (OnRouteRequestManagerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRouteRequestManagerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public RouteRequestManagerFragment() {
    }

    public static RouteRequestManagerFragment newInstance(String param1, String param2) {
        RouteRequestManagerFragment fragment = new RouteRequestManagerFragment();
        Bundle args = new Bundle();

//        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    private static final int CREATE_ROUTE_REQUEST_CODE = 1;
    FloatingActionButton fab_add_route;

    RecyclerView rycv_route_request;
    List<RouteRequest> routeRequests;

    DatabaseReference dbRefe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_request_manager, container, false);

        Init(view);

        Event(view);

        refreshList(true);

        return view;
    }

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void Init(View view) {
        //view
        fab_add_route = view.findViewById(R.id.fab_add_request);

        // recycler
        initRecyclerView(view);

        routeRequests = new ArrayList<>();

        // init database
        dbRefe = FirebaseDatabase.getInstance().getReference();

        initRefeshLayout(view);

    }

    private void Event(View view) {
        fab_add_route.setOnClickListener(e ->{
            Intent intent = new Intent(getContext(), CreateRouteActivity.class);
            startActivityForResult(intent,CREATE_ROUTE_REQUEST_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                                    //todo: notify Notification
                                    Toast.makeText(getContext(),"Found your passenger",Toast.LENGTH_LONG).show();
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
            new MaterialDialog.Builder(getContext())
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
                new MaterialDialog.Builder(getContext())
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
                new MaterialDialog.Builder(getContext())
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

                        Intent intent = new Intent(getContext(), PassengerRequestInfoActivity.class);
                        intent.putExtra("trip", trip);
                        intent.putExtra("userInfo", userInfo);
                        intent.putExtra("passengerRequest", passengerRequest);

                        startActivity(intent);

                        hideLoadingPassengerRequestInfo();
                    });
                });

            });
        }
    }

    public void showNotificationforDriver(UserInfo userInfo, Trip trip) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_location_on)
                        .setContentTitle(userInfo.getName())
                        .setContentText(userInfo.getPhoneNumber());

        Intent resultIntent = new Intent(getApplicationContext(), PassengerRequestInfoActivity.class);

        resultIntent.putExtra("trip", trip);
        resultIntent.putExtra("userInfo", userInfo);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
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

        int mNotificationId = 0;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
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
        loadingPassengerInfo = new MaterialDialog.Builder(getContext())
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
    private void initRecyclerView(View view) {
        rycv_route_request = view.findViewById(R.id.recycler_view_requests);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
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
        topRightMenu = new TopRightMenu(getActivity());

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
                .showAsDropDown(view, -250, 0);
    }

    //endregion

    //region -------------- Smart Refresh Layout ----------------

    SmartRefreshLayout smartRefreshLayout;

    private void initRefeshLayout(View view) {
        smartRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
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
