package com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinderListener;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.DirectionManager;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerRequestManager.PassengerRequestManagerActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripState;
import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FindingDriverActivity extends AppCompatActivity {

    private static final String TAG = "FindingDriverActivity";
    private DatabaseReference dbRefe;
    private DirectionManager directionManager;

    private TextView tv_start_address, tv_end_address, tv_estimate_fare;
    private MaterialFancyButton btn_cancel;
    private AVLoadingIndicatorView prb_finding_passenger;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_driver);

        GetBundle(savedInstanceState);
        Init();
        Event();

        // set finding animation to center
        View r =  findViewById(R.id.frame_layout);
        r.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams mParams;
                mParams = (RelativeLayout.LayoutParams) r.getLayoutParams();
                mParams.width = r.getHeight();
                r.setLayoutParams(mParams);
                r.postInvalidate();
            }
        });

        TextView tv_estimate_fare_note = findViewById(R.id.tv_estimate_fare_note);
        tv_estimate_fare_note.setSelected(true);

        startBooking();
    }

    Trip trip;
    PassengerRequest passengerRequest;

    private void GetBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                trip = null;
                passengerRequest = null;
            } else {
                trip = (Trip) getIntent().getParcelableExtra("trip");
                passengerRequest = (PassengerRequest) getIntent().getParcelableExtra("passengerRequest");
            }
        } else {
            trip = (Trip) savedInstanceState.getParcelable("trip");
            passengerRequest = (PassengerRequest) savedInstanceState.getParcelable("passengerRequest");
        }

        if (trip == null || passengerRequest == null) {
            finish();
        }
    }

    private void Init() {
        // View
        tv_start_address = findViewById(R.id.tv_pick_up_address);
        tv_end_address = findViewById(R.id.tv_drop_off_address);
        btn_cancel = findViewById(R.id.btn_cancel);
        tv_estimate_fare = findViewById(R.id.tv_estimate_fare);

        prb_finding_passenger = findViewById(R.id.prb_finding_passenger);

        // init value
        tv_start_address.setText(passengerRequest.getPickUpSavePlace().getPrimaryText());
        tv_end_address.setText(passengerRequest.getDropOffSavePlace().getPrimaryText());
        tv_estimate_fare.setText(passengerRequest.getTripFareInfo().func_getEstimateFareText());

        directionManager = new DirectionManager(getApplicationContext());

        // Init firebase
        dbRefe = FirebaseDatabase.getInstance().getReference();
    }

    private void Event() {
        btn_cancel.setOnClickListener(v ->{
            cancelRequest();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelRequest();
    }

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    //region ------ Start Booking --------

    Boolean isDriverFound;      // --> use for synchronous purpose
    boolean hhMode;

    // NOTE: only put trip when found driver
    private void startBooking() {
        hhMode = true;
        isDriverFound = false;

        // Find Driver todo: add car type
        if (hhMode) {
            findMatchingHH(passengerRequest,new FindHHCompleteListener() {
                        @Override
                        public void OnLoopThroughAllRequestHH() {
                            synchronized (isDriverFound)
                            {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        handleNotFoundDriver();
                                    }
                                }, 2000);
                            }
                        }

                        @Override
                        public void OnFoundDriverRequest(RouteRequest routeRequest) {
                            Log.i(TAG, "Found HH request" + routeRequest.getDriverUId());

                            // up trip and passenger request to sever
                            trip.setRouteRequestUId(routeRequest.getRouteRequestUId());
                            trip.setDriverUId(routeRequest.getDriverUId());
                            trip.setTripState(TripState.ACCEPTED);
                            dbRefe.child(Define.DB_TRIPS)
                                    .child(trip.getTripUId())
                                    .setValue(trip);

                            // change passenger Request state and notify passenger
                            passengerRequest.setPassengerRequestState(PassengerRequestState.FOUND_DRIVER);
                            passengerRequest.setNotifyTrip(new NotifyTrip(trip.getTripUId(),true));
                            dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                                    .child(passengerRequest.getPassengerUId())
                                    .child(passengerRequest.getPassengerRequestUId())
                                    .setValue(passengerRequest);

                            // change driver Request state and notify driver
                            routeRequest.setRouteRequestState(RouteRequestState.FOUND_PASSENGER);
                            routeRequest.setNotifyTrip(new NotifyTrip(trip.getTripUId(),false));
                            dbRefe.child(Define.DB_ROUTE_REQUESTS)
                                    .child(routeRequest.getDriverUId())
                                    .child(routeRequest.getRouteRequestUId())
                                    .setValue(routeRequest);

                            Intent intent = new Intent(getApplicationContext(), PassengerRequestManagerActivity.class);
                            intent.putExtra("routeRequest",routeRequest);
                            intent.putExtra("passengerRequest",passengerRequest);
                            FindingDriverActivity.this.startActivity(intent);
                            finish();
                        }
                    }
            );
        } else {
            //todo:v2
//            findNearestDriver(trip);
        }
    }

    private void handleNotFoundDriver() {
        //todo:v2
        // if loop through all the objects but still not find matching HH request then use normal request
//                                if( ! isDriverFound)
//                                    findNearestDriver(trip);
        if (! isDriverFound) {
            // show suggestion
            if(! this.isFinishing())
                new MaterialDialog.Builder(FindingDriverActivity.this)
                        .content(R.string.add_waiting_list_content)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                        .positiveColor(getResources().getColor(R.color.title_bar_background_color_blue))
                        .widgetColorRes(R.color.title_bar_background_color_blue)
                        .buttonRippleColorRes(R.color.title_bar_background_color_blue)
                        .onPositive((dialog, which) -> {
                            addPassengerRequestToWaitingList();
                        })
                        .onNegative((dialog, which) -> {
                            cancelRequest();
                        })
                        .show();
        }
    }

    private void addPassengerRequestToWaitingList() {
        // add request to waiting list
        dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                .child(passengerRequest.getPassengerUId())
                .child(passengerRequest.getPassengerRequestUId())
                .setValue(passengerRequest);

        Intent intent = new Intent(getApplicationContext(), PassengerRequestManagerActivity.class);
        intent.putExtra("passengerRequest", passengerRequest);
        FindingDriverActivity.this.startActivity(intent);
        finish();
    }


    //region ------------ Find matching Active Driver
