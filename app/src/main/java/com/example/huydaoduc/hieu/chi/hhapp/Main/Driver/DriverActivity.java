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
public class DriverActivity extends AppCompatActivity
        implements
        // My Location Button
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        AcceptingTripFragment.OnAcceptingFragmentListener
{

    private static final String TAG = "DriverActivity";
    // store all info in the map

    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;



    private static int UPDATE_INTERVAL = 3000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire geoFire;

    MaterialAnimatedSwitch locationDriver_switch;

    DatabaseReference onlineRef, currenUserRef;

    //Car animation
    private List<LatLng> polyLineList;
    private Marker carMaker, userMaker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPostion, endPosition, currentPosition;
    private int index, next;
    private Button btnGo;
    private TextView tvName, tvPhone;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;

    private IGoogleAPI mapService;

    TabHost host;

    boolean isDriverFound = false;
    String driverId = "";
    int radius = 1; //1km
    int distance = 1; //3km

    //------------------------------------ Chi :

    // Activity Property
    DatabaseReference dbRefe;



    // User Property
    UserState userState;


    public void realTimeChecking() {
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

    //region ------ Camera Manager --------

    MapCameraManager cameraManager;

    private void initCameraManager() {
        cameraManager = new MapCameraManager(mMap);
    }

    //endregion

    //region ------ Direction Manager --------

    DirectionManager directionManager;

    /**
     * Call only when map is ready
     */
    private void initDirectionManager() {
        directionManager = new DirectionManager(getApplicationContext(), mMap);
    }

    //endregion

    //region ------ Makers --------

    MarkerManager markerManager;

    private void initMarkerManager() {
        GoogleMap.OnMarkerClickListener listener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                return false;
            }
        };

        markerManager = new MarkerManager(mMap, listener);
    }

    //endregion

    //region ------ My Location Button --------

    @SuppressLint("MissingPermission")
    private void initMyLocationButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // permission
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        // Change location to bottom-right ( default: top-right)
        View locationButton = ((View) mapFragment.getView()
                .findViewById(Integer.parseInt("1")).getParent())
                .findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
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


    //region ------ Setup Activity (Fixed)  --------

    // Setup -----------------
    //todo: handle GPS off --> equals no connection
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;

    SupportMapFragment mapFragment;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));

        // Move map to viet nam
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(14.058324,108.277199), 5.6f);
        mMap.moveCamera(update);

        setUpLocation();

        // My Location Button
        initMyLocationButton();
        // Direction Manager
        initDirectionManager();
        // Marker manager
        initMarkerManager();
        // Camera manager
        initCameraManager();

    }

    @SuppressLint("MissingPermission")
    private void setUpLocation() {
        firstGetLocationCheck();

        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {
        // Handle Event Callback
        GoogleApiClient.ConnectionCallbacks callbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                buildFusedLocationProviderClient();
            }

            @Override
            public void onConnectionSuspended(int i) {
                mGoogleApiClient.connect();
                // todo: handle waiting progress circle
                Log.e(TAG,"onConnectionSuspended");
            }
        };

        GoogleApiClient.OnConnectionFailedListener failedListener = connectionResult -> {
            //todo: handle connection fail
            Toast.makeText(getApplicationContext(),"GoogleApiClient.OnConnectionFailed", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"OnConnectionFailedListener");
        };

        // Init
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(failedListener)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    //todo: check GPS "status"
    /**
     * This will start Location Update after a "period of time"
     *
     * .setInterval(Define.POLLING_FREQ_MILLI_SECONDS) --> location will update in freq
     * onLocationResult  -->  trigger every time location update
     */
    @SuppressLint("MissingPermission")
    private void buildFusedLocationProviderClient() {
        Log.d(TAG,"buildFusedLocationProviderClient");

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Define.POLLING_FREQ_MILLI_SECONDS)
                .setFastestInterval(Define.FASTEST_UPDATE_FREQ_MILLI_SECONDS);

