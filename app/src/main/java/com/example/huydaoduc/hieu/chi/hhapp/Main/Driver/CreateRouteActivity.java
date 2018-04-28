package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SearchActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.DriverRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.OnlineUser;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserState;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateRouteActivity extends SimpleMapActivity implements SimpleMapActivity.SimpleMapListener {

    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        // setup map
        setupCheckRealtime = false;
        simpleMapListener = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CreateRouteActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        isFirstGetLocation = false;

        geocoder = new Geocoder(this, Locale.getDefault());

        Init();
        Event();
    }

    private void Init() {
        btn_start_picker = findViewById(R.id.btn_start_place_picker);
        btn_end_picker = findViewById(R.id.btn_end_place_picker);

        btn_date_picker = findViewById(R.id.btn_date_picker);
        btn_date_picker.setText(TimeUtils.curDateToUserStr());

        btn_time_picker = findViewById(R.id.btn_time_picker);
        btn_time_picker.setText(TimeUtils.curTimeToUserString());

        initDateTimePicker();
    }

    private void Event() {
        searViewEvent();

        btn_date_picker.setOnClickListener(view -> {
            datePickerDialog.show(getFragmentManager(),"datePickerDialog");
        });

        btn_time_picker.setOnClickListener(v ->{
            timePickerDialog.show(getFragmentManager(),"timePickerDialog");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AutoCompleteIntentResultHandle(requestCode,resultCode,data);
    }


    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void OnRealTimeLocationUpdate() {

    }

    @Override
    public void OnMapSetupDone() {
        String startAddress = LocationUtils.getLocationAddress(geocoder,mLastLocation);

        getStartPlace().setAddress(startAddress);
        getStartPlace().setLocation(LocationUtils.locaToStr(mLastLocation));
        getStartPlace().setPrimaryText(startAddress);

        btn_start_picker.setText(getStartPlace().getPrimaryText());

        markerManager.draw_PickupPlaceMarker(getStartPlace());

    }


    // Activity Property
    DatabaseReference dbRefe;

    // User Property
    UserState userState;

    //region ------ HH Request   --------
//    private void putRouteToDB() {
//        // find/ show/ put_online  route + start real time checking
//        directionManager.findPath(LocationUtils.locaToLatLng(mLastLocation), getEndPlace().func_getLatLngLocation(),
//                new DirectionFinderListener() {
//                    @Override
//                    public void onDirectionFinderStart() {
//
//                    }
//
//                    @Override
//                    public void onDirectionFinderSuccess(List<Route> routes) {
//                        // run this to put value the first time
//
//
//                        //todo: get the selected route
//                        // put Route online
//                        putRouteRequest(routes.get(0));
//
//                        // move camera
//                        cameraManager.moveCamWithRoutes(routes);
//
//                        // draw markers
//                        markerManager.draw_DropPlaceMarker(routes.get(0).getLegs().get(0).getEndLocation());
//
//                        updateOnlineUserInfo();
//                        // This will Enable real time checking
//                        if (userState != UserState.D_RECEIVING_BOOKING_HH) {
//                            userState = UserState.D_RECEIVING_BOOKING_HH;
//                        }
//
//                        //Listen to Trip UId
//                        dbRefe.child(Define.DB_ONLINE_USERS)
//                                .child(getCurUid())
//                                .child(Define.DB_ONLINE_USERS_TRIP_UID)
//                                .addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        String tripUId = dataSnapshot.getValue(String.class);
//
//                                        if(! TextUtils.isEmpty(tripUId))
//                                        {
//                                            showPassengerRequestAndChangeState(tripUId);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                    }
//                });
//
//    }
//
//    /**
//     * Put Route Request Online
//     */
//    private void putRouteRequest(Route route) {
//        String uid = getCurUid();
//        //todo: handle price
//        Float pricePerKm = 1f;
//
//        // get Driver Request from route
//        DriverRequest driverRequest = DriverRequest.func_createDriverRequestFromRoute(route, uid, pricePerKm);
//
//        dbRefe.child(Define.DB_DRIVER_REQUESTS).child(uid).setValue(driverRequest);
//    }
//
//    /**
//     * This will trigger when current location update = POLLING_FREQ_MILLI_SECONDS
//     * For "DriverRequest" we will:
//     * + Update real time location after current location is update
//     * + Update real time Route request after a period of ONLINE_USER_TIMEOUT or radius out of ONLINE_USER_RADIUS_UPDATE
//     */
//    private void realTimeChecking_DriverRequest() {
//        // todo: handle if getAccuracy > 100 --> will not update data
//
//        // Get old value and Check if location out of radius or Out of time Then update Route Request
//        DBManager.getOnlineUserById(getCurUid(), onlineUser -> {
//            // Check with distance
//            if (Define.ONLINE_USER_RADIUS_UPDATE < LocationUtils.calcDistance(LocationUtils.locaToLatLng(mLastLocation), LocationUtils.strToLatLng(onlineUser.getLocation()))) {
//                updateAndDrawRouteRequest();
//            }
//            // Check with time out
//            else if (onlineUser.func_isTimeOut(Define.ONLINE_USER_TIMEOUT)) {
//                updateAndDrawRouteRequest();
//            }
//        });
//
//        // Update new Online User value
//        updateOnlineUserInfo();
//    }
//
//    private void updateAndDrawRouteRequest() {
//        // find routes
//        directionManager.findPath(mLastLocation, btn_endLocation.getText().toString(),
//                new DirectionFinderListener() {
//                    @Override
//                    public void onDirectionFinderStart() {
//
//                    }
//
//                    @Override
//                    public void onDirectionFinderSuccess(List<Route> routes) {
//                        // Redraw route
//                        directionManager.drawRoutes(routes, true);
//
//                        //todo: get the selected route
//                        // put Route online
//
//                    }
//                });
//    }
//
//    private void endRealTimeChecking() {
//        if (userState == UserState.D_RECEIVING_BOOKING_HH) {
//            directionManager.removeAllRoutes();
//            markerManager.dropPlaceMarker.remove();
//
//            dbRefe.child(Define.DB_DRIVER_REQUESTS).child(getCurUid()).removeValue();
//
//            // Update User state
//            userState = UserState.OFFLINE;
//            updateOnlineUserInfo();
//        }
//    }
//
//    private void updateOnlineUserInfo() {
//        dbRefe.child(Define.DB_ONLINE_USERS).child(getCurUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                OnlineUser onlineUser = dataSnapshot.getValue(OnlineUser.class);
//
//                if (onlineUser != null) {
//                    onlineUser.setLocation(LocationUtils.locaToStr(mLastLocation));
//                    onlineUser.setState(userState);
//                    onlineUser.setLastTimeCheck(TimeUtils.getCurrentTimeAsString());
//                    dbRefe.child(Define.DB_ONLINE_USERS).child(getCurUid()).setValue(onlineUser);
//                }
//                else{
//                    // first time put
//                    OnlineUser newOnlineUser = new OnlineUser();
//                    newOnlineUser.setLocation(LocationUtils.locaToStr(mLastLocation));
//                    newOnlineUser.setState(userState);
//                    newOnlineUser.setLastTimeCheck(TimeUtils.getCurrentTimeAsString());
//                    dbRefe.child(Define.DB_ONLINE_USERS).child(getCurUid()).setValue(newOnlineUser);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void showPassengerRequestAndChangeState(String tripUId) {
//        // Change Driver State
//        userState = UserState.D_WAITING_FOR_ACCEPT;
//        dbRefe.child(Define.DB_ONLINE_USERS)
//                .child(getCurUid())
//                .child(Define.DB_ONLINE_USERS_STATE).setValue(userState);
//
//        // show passenger request
//        dbRefe.child(Define.DB_TRIPS)
//                .child(tripUId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Trip trip = dataSnapshot.getValue(Trip.class);
//
//                        showAcceptingFragment(trip);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//    }
//
//    private void showAcceptingFragment(Trip trip) {
//        PassengerRequest request = trip.getPassengerRequest();
//
//        //todo: distance between pickup and cur OR between pickup and drop off
//        float distance = LocationUtils.calcDistance(request.getPickUpSavePlace().getLocation(),request.getDropOffSavePlace().getLocation());
//        float fare;
//
//        if (userState == UserState.D_RECEIVING_BOOKING_HH) {
//            fare = trip.getTripDistance() * Define.FARE_VND_PER_M * 0.25f;
//        }
//        else
//            fare = trip.getTripDistance() * Define.FARE_VND_PER_M;
//
//        // create dialog
//        AcceptingTripFragment acceptingTripFragment = AcceptingTripFragment
//                .newInstance(distance, request.getPickUpSavePlace().getAddress(),
//                        request.getDropOffSavePlace().getAddress(),
//                        request.getNote(),
//                        fare);
//
//        // set event
//        acceptingTripFragment.show(getSupportFragmentManager(), "dialog");
//
//
//    }
//
//    private void driverAccepted(String tripUId) {
//        // update driver uid to trip info
//        dbRefe.child(Define.DB_TRIPS)
//                .child(tripUId)
//                .child(Define.DB_TRIPS_DRIVER_UID).setValue(getCurUid());
//    }
//


    //endregion

    // new

    Route autoCompleteRoute;
    private void getAutoCompleteRoute() {
        directionManager.findPath(getStartPlace().func_getLatLngLocation(), getEndPlace().func_getLatLngLocation(),
                new DirectionFinderListener() {
                    @Override
                    public void onDirectionFinderStart() {

                    }

                    @Override
                    public void onDirectionFinderSuccess(List<Route> routes) {
                        // Raw route
                        directionManager.drawRoutes(routes, true);

                        // move camera
                        cameraManager.moveCamWithRoutes(routes);

                        autoCompleteRoute = routes.get(0);
                    }
                });
    }

    //region ------ Date Time Picker  --------
    Calendar selectedDateTime;
    Button btn_date_picker, btn_time_picker;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    private void initDateTimePicker() {
        selectedDateTime = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
                    btn_date_picker.setText(TimeUtils.dateToUserStr(dayOfMonth,monthOfYear,year));

                    selectedDateTime.set(year,monthOfYear,dayOfMonth);
                };

        datePickerDialog = DatePickerDialog.newInstance(dateSetListener, Calendar.getInstance());
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setAccentColor(ResourcesCompat.getColor(getResources(), R.color.date_picker_bar, null));

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute, second) -> {
            btn_time_picker.setText(TimeUtils.timeToUserString(hourOfDay,minute));

            selectedDateTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
            selectedDateTime.set(Calendar.MINUTE,minute);
            selectedDateTime.set(Calendar.SECOND,0);
            selectedDateTime.set(Calendar.MILLISECOND,0);
        };

        timePickerDialog = TimePickerDialog.newInstance(timeSetListener, true);
    }

    //endregion

    //region ------ Auto Complete  --------

    int PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2001;
    int END_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2002;
    Button btn_start_picker, btn_end_picker;

    SavedPlace endPlace, startPlace;

    //todo handle null
    private SavedPlace getStartPlace() {
        if (startPlace == null) {
            Log.e(TAG, "startPlace null");
            startPlace = new SavedPlace();
            return startPlace;
        }
        return startPlace;
    }

    private SavedPlace getEndPlace() {
        if (endPlace == null) {
            Log.e(TAG, "endPlace null");
            endPlace = new SavedPlace();
            return endPlace;
        }
        return endPlace;
    }

    private void searViewEvent() {
        btn_start_picker.setOnClickListener(v ->
                StartAutoCompleteIntent(PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE));

        btn_end_picker.setOnClickListener(v ->
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
                    getStartPlace().setId(placeId);
                    getStartPlace().setPrimaryText(placePrimaryText);
                    getStartPlace().setAddress(placeAddress);
                    getStartPlace().setLocation(placeLocation);

                    btn_start_picker.setText(placePrimaryText);

                    markerManager.draw_PickupPlaceMarker(startPlace);
                } else if (requestCode == END_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                    getEndPlace().setId(placeId);
                    getEndPlace().setPrimaryText(placePrimaryText);
                    getEndPlace().setAddress(placeAddress);
                    getEndPlace().setLocation(placeLocation);

                    btn_end_picker.setText(placePrimaryText);

                    markerManager.draw_DropPlaceMarker(getEndPlace());
                }

                // Draw Route
                if (startPlace != null && endPlace != null) {
                    getAutoCompleteRoute();
                }
                // Move Camera
                else if (startPlace != null) {
                    cameraManager.moveCam(getStartPlace().func_getLatLngLocation());
                } else if (endPlace != null) {
                    cameraManager.moveCam(getEndPlace().func_getLatLngLocation());
                }
            }
        }
    }

    private void StartAutoCompleteIntent(int requestCode) {
        Intent intent = new Intent(CreateRouteActivity.this,SearchActivity.class);
        if (mLastLocation != null) {
            intent.putExtra("cur_lat", mLastLocation.getLatitude());
            intent.putExtra("cur_lng", mLastLocation.getLongitude());
            intent.putExtra("radius", Define.RADIUS_AUTO_COMPLETE);        // radius (meters)
            // note: result with be relative with the bound (more details in Activity Class)
        }
        startActivityForResult(intent, requestCode);
    }

    //endregion

}
