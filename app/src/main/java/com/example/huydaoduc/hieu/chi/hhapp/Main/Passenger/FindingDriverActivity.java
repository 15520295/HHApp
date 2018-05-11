package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.DirectionManager;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.NotifyTrip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

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

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_driver);

        GetBundle(savedInstanceState);
        Init();
        Event();

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

    private void startBooking() {
        hhMode = true;
        isDriverFound = false;

        String tripUId = trip.getTripUId();

        // Find Driver todo: add car type
        if (hhMode) {
            findMatchingHH(passengerRequest.getTripFareInfo().getCarType(), passengerRequest.getTripFareInfo().func_getStartTimeAsDate(), passengerRequest.getWaitMinute(), passengerRequest.getPickUpSavePlace().func_getLatLngLocation()
                    ,new FindHHCompleteListener() {
                        @Override
                        public void OnLoopThroughAllRequestHH() {
                            synchronized (isDriverFound)
                            {
                                //todo:v2
                                // if loop through all the objects but still not find matching HH request then use normal request
//                                if( ! isDriverFound)
//                                    findNearestDriver(trip);
                                if (! isDriverFound) {
                                    Toast.makeText(getApplicationContext(),"Can't find your driver",Toast.LENGTH_LONG).show();
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        FindingDriverActivity.this.finish();
                                    }
                                }, 2000);
                            }
                        }

                        @Override
                        public void OnFoundDriverRequest(RouteRequest request) {
                            // up trip and passenger request to sever
                            dbRefe.child(Define.DB_TRIPS)
                                    .child(tripUId)
                                    .setValue(trip);
                            dbRefe.child(Define.DB_PASSENGER_REQUESTS)
                                    .child(passengerRequest.getPassengerUId())
                                    .child(passengerRequest.getPassengerRequestUId())
                                    .setValue(passengerRequest);

                            isDriverFound = true;
                            String driverUId = request.getDriverUId();

                            NotifyTrip notifyTrip = new NotifyTrip(tripUId, false);
                            // notify driver thought database
                            dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId)
                                    .child(request.getRouteRequestUId()).child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
                                    .setValue(notifyTrip);

                            //listen to trip state
                            dbRefe.child(Define.DB_TRIPS)
                                    .child(tripUId).child(Define.DB_TRIPS_TRIP_STATE)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                            Log.i(TAG, "Found HH request" + request.getDriverUId());

                            // show driver info
                            Intent returIntent = new Intent();
                            returIntent.putExtra("driverUId",request.getDriverUId());
                            setResult(Activity.RESULT_OK, returIntent);
                            finish();
                        }
                    }
            );
        } else {
            //todo:v2
//            findNearestDriver(trip);
        }
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

    private void findMatchingHH(CarType carType, Date passengerStartTime, int waitMinute, LatLng passengerCurLocation, FindHHCompleteListener listener) {
        DatabaseReference dbRequest = dbRefe.child(Define.DB_ROUTE_REQUESTS);

        dbRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Note: We need to find the matching and the nearest also
                // Because the latency when get the polyline form google server, so
                // we need to sort all HH driver request in nearest other then we can check from that

                // get List from DataSnapshot after filtered and ordered
                List<RouteRequest> routeRequestsFiltered = filterAndOrderingRequestList(dataSnapshot, passengerStartTime, waitMinute, passengerCurLocation, carType);

                checkListDriverRequest(routeRequestsFiltered, passengerStartTime, waitMinute, listener);

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

    private List<RouteRequest> filterAndOrderingRequestList(DataSnapshot listDriverRequestDS,
                                                            Date passengerStartTime,
                                                            int waitMinute,
                                                            LatLng nearLocation,
                                                            CarType carType) {
        List<RouteRequest> requestList = new ArrayList();

        // get list from database
        for (DataSnapshot postSnapshot: listDriverRequestDS.getChildren()) {
            for (DataSnapshot requestSnapshot : postSnapshot.getChildren()) {
                RouteRequest request = requestSnapshot.getValue(RouteRequest.class);

                if (request.getRouteRequestState() == RouteRequestState.FINDING_PASSENGER
                        && request.getCarType() == carType
                        && request.func_isInTheFuture()) {
//                    LatLng latLng_startLocation = request.getStartPlace().func_getLatLngLocation();
//                    // check validate HH request before add to list
//                    if ((LocationUtils.calcDistance(latLng_startLocation, mLastLocation) < limitHHRadius)
//                            && !request.func_isInTheFuture(Define.DRIVER_REQUESTS_TIMEOUT)) {
//                        requestList.add(request);
//                    }

                    // estimate check the request start time to the passenger start time is in the waitMinute
                    Date requestStartTime = TimeUtils.strToDate(request.getStartTime());
                    if(TimeUtils.getPassTime(passengerStartTime, requestStartTime) < waitMinute*60)
                        requestList.add(request);

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
    private void checkListDriverRequest(List<RouteRequest> routeRequestsFiltered, Date passengerStartTime, int waitMinute, FindHHCompleteListener listener) {

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
