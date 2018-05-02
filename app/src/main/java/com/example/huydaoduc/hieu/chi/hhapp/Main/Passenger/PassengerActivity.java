package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SearchActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.NotifyTrip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripStyle;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.CarType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.OnlineUser;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserState;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

//todo: check 1 tai khoan dang nhap 2 may
//todo: handle every database setValue error with one listener : https://www.youtube.com/watch?v=qG14-gpjwMM
public class PassengerActivity extends SimpleMapActivity
        implements
        SimpleMapActivity.SimpleMapListener,
        NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = "PassengerActivity";

    MaterialAnimatedSwitch locationRider_switch;

    private Button btnPost, btnFindDriver, btnMessage, btnCall;
    private TextView tvName, tvPhone;

    //------------------------------------ Chi :

    // Activity Property
    private UserState userState;

    Dialog dialogInfo;
    DatabaseReference dbRefe;


    @Override
    public void OnRealTimeLocationUpdate() {

    }

    @Override
    public void OnMapSetupDone() {

    }


    //region ------ Real time checking --------


    public void realTimeChecking() {
//        if(userState == UserState.P_FINDING_DRIVER)
//            realTimeChecking_PassengerRequest();
    }

    private void updateOnlineUserInfo() {
        OnlineUser onlineUser = new OnlineUser(getCurUid(), mLastLocation, userState);
        dbRefe.child(Define.DB_ONLINE_USERS).child(getCurUid()).setValue(onlineUser);
    }

    //endregion

    //region ------ Start Booking --------

    Boolean isDriverFound;      // --> use for synchronous purpose
    boolean notFoundHH;      // --> use for synchronous purpose
    boolean hhMode;

    Float estimateFare;

    private void startBooking() {
        //todo: put hhMode to screen
        hhMode = true;
        notFoundHH = false;

        estimateFare = 10000f;
        int waitMinute = 10;
        float distance = 1000;
        float duration = 1000;


        // create a trip
        String tripUId = dbRefe.child(Define.DB_TRIPS).push().getKey();

        Trip trip = Trip.Builder.aTrip(tripUId)
                .setPassengerUId(getCurUid())
                .setEstimateFare(estimateFare)
                .setTripDistance(distance)
                .setTripDuration(duration)
                .build();

        // Set up Trip
        // todo: handle car type, notes
        // create Passenger Request
        PassengerRequest passengerRequest = PassengerRequest.Builder.aPassengerRequest(getCurUid())
                .setPickUpSavePlace(getPickupPlace())
                .setDropOffSavePlace(getDropPlace())
                .setPostTime(TimeUtils.getCurrentTimeAsString())
                .setCarType(CarType.BIKE)
                .setNote("Notes..")
                .build();

        trip.setTripState(TripState.WAITING_ACCEPT);
        trip.setTripStyle(TripStyle.HH);
        trip.setPassengerRequest(passengerRequest);

        dbRefe.child(Define.DB_TRIPS)
                .child(tripUId).setValue(trip);


        // Find Driver todo: add car type
        if (hhMode) {
            findMatchingHH(trip, TimeUtils.getCurrentTimeAsDate(), waitMinute, new FindHHCompleteListener() {
                        @Override
                        public void OnLoopThoughAllRequestHH() {
                            synchronized (isDriverFound)
                            {
                                //todo:v2
                                // if loop through all the objects but still not find matching HH request then use normal request
//                                if( ! isDriverFound)
//                                    findNearestDriver(trip);
                            }
                        }

                        @Override
                        public void OnFoundDriverRequest(RouteRequest request) {
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

                            // show driver info
                            setUpFoundDriver(request.getDriverUId());

                            Log.i(TAG, "Found HH request" + request.getDriverUId());
                        }
                    }
            );
        } else {
            //todo:v2
//            findNearestDriver(trip);
        }
    }


    //region ------------ Find matching Active Driver

    interface FindActiveDriverListener {
        void OnLoopThoughAllRequestHH();
        void OnFoundDriverRequest(RouteRequest request);
    }


    private void findNearestDriver(Trip trip) {

//        startFindActiveDriver();
    }
    //endregion


    //region ------------ Find matching HH request

    // for synchronous purpose use interface and synchronized keyword
    // interface for raise loop thought all list event
    // synchronized keyword for locking the Boolean variable
    interface FindHHCompleteListener {
        void OnLoopThoughAllRequestHH();
        void OnFoundDriverRequest(RouteRequest request);
    }

    private void findMatchingHH(Trip trip, Date passengerStartTime, int waitMinute, FindHHCompleteListener listener) {
        DatabaseReference dbRequest = dbRefe.child(Define.DB_ROUTE_REQUESTS);

        dbRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Note: We need to find the matching and the nearest also
                // Because the latency when get the polyline form google server, so
                // we need to sort all HH driver request in nearest other then we can check from that

                // get List from DataSnapshot after filtered and ordered
                List<RouteRequest> routeRequestsFiltered = filterAndOrderingRequestList(dataSnapshot, passengerStartTime, waitMinute, mLastLocation);

                checkListDriverRequest(routeRequestsFiltered, passengerStartTime, waitMinute, listener);

                // if list null raise event
                if (routeRequestsFiltered.size() == 0) {
                    listener.OnLoopThoughAllRequestHH();
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
                                                            Location curLocation) {
        List<RouteRequest> requestList = new ArrayList();

        // get list from database
        for (DataSnapshot postSnapshot: listDriverRequestDS.getChildren()) {
            for (DataSnapshot requestSnapshot : postSnapshot.getChildren()) {
                RouteRequest request = requestSnapshot.getValue(RouteRequest.class);

                if (request.getRouteRequestState() == RouteRequestState.FINDING_PASSENGER
                        && request.func_isInTheFuture()) {
//                    LatLng latLng_startLocation = request.getStartPlace().func_getLatLngLocation();
//                    // check validate HH request before add to list
//                    if ((LocationUtils.calcDistance(latLng_startLocation, mLastLocation) < limitHHRadius)
//                            && !request.func_isInTheFuture(Define.DRIVER_REQUESTS_TIMEOUT)) {
//                        requestList.add(request);
//                    }

                    // estimate check the request start time to the passenger start time is in the waitMinute
                    // (this also check passengerStartTime > requestStartTime) todo: is it?
                    Date requestStartTime = TimeUtils.strToDate(request.getStartTime());
                    if(TimeUtils.getPassTime(passengerStartTime, requestStartTime) < waitMinute*60)
                        requestList.add(request);

                }
            }
        }

        // sort list
        Collections.sort(requestList, (dq1, dq2) ->{
            double distance1 = LocationUtils.calcDistance(dq1.getStartPlace().func_getLatLngLocation(), curLocation);
            double distance2 = LocationUtils.calcDistance(dq2.getStartPlace().func_getLatLngLocation(), curLocation);

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
            directionManager.findPath(latLng_startLocation, getPickupPlace().func_getLatLngLocation(),
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
                                                                getPickupPlace().func_getLatLngLocation(),
                                                                getDropPlace().func_getLatLngLocation(),
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
                                                    listener.OnLoopThoughAllRequestHH();
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


    //region -------------- Show Driver Info

    /**
     * If Online User is in "D_RECEIVING_BOOKING_HH state" and NOT "time out" then get User info as marker
     */
    //todo: add Driver end location
    private void setUpFoundDriver(String driverUId) {
        DBManager.getUserById(driverUId, (userInfo) ->
            {
                setUpDialogInfo(userInfo);
                dialogInfo.show();
            }
        );
    }

    private void setUpDialogInfo(final UserInfo driverInfo) {
        dialogInfo = new Dialog(PassengerActivity.this);
        dialogInfo.setContentView(R.layout.info_user);

        btnMessage = dialogInfo.findViewById(R.id.btnMessage);
        btnCall = dialogInfo.findViewById(R.id.btnCall);
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

                        PassengerActivity.this.startActivity(intent);
                        dialogInfo.dismiss();

                    } else {
                        ActivityCompat.requestPermissions(PassengerActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1001);
                    }
                }
            }
        });

    }

    //endregion


    //endregion

    //endregion

