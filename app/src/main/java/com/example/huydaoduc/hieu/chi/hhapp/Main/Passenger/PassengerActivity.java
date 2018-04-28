package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Common.Common;
import com.example.huydaoduc.hieu.chi.hhapp.CostomInfoWindow.CustomInfoWindow;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.DirectionManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.MapCameraManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.MarkerManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SearchActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.DriverRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripStyle;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.CarType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.OnlineUser;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserState;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.Remote.IGoogleAPI;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        long limitHHRadius = 500;
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
                .setPickUpSavePlace(pickupPlace)
                .setDropOffSavePlace(dropPlace)
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
            findMatchingHH(trip, limitHHRadius, new FindHHListener() {
                        @Override
                        public void OnLoopThoughAllRequestHH() {
                            synchronized (isDriverFound)
                            {
                                // if loop through all the objects but still not find matching HH request then use normal request
                                if( ! isDriverFound)
                                    findNearestDriver(trip);
                            }
                        }

                        @Override
                        public void OnFoundDriverRequest(DriverRequest request) {
                            isDriverFound = true;
                            String driverUId = request.getDriverUId();

                            // notify driver thought database
                            dbRefe.child(Define.DB_ONLINE_USERS)
                                    .child(driverUId).child(Define.DB_ONLINE_USERS_TRIP_UID).setValue(tripUId);


                            // waitForDriverAccept
                            dbRefe.child(Define.DB_TRIPS)
                                    .child(tripUId).child(Define.DB_TRIPS_TRIP_STATE)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            driverAccepted();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                            Log.i(TAG, "Found HH request" + request.getDriverUId());
                        }
                    }
            );
        } else {
            findNearestDriver(trip);
        }
    }

    //region --------- Post request


    //endregion

    private void driverAccepted() {

    }



    //region ------------ Find matching Active Driver

    interface FindActiveDriverListener {
        void OnLoopThoughAllRequestHH();
        void OnFoundDriverRequest(DriverRequest request);
    }


    private void findNearestDriver(Trip trip) {

//        startFindActiveDriver();
    }
    //endregion

    //region ------------ Find matching HH request

    // for synchronous purpose use interface and synchronized keyword
    // interface for raise loop thought all list event
    // synchronized keyword for locking the Boolean variable
    interface FindHHListener{
        void OnLoopThoughAllRequestHH();
        void OnFoundDriverRequest(DriverRequest request);
    }

    private void findMatchingHH(Trip trip, long limitHHRadius, FindHHListener listener) {
        DatabaseReference dbRequest = dbRefe.child(Define.DB_DRIVER_REQUESTS);

        dbRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Note: We need to find the matching and the nearest also
                // Because the latency when get the polyline form google server, so
                // we need to sort all HH driver request in nearest other then we can check from that

                // get List from DataSnapshot after filtered and ordered
                List<DriverRequest> driverRequests = filterAndOrderingRequestList(dataSnapshot, mLastLocation, limitHHRadius);

                // loop the the list and find matching request if not found raise the loop thought listener
                // if found run foundDriver method
                for (int i = 0; i < driverRequests.size(); i++) {
                    // if driver found break the check loop
                    synchronized (isDriverFound) {
                        if(isDriverFound)
                            break;
                    }
                    checkIfDriverRequestMatch(driverRequests.size(), i, driverRequests.get(i), listener);
                }

                // if list null raise event
                if (driverRequests.size() == 0) {
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

    private List<DriverRequest> filterAndOrderingRequestList(DataSnapshot listDriverRequestDS,
                                                             Location curLocation, long limitHHRadius) {
        List<DriverRequest> requestList = new ArrayList();

        // get list from database
        for (DataSnapshot postSnapshot: listDriverRequestDS.getChildren()) {
            DriverRequest request = postSnapshot.getValue(DriverRequest.class);

            LatLng latLng_startLocation = LocationUtils.strToLatLng(request.getStartLocation());

            // check validate HH request before add to list
            if ((LocationUtils.calcDistance(latLng_startLocation, mLastLocation) < limitHHRadius)
                    && ! request.func_isTimeOut(Define.DRIVER_REQUESTS_TIMEOUT))
            {
                requestList.add(request);
            }
        }

        // sort list
        Collections.sort(requestList, (dq1, dq2) ->{
            double distance1 = LocationUtils.calcDistance(dq1.getStartLocation(), curLocation);
            double distance2 = LocationUtils.calcDistance(dq2.getStartLocation(), curLocation);

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
    private void checkIfDriverRequestMatch(final int listSize, final int itemIndex,
                                           final DriverRequest request, FindHHListener listener) {
        //get Location From Request
        LatLng latLng_startLocation = LocationUtils.strToLatLng(request.getStartLocation());
        LatLng latLng_endLocation = LocationUtils.strToLatLng(request.getEndLocation());

        Log.d(TAG, "trigger Event");

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
                        Log.d(TAG, "done Event");
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
                                    if(isMatch && !isDriverFound)
                                    {
                                        listener.OnFoundDriverRequest(request);
                                    }
                                }
                            }
                        }

                        // raise event loop thought all request if last element
                        if (listSize == itemIndex + 1) {
                            listener.OnLoopThoughAllRequestHH();
                            Log.i(TAG, "Loop thought all HH request");
                        }
                    }
                });
        Log.d(TAG, "out Event");
    }

    //endrigon


    //region -------------- Show Driver Info

    /**
     * If Online User is in "D_RECEIVING_BOOKING_HH state" and NOT "time out" then get User info as marker
     */
    //todo: add Driver end location
    private void setUpFoundDriver(String driverUId) {
        DBManager.getOnlineUserById(driverUId, (onlineUser ->
        {
            //todo: check lai dieu kien cho dung
            // check if user is D_RECEIVING_BOOKING_HH && Time out
            if (onlineUser.getState() == UserState.D_RECEIVING_BOOKING_HH && !onlineUser.func_isTimeOut(Define.ONLINE_USER_TIMEOUT))
            {
                // get Driver Info
                DBManager.getUserById(onlineUser.getUid(), (userInfo) ->
                        {
                            markerManager.draw_DriverMarker(userInfo, onlineUser);
                            setUpDialogInfo(userInfo);
                        }
                );
            }
        }));
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



    private void realTimeChecking_PassengerRequest() {
        // todo: handle if getAccuracy > 100 --> will not update data

        // Get old value and Check if location out of radius or Out of time Then update Route Request
        DBManager.getOnlineUserById(getCurUid(), onlineUser -> {
            // Check with distance
            if (Define.ONLINE_USER_RADIUS_UPDATE < LocationUtils.calcDistance(LocationUtils.locaToLatLng(mLastLocation), LocationUtils.strToLatLng(onlineUser.getLocation()))) {
                updateUserRequest();
            }
            // Check with time out
            else if (onlineUser.func_isTimeOut(Define.ONLINE_USER_TIMEOUT)) {
                updateUserRequest();
            }
        });

        // Update new Online User value
        OnlineUser onlineUser = new OnlineUser(getCurUid(), mLastLocation, UserState.D_RECEIVING_BOOKING_HH);
        dbRefe.child(Define.DB_ONLINE_USERS).child(getCurUid()).setValue(onlineUser);

    }

    private void updateUserRequest() {
//        // find routes
//        directionManager.findPath(mLastLocation, btn_dropLocation.getText().toString(),
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
//                        putRouteRequest(routes.get(0));
//                    }
//                });
    }

    private void endRealTimeChecking() {
        if (userState == UserState.D_RECEIVING_BOOKING_HH) {
            directionManager.removeAllRoutes();
            markerManager.dropPlaceMarker.remove();
            userState = UserState.OFFLINE;

            dbRefe.child(Define.DB_DRIVER_REQUESTS).child(getCurUid()).removeValue();
            // change online user state
        }
    }


    //endregion

    //endregion

    //region ------ Auto Complete  --------

    int PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2001;
    int END_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2002;
    Button btn_pickupLocation, btn_dropLocation;

    SavedPlace dropPlace, pickupPlace;

    //todo handle null
    private SavedPlace getPickupPlace() {
        if (pickupPlace == null) {
            Log.e(TAG, "pickupPlace null");
            return new SavedPlace();
        }
        return pickupPlace;
    }

    private SavedPlace getDropPlace() {
        if (dropPlace == null) {
            Log.e(TAG, "dropPlace null");
            return new SavedPlace();
        }
        return dropPlace;
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