//    interface FindActiveDriverListener {
//        void OnLoopThroughAllRequestHH();
//        void OnFoundDriverRequest(RouteRequest request);
//    }
//
//
//    private void findNearestDriver(Trip trip) {
//
//        startFindActiveDriver();
//    }
    //endregion


    //region ------------ Find matching HH request

    // for synchronous purpose use interface and synchronized keyword
    // interface for raise loop thought all list event
    // synchronized keyword for locking the Boolean variable
    interface FindHHCompleteListener {
        void OnLoopThroughAllRequestHH();
        void OnFoundDriverRequest(RouteRequest request);
    }

    private void findMatchingHH(PassengerRequest passengerRequest, FindHHCompleteListener listener) {

        DatabaseReference dbRequest = dbRefe.child(Define.DB_ROUTE_REQUESTS);

        dbRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Note: We need to find the matching and the nearest also
                // Because the latency when get the polyline form google server, so
                // we need to sort all HH driver request in nearest other then we can check from that

                // get List from DataSnapshot after filtered and ordered
                List<RouteRequest> routeRequestsFiltered = filterAndOrderingRequestList(dataSnapshot, passengerRequest);

                checkListDriverRequest(routeRequestsFiltered, passengerRequest, listener);

                // if list null raise event
                if (routeRequestsFiltered.size() == 0) {
                    listener.OnLoopThroughAllRequestHH();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo: handle error
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private List<RouteRequest> filterAndOrderingRequestList(DataSnapshot listDriverRequestDS, PassengerRequest passengerRequest) {
        Date passengerStartTime = passengerRequest.getTripFareInfo().func_getStartTimeAsDate();
        int waitMinute = passengerRequest.getWaitMinute();
        LatLng nearLocation = passengerRequest.getPickUpSavePlace().func_getLatLngLocation();
        CarType carType = passengerRequest.getTripFareInfo().getCarType();

        List<RouteRequest> requestList = new ArrayList();

        // get list from database
        for (DataSnapshot postSnapshot: listDriverRequestDS.getChildren()) {
            for (DataSnapshot requestSnapshot : postSnapshot.getChildren()) {
                RouteRequest routeRequest = requestSnapshot.getValue(RouteRequest.class);

                long passTimeBetweenDrAndPas = TimeUtils.getPassTime(routeRequest.func_getStartTimeAsDate(),passengerRequest.getTripFareInfo().func_getStartTimeAsDate())/60;
                int waitMin = passengerRequest.getWaitMinute();

                if (routeRequest.getRouteRequestState() == RouteRequestState.FINDING_PASSENGER
                        && routeRequest.getCarType() == carType
                        && routeRequest.func_isInTheFuture()
                        && ! routeRequest.getDriverUId().equals(getCurUid())
                        && passTimeBetweenDrAndPas < waitMin) {
//                    LatLng latLng_startLocation = request.getStartPlace().func_getLatLngLocation();
//                    // check validate HH request before add to list
//                    if ((LocationUtils.calcDistance(latLng_startLocation, mLastLocation) < limitHHRadius)
//                            && !request.func_isInTheFuture(Define.DRIVER_REQUESTS_TIMEOUT)) {
//                        requestList.add(request);
//                    }

                    // estimate check the request start time to the passenger start time is in the waitMinute
                    Date requestStartTime = routeRequest.func_getStartTimeAsDate();
                    if(TimeUtils.getPassTime(passengerStartTime, requestStartTime) < waitMinute*60)
                        requestList.add(routeRequest);

                }
            }
        }

        // sort list to nearest to passenger current location
        Collections.sort(requestList, (dq1, dq2) ->{
            double distance1 = LocationUtils.calcDistance(dq1.getStartPlace().func_getLatLngLocation(), nearLocation);
            double distance2 = LocationUtils.calcDistance(dq2.getStartPlace().func_getLatLngLocation(), nearLocation);

            if(distance1 < distance2)
                return -1;
            else if (distance1 == distance2)
                return 0;
            else
                return 1;
        } );

        return requestList;
    }

    //todo : check if user swich off
    //todo : Check driver state

    /**
     * check if Pickup Place and End Place match to the Request Polyline
     * isDriverFound --> use for synchronous purpose
     */
    private void checkListDriverRequest(List<RouteRequest> routeRequestsFiltered,PassengerRequest passengerRequest , FindHHCompleteListener listener) {

        Date passengerStartTime = passengerRequest.getTripFareInfo().func_getStartTimeAsDate();
        int waitMinute = passengerRequest.getWaitMinute();

        // loop the the list and find matching request if not found raise the loop thought listener
        // if found run foundDriver method
        for (int i = 0; i < routeRequestsFiltered.size(); i++) {
            // if driver found break the check loop
            synchronized (isDriverFound) {
                if(isDriverFound)
                    break;
            }
            final RouteRequest request = routeRequestsFiltered.get(i);
            int itemIndex = i;

            //get Location From Request
            LatLng latLng_startLocation = request.getStartPlace().func_getLatLngLocation();
            LatLng latLng_endLocation = request.getEndPlace().func_getLatLngLocation();

            // check if wait minute is accepted
            directionManager.findPath(latLng_startLocation, passengerRequest.getPickUpSavePlace().func_getLatLngLocation(),
                    new DirectionFinderListener() {
                        @Override
                        public void onDirectionFinderStart() {

                        }

                        @Override
                        public void onDirectionFinderSuccess(List<Route> routes) {
                            // routeDurationSec = time depend on route from driver to passenger
                            // startTimeInterval = passenger start time - request start time
                            // timeToWait = total time passenger have to wait
                            long routeDurationSec = routes.get(0).getLegs().get(0).getDuration().getValue();
                            long startTimeInterval = TimeUtils.getPassTime(passengerStartTime, TimeUtils.strToDate(request.getStartTime()));
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
                                                                listener.OnFoundDriverRequest(request);
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

    //endrigon



    //endregion


    //region ------------ Cancel Request

    private void cancelRequest() {
        dbRefe.child(Define.DB_TRIPS)
                .child(trip.getTripUId())
                .removeValue();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    //endregion

    //endregion

}
