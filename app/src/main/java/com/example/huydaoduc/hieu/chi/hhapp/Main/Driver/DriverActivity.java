package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Common.Common;
import com.example.huydaoduc.hieu.chi.hhapp.CostomInfoWindow.CustomInfoWindow;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.CheckActivityCloseService;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.MapCameraManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.DirectionManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.MarkerManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SearchActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.DriverRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

//todo: check 1 tai khoan dang nhap 2 may
public class DriverActivity extends SimpleMapActivity
        implements
        SimpleMapActivity.SimpleMapListener,
        NavigationView.OnNavigationItemSelectedListener,
        AcceptingTripFragment.OnAcceptingFragmentListener {

    private static final String TAG = "DriverActivity";

    DatabaseReference drivers;
    GeoFire geoFire;

    MaterialAnimatedSwitch locationDriver_switch;

    private Button btnGo;

    //------------------------------------ Chi :

    // Activity Property
    DatabaseReference dbRefe;


    // User Property
    UserState userState;

    @Override
    public void OnRealTimeLocationUpdate() {
        if(userState == UserState.D_RECEIVING_BOOKING_HH)
            realTimeChecking_DriverRequest();
    }

    //region ------ HH Request   --------
    private void startRealTimeCheckingAndShowRoute() {
        // find/ show/ put_online  route + start real time checking
        directionManager.findPath(LocationUtils.locaToLatLng(mLastLocation), getEndPlace().func_getLatLngLocation(),
                new DirectionFinderListener() {
                    @Override
                    public void onDirectionFinderStart() {

                    }

                    @Override
                    public void onDirectionFinderSuccess(List<Route> routes) {
                        // Redraw route
                        directionManager.drawRoutes(routes, true);

                        // move camera
                        cameraManager.moveCamWithRoutes(routes);

                        // draw markers
                        markerManager.draw_DropPlaceMarker(routes.get(0).getLegs().get(0).getEndLocation());

                        //todo: get the selected route
                        // put Route online
                        putRouteRequest(routes.get(0));

                        // run this to put value the first time
                        updateRouteRequest();
                        updateOnlineUserInfo();
                        // This will Enable real time checking
                        if (userState != UserState.D_RECEIVING_BOOKING_HH) {
                            userState = UserState.D_RECEIVING_BOOKING_HH;
                        }

                        //Listen to Trip UId
                        dbRefe.child(Define.DB_ONLINE_USERS)
                                .child(getCurUid())
                                .child(Define.DB_ONLINE_USERS_TRIP_UID)
                                .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String tripUId = dataSnapshot.getValue(String.class);

                                            if(! TextUtils.isEmpty(tripUId))
                                            {
                                                showPassengerRequestAndChangeState(tripUId);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                    }
                });

        //todo: not work when phone turn off
        // delete data when App Kill
        Intent serviceIntent = new Intent(DriverActivity.this, CheckActivityCloseService.class);
        serviceIntent.putExtra("uid", getCurUid());
        DriverActivity.this.startService(serviceIntent);
    }

    /**
     * Put Route Request Online
     */
    private void putRouteRequest(Route route) {
        String uid = getCurUid();
        //todo: handle price
        Float pricePerKm = 1f;

        // get Driver Request from route
        DriverRequest driverRequest = DriverRequest.func_createDriverRequestFromRoute(route, uid, pricePerKm);

        dbRefe.child(Define.DB_DRIVER_REQUESTS).child(uid).setValue(driverRequest);
    }

    /**
     * This will trigger when current location update = POLLING_FREQ_MILLI_SECONDS
     * For "DriverRequest" we will:
     * + Update real time location after current location is update
     * + Update real time Route request after a period of ONLINE_USER_TIMEOUT or radius out of ONLINE_USER_RADIUS_UPDATE
     */
    private void realTimeChecking_DriverRequest() {
        // todo: handle if getAccuracy > 100 --> will not update data

        // Get old value and Check if location out of radius or Out of time Then update Route Request
        DBManager.getOnlineUserById(getCurUid(), onlineUser -> {
            // Check with distance
            if (Define.ONLINE_USER_RADIUS_UPDATE < LocationUtils.calcDistance(LocationUtils.locaToLatLng(mLastLocation), LocationUtils.strToLatLng(onlineUser.getLocation()))) {
                updateRouteRequest();
            }
            // Check with time out
            else if (onlineUser.func_isTimeOut(Define.ONLINE_USER_TIMEOUT)) {
                updateRouteRequest();
            }
        });

        // Update new Online User value
        updateOnlineUserInfo();
    }

    private void updateRouteRequest() {
        // find routes
        directionManager.findPath(mLastLocation, btn_endLocation.getText().toString(),
                new DirectionFinderListener() {
                    @Override
                    public void onDirectionFinderStart() {

                    }

                    @Override
                    public void onDirectionFinderSuccess(List<Route> routes) {
                        // Redraw route
                        directionManager.drawRoutes(routes, true);

                        //todo: get the selected route
                        // put Route online
                        putRouteRequest(routes.get(0));
                    }
                });
    }

    private void endRealTimeChecking() {
        if (userState == UserState.D_RECEIVING_BOOKING_HH) {
            directionManager.removeAllRoutes();
            markerManager.dropPlaceMarker.remove();

            dbRefe.child(Define.DB_DRIVER_REQUESTS).child(getCurUid()).removeValue();

            // Update User state
            userState = UserState.OFFLINE;
            updateOnlineUserInfo();
        }
    }

    private void updateOnlineUserInfo() {
        dbRefe.child(Define.DB_ONLINE_USERS).child(getCurUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OnlineUser onlineUser = dataSnapshot.getValue(OnlineUser.class);

                if (onlineUser != null) {
                    onlineUser.setLocation(LocationUtils.locaToStr(mLastLocation));
                    onlineUser.setState(userState);
                    onlineUser.setLastTimeCheck(TimeUtils.getCurrentTimeAsString());
                    dbRefe.child(Define.DB_ONLINE_USERS).child(getCurUid()).setValue(onlineUser);
                }
                else{
                    // first time put
                    OnlineUser newOnlineUser = new OnlineUser();
                    newOnlineUser.setLocation(LocationUtils.locaToStr(mLastLocation));
                    newOnlineUser.setState(userState);
                    newOnlineUser.setLastTimeCheck(TimeUtils.getCurrentTimeAsString());
                    dbRefe.child(Define.DB_ONLINE_USERS).child(getCurUid()).setValue(newOnlineUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showPassengerRequestAndChangeState(String tripUId) {
        // Change Driver State
        userState = UserState.D_WAITING_FOR_ACCEPT;
        dbRefe.child(Define.DB_ONLINE_USERS)
                .child(getCurUid())
                .child(Define.DB_ONLINE_USERS_STATE).setValue(userState);

        // show passenger request
        dbRefe.child(Define.DB_TRIPS)
                .child(tripUId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);

                showAcceptingFragment(trip);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showAcceptingFragment(Trip trip) {
        PassengerRequest request = trip.getPassengerRequest();

        //todo: distance between pickup and cur OR between pickup and drop off
        float distance = LocationUtils.calcDistance(request.getPickUpSavePlace().getLocation(),request.getDropOffSavePlace().getLocation());
        float fare;

        if (userState == UserState.D_RECEIVING_BOOKING_HH) {
            fare = trip.getTripDistance() * Define.FARE_VND_PER_M * 0.25f;
        }
        else
            fare = trip.getTripDistance() * Define.FARE_VND_PER_M;

        // create dialog
        AcceptingTripFragment acceptingTripFragment = AcceptingTripFragment
                .newInstance(distance, request.getPickUpSavePlace().getAddress(),
                        request.getDropOffSavePlace().getAddress(),
                        request.getNote(),
                        fare);

        // set event
        acceptingTripFragment.show(getSupportFragmentManager(), "dialog");


    }


    @Override
    public void OnTripAcceptTimeOut() {

    }

    @Override
    public void OnTripAccepted() {

    }

    private void driverAccepted(String tripUId) {
        // update driver uid to trip info
        dbRefe.child(Define.DB_TRIPS)
                .child(tripUId)
                .child(Define.DB_TRIPS_DRIVER_UID).setValue(getCurUid());
    }


    //endregion

    //region ------ Auto Complete  --------
    int END_PLACE_AUTOCOMPLETE_REQUEST_CODE = 1002;
    Button btn_endLocation;
    SavedPlace endPlace;


    private SavedPlace getEndPlace() {
        if (endPlace == null) {
            Log.e(TAG, "dropPlace null");
            return new SavedPlace();
        }
        return endPlace;
    }

    private void searViewEvent() {
        btn_endLocation.setOnClickListener(v ->
                StartAutoCompleteIntent(END_PLACE_AUTOCOMPLETE_REQUEST_CODE));

    }

    private void AutoCompleteIntentResultHandle(int requestCode, int resultCode, Intent data) {
        if (requestCode == END_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == SearchActivity.RESULT_OK) {
                //todo: handle null
                String placeId = data.getStringExtra("place_id");
                String placePrimaryText = data.getStringExtra("place_primary_text");
                String placeLocation = data.getStringExtra("place_location");
                String placeAddress = data.getStringExtra("place_address");

                endPlace = new SavedPlace();
                endPlace.setId(placeId);
                endPlace.setPrimaryText(placePrimaryText);
                endPlace.setAddress(placeAddress);
                endPlace.setLocation(placeLocation);

                btn_endLocation.setText(placePrimaryText);

                markerManager.draw_DropPlaceMarker(endPlace);

                cameraManager.moveCam(LocationUtils.locaToLatLng(mLastLocation), endPlace.func_getLatLngLocation());
            }
        }
    }

    private void StartAutoCompleteIntent(int requestCode) {
        Intent intent = new Intent(DriverActivity.this, SearchActivity.class);
        if (mLastLocation != null) {
            intent.putExtra("cur_lat", mLastLocation.getLatitude());
            intent.putExtra("cur_lng", mLastLocation.getLongitude());
            intent.putExtra("radius", 1000);        // radius (meters)
        }
        // note: result with be relative with the bound (more details in Activity Class)
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

        setContentView(R.layout.activity_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(DriverActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // onMapReadyCallback

        //
//        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
//        currenUserRef = FirebaseDatabase.getInstance().getReference(Define.DB_DRIVERS)
//                .child(FirebaseAuth.getInstance().getCurrentUser().getPassengerUId());
//        onlineRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                currenUserRef.onDisconnect().removeValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        // nut ping vi tri

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //
        Init();
        addEven();
    }

    private void Init() {
        // Init View
        locationDriver_switch = findViewById(R.id.locationDriver_switch);

        btnGo = findViewById(R.id.btnGo);

        // CHi
        btn_endLocation = findViewById(R.id.btn_end_location);

        // Firebase Init
        dbRefe = FirebaseDatabase.getInstance().getReference();

    }

    private void addEven() {

        //// Driver
//        locationDriver_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(boolean b) {
//                if (b) {
//                    // check online -- not done
//                    FirebaseDatabase.getInstance().goOnline();
//
//
//                    startCurrentLocationUpdates();
//                    displayLocationAndUpdateData();
//                    Snackbar.make(mapFragment.getView(), "You are Online", Snackbar.LENGTH_SHORT).show();
//                } else {
//                    FirebaseDatabase.getInstance().goOffline();
//
//                    stopLocationUpdate();
//                    if (userMaker != null)
//                        userMaker.remove();
//                    mMap.clear();
////                    handler.removeCallbacks(drawPathRunnable);
//                    Snackbar.make(mapFragment.getView(), "You are Offline", Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        });

        // Chi
        searViewEvent();

        btnGo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), HitchSimple.class);
            DriverActivity.this.startActivity(intent);
        });

        locationDriver_switch.setOnCheckedChangeListener(b -> {
            if (b) {
                startRealTimeCheckingAndShowRoute();
            } else {
                //endRealTimeChecking();
            }
        });
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
            Intent intent = new Intent(DriverActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
