package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.UpdateInfoActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.DirectionManager;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.ImageUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Main.AboutApp;
import com.example.huydaoduc.hieu.chi.hhapp.Main.AboutUser;
import com.example.huydaoduc.hieu.chi.hhapp.Main.CurUserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.PassengerRequestInfoActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Main.SplashActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.maps.model.LatLng;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.titlebar.BGATitleBar;


public class RouteRequestManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int CREATE_ROUTE_REQUEST_CODE = 1;
    private static final String TAG = "RouteRequestManagerAct";
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
        // init database
        dbRefe = FirebaseDatabase.getInstance().getReference();

        Init();

        Event();

        refreshList(true);
    }

    private void Init() {
        //view
        fab_add_route = findViewById(R.id.fab_add_request);

        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        // recycler
        initNavigation();

        initRecyclerView();

        routeRequests = new ArrayList<>();


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

        fab_add_route.setOnClickListener(e ->{
            Intent intent = new Intent(getApplicationContext(), CreateRouteActivity.class);
            RouteRequestManagerActivity.this.startActivityForResult(intent,CREATE_ROUTE_REQUEST_CODE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        handleResultCreateRoute(requestCode, resultCode, data);

        NavigationResultHandle(requestCode, resultCode, data);
    }



    //region -------------- Route request & Change Route Request State ----------------

    private void handleResultCreateRoute(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ROUTE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                RouteRequest routeRequest = data.getParcelableExtra("routeRequest");

                //Listen to Trip Notify - asynchronous with service
                dbRefe.child(Define.DB_ROUTE_REQUESTS)
                        .child(getCurUid())
                        .child(routeRequest.getRouteRequestUId())
                        .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                NotifyTrip notifyTrip = dataSnapshot.getValue(NotifyTrip.class);

                                if(notifyTrip != null && !notifyTrip.isNotified())
                                {
                                    notifyTrip.setNotified(true);
                                    dbRefe.child(Define.DB_ROUTE_REQUESTS)
                                            .child(routeRequest.getDriverUId())
                                            .child(routeRequest.getRouteRequestUId())
                                            .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
                                            .setValue(notifyTrip);

                                    // notify driver
                                    //todo: notify Notification
                                    Toast.makeText(getApplicationContext(),"Found your passenger",Toast.LENGTH_LONG).show();
                                    refreshList(false);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                // Run finding waiting Passenger Request in asynchronous
                findMatchingPassengerRequest(routeRequest);

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
            if (command == "Delete") {
                // change state on server
                dbRefe.child(Define.DB_ROUTE_REQUESTS)
                        .child(routeRequest.getDriverUId())
                        .child(routeRequest.getRouteRequestUId())
                        .removeValue();

                refreshList(false);
            } else {
                new MaterialDialog.Builder(this)
                        .content(R.string.request_out_of_date)
                        .positiveText(R.string.ok)
                        .titleColor(getResources().getColor(R.color.title_bar_background_color))
                        .positiveColor(getResources().getColor(R.color.title_bar_background_color))
                        .widgetColorRes(R.color.title_bar_background_color)
                        .buttonRippleColorRes(R.color.title_bar_background_color)
                        .show();
            }


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

    //region -------------- Asynchronous Finding Waiting Passenger request ----------------

    private void findMatchingPassengerRequest(RouteRequest routeRequest) {
        String tripUId = dbRefe.child(Define.DB_TRIPS).push().getKey();

        Trip trip = Trip.Builder.aTrip(tripUId)
                .setDriverUId(getCurUid())
                .setTripState(TripState.WAITING_ACCEPT)
                .setTripType(TripType.HH)
                .setRouteRequestUId(routeRequest.getRouteRequestUId())
                .build();

        new FindPassengerRequestAsynchronous(getApplicationContext(), routeRequest, new FindPassengerRequestAsynchronous.FindHHCompleteListener() {
            @Override
            public void OnLoopThroughAllRequestHH() {

            }

            @Override
            public void OnFoundDriverRequest(PassengerRequest passengerRequest) {
                DatabaseReference dbRefe = FirebaseDatabase.getInstance().getReference();
                // up trip and passenger request to sever
                trip.setRouteRequestUId(passengerRequest.getPassengerRequestUId());
                trip.setTripState(TripState.ACCEPTED);
                trip.setPassengerUId(passengerRequest.getPassengerUId());
                trip.setPassengerRequestUId(passengerRequest.getPassengerRequestUId());
                dbRefe.child(Define.DB_TRIPS)
                        .child(trip.getTripUId())
                        .setValue(trip);

                // notify passenger and change passenger state
                passengerRequest.setPassengerRequestState(PassengerRequestState.FOUND_DRIVER);
                passengerRequest.setNotifyTrip(new NotifyTrip(trip.getTripUId(),false));
                dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                        .child(passengerRequest.getPassengerUId())
                        .child(passengerRequest.getPassengerRequestUId())
                        .setValue(passengerRequest);

                // notify driver and change passenger state
                routeRequest.setRouteRequestState(RouteRequestState.FOUND_PASSENGER);
                routeRequest.setNotifyTrip(new NotifyTrip(trip.getTripUId(),false));
                dbRefe.child(Define.DB_ROUTE_REQUESTS)
                        .child(routeRequest.getDriverUId())
                        .child(routeRequest.getRouteRequestUId())
                        .setValue(routeRequest);
            }
        }).execute("");
    }



    private static class FindPassengerRequestAsynchronous extends AsyncTask<String, Void, String> {

        RouteRequest routeRequest;

        private WeakReference<Context> contextReference;
        FindHHCompleteListener listener;

        public FindPassengerRequestAsynchronous(Context context, RouteRequest routeRequest, FindHHCompleteListener listener) {
            super();
            contextReference = new WeakReference<>(context);
            this.routeRequest = routeRequest;
            this.listener = listener;
        }

        interface FindHHCompleteListener {
            void OnLoopThroughAllRequestHH();
            void OnFoundDriverRequest(PassengerRequest request);
        }

        @Override
        protected String doInBackground(String... params) {
            findMatchingHH(routeRequest,listener);
            return "Executed";
        }

        // for synchronous purpose use interface and synchronized keyword
        // interface for raise loop thought all list event
        // synchronized keyword for locking the Boolean variable
        Boolean isDriverFound = false;      // --> use for synchronous purpose

        private void findMatchingHH(RouteRequest routeRequest, FindHHCompleteListener listener) {
            // if context is delete then return
            Context context = contextReference.get();
            if (context == null || context.isRestricted())
                return;

            DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference().child(Define.DB_PASSENGER_REQUESTS);
            LatLng nearLocation = routeRequest.getStartPlace().func_getLatLngLocation();

            dbRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Note: We need to find the matching and the nearest also
                    // Because the latency when get the polyline form google server, so
                    // we need to sort all HH driver request in nearest other then we can check from that


                    // Step 1:  get List from DataSnapshot and filter with the condition and order the list
                    List<PassengerRequest> passegnerRequestsFiltered = filterAndOrderingRequestList(dataSnapshot, routeRequest, nearLocation);

                    // Step 2: check the list to get the matching passenger request
                    checkListPassengerRequest(passegnerRequestsFiltered, routeRequest, listener);

                    // if list null raise event
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //todo: handle error
                    Log.e(TAG, databaseError.getMessage());
                }
            });
        }

        private List<PassengerRequest> filterAndOrderingRequestList(DataSnapshot listDriverRequestDS, RouteRequest routeRequest, LatLng nearLocation) {
            List<PassengerRequest> requestList = new ArrayList();

            // get list from database
            for (DataSnapshot postSnapshot: listDriverRequestDS.getChildren()) {
                for (DataSnapshot requestSnapshot : postSnapshot.getChildren()) {
                    PassengerRequest passengerRequest = requestSnapshot.getValue(PassengerRequest.class);

                    // check all the condition
                    if (passengerRequest.getPassengerRequestState() == PassengerRequestState.FINDING_DRIVER
                            && passengerRequest.getTripFareInfo().getCarType() == routeRequest.getCarType()
                            && passengerRequest.func_isInTheFuture())
                    {
                        Date passengerRequestStartTime = passengerRequest.func_getStartTimeAsDate();
                        Date driverRequestStartTime = routeRequest.func_getStartTimeAsDate();
                        int waitMinute = passengerRequest.getWaitMinute();

                        // first estimate check time ( still have one more check in checkListPassengerRequest)
                        if (TimeUtils.getPassTime(passengerRequestStartTime, driverRequestStartTime) < waitMinute * 60) {
                            requestList.add(passengerRequest);
                        }
                    }
                }
            }

            // sort list to nearest to passenger current location
            Collections.sort(requestList, (dq1, dq2) ->{
                double distance1 = LocationUtils.calcDistance(dq1.getPickUpSavePlace().func_getLatLngLocation(), nearLocation);
                double distance2 = LocationUtils.calcDistance(dq2.getPickUpSavePlace().func_getLatLngLocation(), nearLocation);

                if(distance1 < distance2)
                    return -1;
                else if (distance1 == distance2)
                    return 0;
                else
                    return 1;
            } );

            return requestList;
        }

        /**
         * check if Pickup Place and End Place match to the Request Polyline
         * isDriverFound --> use for synchronous purpose
         */
        private void checkListPassengerRequest(List<PassengerRequest> routeRequestsFiltered, RouteRequest routeRequest, FindHHCompleteListener listener) {

            // loop the the list and find matching request if not found raise the loop thought listener
            // if found run foundDriver method
            for (int i = 0; i < routeRequestsFiltered.size(); i++) {
                // if driver found break the check loop
                synchronized (isDriverFound) {
                    if(isDriverFound)
                        break;
                }
                final PassengerRequest passengerRequest = routeRequestsFiltered.get(i);
                int waitMinute = passengerRequest.getWaitMinute();
                Date passengerStartTime = passengerRequest.getTripFareInfo().func_getStartTimeAsDate();

                int itemIndex = i;

                //get Location From Request
                LatLng latLng_startLocation = routeRequest.getStartPlace().func_getLatLngLocation();
                LatLng latLng_endLocation = routeRequest.getEndPlace().func_getLatLngLocation();

                // if context is delete then return, else keep moving
                Context context = contextReference.get();
                if (context == null || context.isRestricted())
                    return;
                DirectionManager directionManager = new DirectionManager(context);

                // check if wait minute is accepted
                directionManager.findPath(latLng_startLocation, passengerRequest.getPickUpSavePlace().func_getLatLngLocation(),
                        new DirectionFinderListener() {
                            @Override
                            public void onDirectionFinderStart() {

                            }
                            @Override
                            public void onDirectionFinderSuccess(List<Route> routes) {
                                // routeDurationSec = time depend on route from "driver Start point" to "passenger Pickup point"
                                // startTimeInterval = passenger start time - request start time
                                // timeToWait = total time passenger have to wait
                                long routeDurationSec = routes.get(0).getLegs().get(0).getDuration().getValue();
                                long startTimeInterval = TimeUtils.getPassTime(passengerStartTime, routeRequest.func_getStartTimeAsDate());
                                long timeToWaitSec = routeDurationSec + startTimeInterval;

                                if(timeToWaitSec < waitMinute*60)
                                {
                                    // check limit radius --> if out off range will not check
                                    // find the Direction depend on Request
                                    directionManager.findPath(latLng_startLocation, latLng_endLocation,
                                            new DirectionFinderListener() {
                                                @Override
                                                public void onDirectionFinderStart() {

                                                }

                                                @Override
                                                public void onDirectionFinderSuccess(List<Route> routes) {
                                                    // isDriverFound --> for synchronous purpose
                                                    if (!isDriverFound && routes.size() > 0) {
                                                        {
                                                            // get Driver Request Polyline
                                                            final List<LatLng> polyline = LocationUtils.getPointsFromRoute(routes.get(0));

                                                            // check if Pickup Place and End Place match to the Polyline
                                                            boolean isMatch = LocationUtils.isMatching(polyline,
                                                                    passengerRequest.getPickUpSavePlace().func_getLatLngLocation(),
                                                                    passengerRequest.getDropOffSavePlace().func_getLatLngLocation(),
                                                                    500);

                                                            synchronized (isDriverFound) {
                                                                if (isMatch && !isDriverFound) {
                                                                    isDriverFound = true;
                                                                    listener.OnFoundDriverRequest(passengerRequest);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // raise event loop thought all request if last element
                                                    if (routeRequestsFiltered.size() == itemIndex + 1) {
                                                        listener.OnLoopThroughAllRequestHH();
                                                        Log.i(TAG, "Loop thought all HH request");
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        }
    }


    //endregion

    //region -------------- Notify & show Passenger Request Info ----------------

    private void showPassengerInfoActivityItem(int position) {
        if (routeRequests.get(position).getRouteRequestState() != RouteRequestState.FOUND_PASSENGER)
            return;

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

    private void checkRequestNotify(RouteRequest request) {
        NotifyTrip notifyTrip = request.getNotifyTrip();
        if(notifyTrip != null && !notifyTrip.isNotified())
        {
            // update NotifyTrip value to notified
            notifyTrip.setNotified(true);
            dbRefe.child(Define.DB_ROUTE_REQUESTS)
                    .child(request.getDriverUId())
                    .child(request.getRouteRequestUId())
                    .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
                    .setValue(notifyTrip);

            // todo: notification
            Toast.makeText(getApplicationContext(), "found driver", Toast.LENGTH_LONG).show();
            refreshList(false);
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

        FirebaseDatabase.getInstance().getReference().child(Define.DB_ROUTE_REQUESTS)
                .child(getCurUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routeRequests.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    RouteRequest request = postSnapshot.getValue(RouteRequest.class);

                    checkRequestNotify(request);

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
            Intent i = new Intent(RouteRequestManagerActivity.this, AboutUser.class);
            RouteRequestManagerActivity.this.startActivityForResult(i, ABOUT_USER_REQUEST_CODE);
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
            Intent i = new Intent(RouteRequestManagerActivity.this, AboutApp.class);
            RouteRequestManagerActivity.this.startActivity(i);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(RouteRequestManagerActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_share){

            Intent i = new Intent(Intent.ACTION_SEND);

            i.setType("text/plain");
            String shareBody = "link";
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