//    //region ------ Start Booking v1--------
//
//    Boolean isDriverFound;      // --> use for synchronous purpose
//    boolean notFoundHH;      // --> use for synchronous purpose
//    boolean hhMode;
//
//    Float estimateFare;
//
//    private void startBooking() {
//        //todo: put hhMode to screen
//        hhMode = true;
//        notFoundHH = false;
//
//        estimateFare = 10000f;
//        long limitHHRadius = 500;
//        float distance = 1000;
//        float duration = 1000;
//
//
//        // create a trip
//        String tripUId = dbRefe.child(Define.DB_TRIPS).push().getKey();
//
//        Trip trip = Trip.Builder.aTrip(tripUId)
//                .setPassengerUId(getCurUid())
//                .setEstimateFare(estimateFare)
//                .setTripDistance(distance)
//                .setTripDuration(duration)
//                .build();
//
//        // Set up Trip
//        // todo: handle car type, notes
//        // create Passenger Request
//        PassengerRequest passengerRequest = PassengerRequest.Builder.aPassengerRequest(getCurUid())
//                .setPickUpSavePlace(pickupPlace)
//                .setDropOffSavePlace(dropPlace)
//                .setPostTime(TimeUtils.getCurrentTimeAsString())
//                .setCarType(CarType.BIKE)
//                .setNote("Notes..")
//                .build();
//
//        trip.setTripState(TripState.WAITING_ACCEPT);
//        trip.setTripStyle(TripStyle.HH);
//        trip.setPassengerRequest(passengerRequest);
//
//        dbRefe.child(Define.DB_TRIPS)
//                .child(tripUId).setValue(trip);
//
//
//        // Find Driver todo: add car type
//        if (hhMode) {
//            findMatchingHH(trip, limitHHRadius, new FindHHCompleteListener() {
//                        @Override
//                        public void OnLoopThoughAllRequestHH() {
//                            synchronized (isDriverFound)
//                            {
//                                // if loop through all the objects but still not find matching HH request then use normal request
//                                if( ! isDriverFound)
//                                    findNearestDriver(trip);
//                            }
//                        }
//
//                        @Override
//                        public void OnFoundDriverRequest(RouteRequest request) {
//                            isDriverFound = true;
//                            String driverUId = request.getDriverUId();
//
//                            // notify driver thought database
//                            dbRefe.child(Define.DB_ONLINE_USERS)
//                                    .child(driverUId).child(Define.DB_ONLINE_USERS_NOTIFY_TRIP).setValue(tripUId);
//
//
//                            // waitForDriverAccept
//                            dbRefe.child(Define.DB_TRIPS)
//                                    .child(tripUId).child(Define.DB_TRIPS_TRIP_STATE)
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            driverAccepted();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//                            Log.i(TAG, "Found HH request" + request.getDriverUId());
//                        }
//                    }
//            );
//        } else {
//            findNearestDriver(trip);
//        }
//    }
//
//    //region --------- Post request
//
//
//    //endregion
//
//    private void driverAccepted() {
//
//    }
//
//
//
//    //region ------------ Find matching Active Driver
//
//    interface FindActiveDriverListener {
//        void OnLoopThoughAllRequestHH();
//        void OnFoundDriverRequest(RouteRequest request);
//    }
//
//
//    private void findNearestDriver(Trip trip) {
//
////        startFindActiveDriver();
//    }
//    //endregion
//
//    //region ------------ Find matching HH request
//
//    // for synchronous purpose use interface and synchronized keyword
//    // interface for raise loop thought all list event
//    // synchronized keyword for locking the Boolean variable
//    interface FindHHCompleteListener{
//        void OnLoopThoughAllRequestHH();
//        void OnFoundDriverRequest(RouteRequest request);
//    }
//
//    private void findMatchingHH(Trip trip, long limitHHRadius, FindHHCompleteListener listener) {
//        DatabaseReference dbRequest = dbRefe.child(Define.DB_ROUTE_REQUESTS);
//
//        dbRequest.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Note: We need to find the matching and the nearest also
//                // Because the latency when get the polyline form google server, so
//                // we need to sort all HH driver request in nearest other then we can check from that
//
//                // get List from DataSnapshot after filtered and ordered
//                List<RouteRequest> routeRequests = filterAndOrderingRequestList(dataSnapshot, mLastLocation, limitHHRadius);
//
//                // loop the the list and find matching request if not found raise the loop thought listener
//                // if found run foundDriver method
//                for (int i = 0; i < routeRequests.size(); i++) {
//                    // if driver found break the check loop
//                    synchronized (isDriverFound) {
//                        if(isDriverFound)
//                            break;
//                    }
//                    checkListDriverRequest(routeRequests.size(), i, routeRequests.get(i), listener);
//                }
//
//                // if list null raise event
//                if (routeRequests.size() == 0) {
//                    listener.OnLoopThoughAllRequestHH();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //todo: handle error
//                Log.e(TAG, databaseError.getMessage());
//            }
//        });
//    }
//
//    private List<RouteRequest> filterAndOrderingRequestList(DataSnapshot listDriverRequestDS,
//                                                            Location curLocation, long limitHHRadius) {
//        List<RouteRequest> requestList = new ArrayList();
//
//        // get list from database
//        for (DataSnapshot postSnapshot: listDriverRequestDS.getChildren()) {
//            RouteRequest request = postSnapshot.getValue(RouteRequest.class);
//
//            LatLng latLng_startLocation = LocationUtils.strToLatLng(request.getStartLocation());
//
//            // check validate HH request before add to list
//            if ((LocationUtils.calcDistance(latLng_startLocation, mLastLocation) < limitHHRadius)
//                    && ! request.func_isInTheFuture(Define.DRIVER_REQUESTS_TIMEOUT))
//            {
//                requestList.add(request);
//            }
//        }
//
//        // sort list
//        Collections.sort(requestList, (dq1, dq2) ->{
//            double distance1 = LocationUtils.calcDistance(dq1.getStartLocation(), curLocation);
//            double distance2 = LocationUtils.calcDistance(dq2.getStartLocation(), curLocation);
//
//            if(distance1 < distance2)
//                return -1;
//            else if (distance1 == distance2)
//                return 0;
//            else
//                return 1;
//        } );
//
//        return requestList;
//    }
//
//    //todo : check if user swich off
//    //todo : Check driver state
//
//    /**
//     * check if Pickup Place and End Place match to the Request Polyline
//     * isDriverFound --> use for synchronous purpose
//     */
//    private void checkListDriverRequest(final int listSize, final int itemIndex,
//                                           final RouteRequest request, FindHHCompleteListener listener) {
//        //get Location From Request
//        LatLng latLng_startLocation = LocationUtils.strToLatLng(request.getStartLocation());
//        LatLng latLng_endLocation = LocationUtils.strToLatLng(request.getEndLocation());
//
//        Log.d(TAG, "trigger Event");
//
//        // check limit radius --> if out off range will not check
//        // find the Direction depend on Request
//        directionManager.findPath(latLng_startLocation, latLng_endLocation,
//                new DirectionFinderListener() {
//                    @Override
//                    public void onDirectionFinderStart() {
//
//                    }
//
//                    @Override
//                    public void onDirectionFinderSuccess(List<Route> routes) {
//                        // isDriverFound --> for synchronous purpose
//                        Log.d(TAG, "done Event");
//                        if (!isDriverFound && routes.size() > 0) {
//                            {
//                                // get Driver Request Polyline
//                                final List<LatLng> polyline = LocationUtils.getPointsFromRoute(routes.get(0));
//
//                                // check if Pickup Place and End Place match to the Polyline
//                                boolean isMatch = LocationUtils.isMatching(polyline,
//                                        getPickupPlace().func_getLatLngLocation(),
//                                        getDropPlace().func_getLatLngLocation(),
//                                        500);
//
//                                synchronized (isDriverFound) {
//                                    if(isMatch && !isDriverFound)
//                                    {
//                                        listener.OnFoundDriverRequest(request);
//                                    }
//                                }
//                            }
//                        }
//
//                        // raise event loop thought all request if last element
//                        if (listSize == itemIndex + 1) {
//                            listener.OnLoopThoughAllRequestHH();
//                            Log.i(TAG, "Loop thought all HH request");
//                        }
//                    }
//                });
//        Log.d(TAG, "out Event");
//    }

    //endrigon


    //endregion

    //endregion

    //region ------ Auto Complete  --------

    int PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2001;
    int END_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2002;
    Button btn_pickupLocation, btn_dropLocation;

    SavedPlace dropPlace, pickupPlace;

    private SavedPlace getDropPlace() {
        if (dropPlace == null) {
            Log.e(TAG, "startPlace null");
            dropPlace = new SavedPlace();
            return dropPlace;
        }
        return dropPlace;
    }

    private SavedPlace getPickupPlace() {
        if (pickupPlace == null) {
            Log.e(TAG, "endPlace null");
            pickupPlace = new SavedPlace();
            return pickupPlace;
        }
        return pickupPlace;
    }

    private void searViewEvent() {
        btn_pickupLocation.setOnClickListener(v ->
                StartAutoCompleteIntent(PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE));

        btn_dropLocation.setOnClickListener(v ->
                StartAutoCompleteIntent(END_PLACE_AUTOCOMPLETE_REQUEST_CODE));
    }

    private void AutoCompleteIntentResultHandle(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE || requestCode == END_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == SearchActivity.RESULT_OK) {
                String placeId = data.getStringExtra("place_id");
                final String placePrimaryText = data.getStringExtra("place_primary_text");
                final String placeLocation = data.getStringExtra("place_location");
                final String placeAddress = data.getStringExtra("place_address");


                if (requestCode == PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                    pickupPlace = new SavedPlace();
                    pickupPlace.setId(placeId);
                    pickupPlace.setPrimaryText(placePrimaryText);
                    pickupPlace.setAddress(placeAddress);
                    pickupPlace.setLocation(placeLocation);

                    btn_pickupLocation.setText(placePrimaryText);

                    markerManager.draw_PickupPlaceMarker(pickupPlace);
                } else if (requestCode == END_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                    dropPlace = new SavedPlace();
                    dropPlace.setId(placeId);
                    dropPlace.setPrimaryText(placePrimaryText);
                    dropPlace.setAddress(placeAddress);
                    dropPlace.setLocation(placeLocation);

                    btn_dropLocation.setText(placePrimaryText);

                    markerManager.draw_DropPlaceMarker(dropPlace);
                }

                // Move Camera
                if (pickupPlace != null && dropPlace != null) {
                    cameraManager.moveCam(pickupPlace.func_getLatLngLocation(), dropPlace.func_getLatLngLocation());
                }
                else if (pickupPlace != null) {
                    cameraManager.moveCam(pickupPlace.func_getLatLngLocation());
                } else if (dropPlace != null) {
                    cameraManager.moveCam(dropPlace.func_getLatLngLocation());
                }
            }
        }
    }

    private void StartAutoCompleteIntent(int requestCode) {
        Intent intent = new Intent(PassengerActivity. this,SearchActivity.class);
        if (mLastLocation != null) {
            intent.putExtra("cur_lat", mLastLocation.getLatitude());
            intent.putExtra("cur_lng", mLastLocation.getLongitude());
            intent.putExtra("radius", Define.RADIUS_AUTO_COMPLETE);        // radius (meters)
            // note: result with be relative with the bound (more details in Activity Class)
        }
        startActivityForResult(intent, requestCode);
    }

    //endregion

    //region ------ Others  --------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AutoCompleteIntentResultHandle(requestCode, resultCode, data);

    }

    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    //endregion

    //------------------------------------


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup map
        setupCheckRealtime = false;
        simpleMapListener = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PassengerActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // set Callback listener
        isFirstGetLocation = false;

        isDriverFound = false;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //
        Init();
        addEven();
    }


    private void Init() {

        // Init firebase
        dbRefe = FirebaseDatabase.getInstance().getReference();

        // Init View
        locationRider_switch = findViewById(R.id.locationRider_switch);

        btnPost = findViewById(R.id.btnPost);
        btnFindDriver = findViewById(R.id.btn_find_driver);


        // search view
        btn_pickupLocation = findViewById(R.id.btn_pick_up_location);
        btn_dropLocation = findViewById(R.id.btn_end_location);

    }

    private void addEven() {

        //// Rider
        locationRider_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean b) {
                if (b) {

                } else {

                }
            }
        });


        // post Pick up point and destination -- not done
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //todo: set Event + add callback value + filter Vietnam
//                destination = destination.replace(" ", "+"); //Replace space with + for fetch data
//                Toast.makeText(getApplicationContext(), "SS", Toast.LENGTH_SHORT).show();
//                //Log.d("EDMTDEV", destination);
//                getDirection();


            }
        });

        btnFindDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBooking();
            }
        });

        searViewEvent();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the PassengerActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user_info) {
            // Handle the camera action
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PassengerActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