//                .setSmallestDisplacement(DISPLACEMENT)      //todo: ??? wtf

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //todo: handle when get first location move cam the that
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;       // get current location
                }

                if (mLastLocation != null) {
                    Log.d(TAG,"onLocationResult: "+ mLastLocation.getBearing()+ "," + mLastLocation.getAccuracy());

                    realTimeChecking();
                    firstGetLocationCheck();
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);

                // if isLocationAvailable return false you can assume that location will not be returned in onLocationResult
                if (locationAvailability.isLocationAvailable() == false) {
                    mFusedLocationClient.flushLocations();
                }
                Log.d(TAG,"onLocationAvailability: "+ locationAvailability.isLocationAvailable());
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Handle GPS status "Device Only"
     */
    private void GetLocationInDeviceOnlyMode() {

    }

    /**
     * Use when need stop checking
     */
    private void stopLocationUpdate() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Use this for resume
     */
    @SuppressLint("MissingPermission")
    private void resumeLocationUpdate() {
        if (mGoogleApiClient != null && mFusedLocationClient != null) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } else {
            buildGoogleApiClient();
        }
    }


    boolean isFirstGetLocation;
    @SuppressLint("MissingPermission")
    /**
     * Use this to get Location first time and get it very quick
     * !! Only work when there is a "last location" information on cache whether there is your app or others
     */
    private void firstGetLocationCheck() {
        // first get location handle
        if(!isFirstGetLocation)
        {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(DriverActivity.this, location -> {
                        Log.i(TAG,"firstGetLocationCheck : onSuccess");
                        if (location != null) {
                            mLastLocation = location;

                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LocationUtils.locaToLatLng(mLastLocation),
                                    Define.MAP_BOUND_POINT_ZOOM);
                            mMap.moveCamera(update);

                            isFirstGetLocation = true;
                        }
                    });
        }
    }

    //endregion

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

        //Geo Fire
        drivers = dbRefe.child(Define.DB_DRIVERS);
        geoFire = new GeoFire(drivers);
        mapService = Common.getGoogleAPI();

    }

    private void Init() {

        // Init View
        locationDriver_switch = findViewById(R.id.locationDriver_switch);

        polyLineList = new ArrayList<>();
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
            Intent intent = new Intent(getApplicationContext(), HitchActivity.class);
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



    // ve xe nho nho
    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < polyLineList.size() - 1) {
                index++;
                next = index + 1;
            }
            if (index < polyLineList.size() - 1) {
                startPostion = polyLineList.get(index);
                endPosition = polyLineList.get(next);
            }

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v = animation.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v) * startPostion.longitude;
                    lat = v * endPosition.latitude + (1 - v) * startPostion.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    carMaker.setPosition(newPos);
                    carMaker.setAnchor(0.5f, 0.5f);
                    carMaker.setRotation(getBearing(startPostion, newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(newPos)
                                    .zoom(15.5f)
                                    .build())
                    );
                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 3000);
        }
    };

    // xet quay dau xe
    private float getBearing(LatLng startPostion, LatLng endPosition) {
        double lat = Math.abs(startPostion.latitude - endPosition.latitude);
        double lng = Math.abs(startPostion.longitude - endPosition.longitude);

        if (startPostion.latitude < endPosition.latitude && startPostion.longitude < endPosition.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        } else if (startPostion.latitude >= endPosition.latitude && startPostion.longitude < endPosition.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        } else if (startPostion.latitude >= endPosition.latitude && startPostion.longitude >= endPosition.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        } else if (startPostion.latitude < endPosition.latitude && startPostion.longitude >= endPosition.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        }
        return -1;
    }




    ///// Moved
    private void drawDirection() {
        currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        // use requestApi to determine if the api is working correctly --> if not get the URL in the log to Debug
        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + destination + "&" +
                    "key=" + getResources().getString(R.string.map_api_key);
            Log.d("Test", requestApi); // Print URL for debug


            ///// Draw direction
            // setup the request get data ( set up the call )
            Call<String> call = mapService.getPath(requestApi);
            // enqueue: execute asynchronously . User a Callback to get respond from the server
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        String responseString = response.body();        // get response object
                        // the response String is the whole JSON file have info of the direction

                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polyLineList = decodePoly(polyline); //decodePoly from Internet 3

                        }

                        //Adjusting lounds
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latLng : polyLineList) {
                            builder.include(latLng);
                        }

                        LatLngBounds bounds = builder.build();
                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                        mMap.animateCamera(mCameraUpdate);

                        // polyline Property
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.GRAY);
                        polylineOptions.width(5);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.endCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polyLineList);
                        greyPolyline = mMap.addPolyline(polylineOptions);

                        blackPolylineOptions = new PolylineOptions();
                        blackPolylineOptions.color(Color.BLACK);
                        blackPolylineOptions.width(5);
                        blackPolylineOptions.startCap(new SquareCap());
                        blackPolylineOptions.endCap(new SquareCap());
                        blackPolylineOptions.jointType(JointType.ROUND);
                        blackPolyline = mMap.addPolyline(blackPolylineOptions);

                        mMap.addMarker(new MarkerOptions()
                                .position(polyLineList.get(polyLineList.size() - 1))
                                .title("Pickup Location"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDirection() {
        currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + destination + "&" +
                    "key=" + getResources().getString(R.string.map_api_key);
            Log.d("Test", requestApi); // Print URL for debug
            mapService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline); //decodePoly from Internet (decodepoly encode android)

                                }

                                //Adjusting lounds
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList) {
                                    builder.include(latLng);
                                }

                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);


                                // polyline Property
                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyline = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(Color.rgb(255, 20, 147));
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1))
                                        .title("Pickup Location"));

                                //Animation
                                ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0, 100);
                                polyLineAnimator.setDuration(2000);
                                polyLineAnimator.setInterpolator(new LinearInterpolator());
                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        List<LatLng> points = greyPolyline.getPoints();
                                        int percentValue = (int) animation.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);
                                    }
                                });
                                polyLineAnimator.start();

                                carMaker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                handler = new Handler();
                                index = -1;
                                next = 1;
                                handler.postDelayed(drawPathRunnable, 3000);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ///////////

    private void requesRoute(String uid) {
        String title = btn_endLocation.getText().toString();

        DatabaseReference dbRequest = dbRefe.child(Define.DB_DRIVER_REQUESTS);
        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        dbRequest.child(uid).child("locationName").setValue(title);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        // show info
        carMaker = mMap.addMarker(new MarkerOptions()
                .title(title)
                .snippet(formattedDate)
                .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        carMaker.showInfoWindow();

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }






    private void loadAllAvailableDriver() {
        //load all available Driver in distance 3km
        DatabaseReference driverLocation = dbRefe.child("Divers");
        GeoFire geoFireDrivers = new GeoFire(driverLocation);

        GeoQuery geoQuery = geoFireDrivers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                dbRefe.child(Define.DB_USERS_INFO)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);

                                //Add driver to map
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .flat(true)
                                        .title(userInfo.getName())
                                        .snippet("Phone: " + userInfo.getPhoneNumber())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= 3) {
                    distance++;
                    loadAllAvailableDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

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
