package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.location.LocationListener;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Common.Common;
import com.example.huydaoduc.hieu.chi.hhapp.CostomInfoWindow.CustomInfoWindow;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.CheckActivityCloseService;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.DirectionManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.MapCameraManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.MarkerManager;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SearchActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.User.RealtimeUser;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.User.UserApp;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.User.UserState;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.DBManager;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.Remote.IGoogleAPI;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

//todo: check 1 tai khoan dang nhap 2 may
public class Home extends AppCompatActivity
        implements
        // My Location Button
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback
{
  
    private static final String TAG = "RiderActivity";

    // store all info in the map
    private GoogleMap mMap;

    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;



    private static int UPDATE_INTERVAL = 3000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire geoFire;

    MaterialAnimatedSwitch locationRider_switch;
    SupportMapFragment mapFragment;

    DatabaseReference onlineRef, currenUserRef;

    //Car animation
    private List<LatLng> polyLineList;
    private Marker carMaker, userMaker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPostion, endPosition, currentPosition;
    private int index, next;
    private Button btnPost, btnFindDriver, btnMessage, btnCall;
    private TextView tvName, tvPhone;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;

    private IGoogleAPI mapService;


    boolean isDriverFoundHuy = false;
    String driverId = "";
    int radius = 1; //1km
    int distance = 1; //3km

    //------------------------------------ Chi :

    // Activity Property

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private UserState userState;

    Dialog dialogInfo;
    DatabaseReference dbRefe;

    //region ------ Makers --------

    MarkerManager markerManager;
    private void initMarkerManager() {
        GoogleMap.OnMarkerClickListener listener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.equals(markerManager.driverMarker)) {
                    dialogInfo.show();
                    return true;
                }
                if (marker.equals(markerManager.pickupPlaceMarker)) {
                    return true;
                }
                if (marker.equals(markerManager.endPlaceMarker)) {
                    return true;
                }

                return false;
            }
        };

        markerManager = new MarkerManager(mMap, listener);


    }
    //endregion

    //region ------ Real time checking --------



    //endregion

    //region ------ FindDriver --------

    boolean isDriverFound;

    private void findDriver() {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Define.DB_ROUTEREQUESTS);

        dbRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if (isDriverFound)
                    {
                        break;      // if found the driver done checking
                    }
                    RouteRequest request = postSnapshot.getValue(RouteRequest.class);
                    isMatch(request);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //todo : run synchronously for every check
    //todo : push info off rider to driver ( driver will check and answer if he will pickup)
    //todo : check if user swich off
    //todo : check if not find any driver

    /**
     * check if Pickup Place and End Place match to the Request Polyline
     */
    private void isMatch(final RouteRequest request) {
        //get Location From Request
        LatLng latLng_startLocation = LocationUtils.strToLatLng(request.getStartLocation());
        LatLng latLng_endLocation = LocationUtils.strToLatLng(request.getEndLocation());

        // find the Direction depend on Request
        directionManager.findPath(latLng_startLocation, latLng_endLocation,
                new DirectionFinderListener() {
                    @Override
                    public void onDirectionFinderStart() {

                    }

                    @Override
                    public void onDirectionFinderSuccess(List<Route> routes) {
                        if (!isDriverFound) {
                            // get Driver Request Polyline
                            final List<LatLng> polyline = LocationUtils.getPointsFromRoute(routes.get(0));

                            // check if Pickup Place and End Place match to the Polyline
                            boolean isMatch = LocationUtils.isMatching(polyline,pickupPlace.getLatLng(),endPlace.getLatLng(),500);
                            if(isMatch)
                            {
                                isDriverFound = true;
                                setUpFoundDriverDialog(request.getUid());
                            }
                        }
                    }
                });
    }

    /**
     * If Realtime user is in "GOING state" and NOT "time out" then get User info as marker
     */
    //todo: add Driver end location
    private void setUpFoundDriverDialog(String driverUId) {
        DBManager.getRealtimeUserById(driverUId, (realtimeUser ->
        {
            //todo: check lai dieu kien cho dung
            // check if user is GOING && Time out
            if (realtimeUser.getState() == UserState.GOING && !realtimeUser.func_isTimeOut(Define.REALTIME_USER_TIMEOUT))
            {
                // get Driver Info
                DBManager.getUserById(realtimeUser.getUid(), (userApp) ->
                        {
                            markerManager.draw_DriverMarker(userApp, realtimeUser);
                            setUpDialogInfo(userApp);
                        }
                );
            }
        }));
    }

    private void setUpDialogInfo(final UserApp driverInfo) {
        dialogInfo = new Dialog(Home.this);
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
                    if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {

                        Home.this.startActivity(intent);
                        dialogInfo.dismiss();

                    } else {
                        ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.CALL_PHONE}, 1001);
                    }
                }
            }
        });
    }


    // post request
    DatabaseReference currUserOnlineDBR;

    public void realTimeChecking() {
        if(userState == UserState.GOING)
            ;
//            realTimeCheckingLocationAndRouteRequest();
    }
//
//    private void startRealTimeCheckingAndShowRoute() {
//
//
//        //todo: not work when phone turn off
//        // delete data when App Kill
//        Intent serviceIntent = new Intent(DriverActivity.this, CheckActivityCloseService.class);
//        serviceIntent.putExtra("uid", getCurUid());
//        DriverActivity.this.startService(serviceIntent);
//    }
//
//    /**
//     * Put Route Request Online
//     */
//    private void putRouteRequest(Route route) {
//        String uid = getCurUid();
//
//        RouteRequest routeRequest = RouteRequest.getRouteRequestFromLeg(route, uid);
//
//        DatabaseReference dbRequest = dbRefe.child(Define.DB_ROUTEREQUESTS);
//
//        dbRequest.child(uid).setValue(routeRequest);
//    }
//
//
//    private void realTimeCheckingLocationAndRouteRequest() {
//        // todo: handle if getAccuracy > 100 --> will not update data
//
//        // Get old value and Check if location out of radius or Out of time Then update Route Request
//        DBManager.getRealtimeUserById(getCurUid(), realtimeUser -> {
//            // Check with distance
//            if (Define.REALTIME_USER_RADIUS_UPDATE < LocationUtils.calDistance(LocationUtils.locaToLatLng(mLastLocation), LocationUtils.strToLatLng(realtimeUser.getLocation()))) {
//                updateRouteRequest();
//            }
//            // Check with time out
//            else if (realtimeUser.func_isTimeOut(Define.REALTIME_USER_TIMEOUT)) {
//                updateRouteRequest();
//            }
//        });
//
//        // Update new RealTime User value
//        RealtimeUser realtimeUser = new RealtimeUser(currUserOnlineDBR.getKey(), mLastLocation, UserState.GOING);
//        currUserOnlineDBR.setValue(realtimeUser);
//
//    }
//
//    private void updateRouteRequest() {
//        // find routes
//        directionManager.findPath(mLastLocation, et_endLocation.getText().toString(),
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
//    }
//
//    private void endRealTimeChecking() {
//        if (userState == UserState.GOING) {
//            directionManager.removeAllRoutes();
//            markerManager.endPlaceMarker.remove();
//            userState = UserState.NONE;
//
//            dbRefe.child(Define.DB_ROUTEREQUESTS).child(getCurUid()).removeValue();
//            currUserOnlineDBR.removeValue();
//        }
//    }


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

    //region ------ Camera Manager --------

    MapCameraManager cameraManager;

    private void initCameraManager() {
        cameraManager = new MapCameraManager(mMap);
    }

    //endregion

    //region ------ My Location Button --------

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

    int PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2001;
    int END_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2002;
    EditText et_pickupLocation, et_endLocation;

    SavedPlace endPlace, pickupPlace;

    private void searViewEvent() {
        et_pickupLocation.setOnClickListener(v ->
                StartAutoCompleteIntent(PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE));

        et_endLocation.setOnClickListener(v ->
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
                    pickupPlace.setName(placePrimaryText);
                    pickupPlace.setAddress(placeAddress);
                    pickupPlace.setLatLng(LocationUtils.strToLatLng(placeLocation));

                    et_pickupLocation.setText(placePrimaryText);

                    markerManager.draw_PickupPlaceMarker(pickupPlace);
                } else if (requestCode == END_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                    endPlace = new SavedPlace();
                    endPlace.setId(placeId);
                    endPlace.setName(placePrimaryText);
                    endPlace.setAddress(placeAddress);
                    endPlace.setLatLng(LocationUtils.strToLatLng(placeLocation));

                    et_endLocation.setText(placePrimaryText);

                    markerManager.draw_EndPlaceMarker(endPlace);
                }

                // Move Camera
                if (pickupPlace != null && endPlace != null) {
                    cameraManager.moveCam(pickupPlace.getLatLng(), endPlace.getLatLng());
                }
                else if (pickupPlace != null) {
                    cameraManager.moveCam(pickupPlace.getLatLng());
                } else if (endPlace != null) {
                    cameraManager.moveCam(endPlace.getLatLng());
                }
            }
        }
    }

    private void StartAutoCompleteIntent(int requestCode) {
        Intent intent = new Intent(Home. this,SearchActivity.class);
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

    //region ------ Setup Activity (Fixed)  --------

    // Setup -----------------
    //todo: handle GPS off --> equals no connection
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;

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
                    .addOnSuccessListener(Home.this, location -> {
                        Log.i(TAG,"firstGetLocationCheck : onSuccess");
                        if (location != null) {
                            mLastLocation = location;

                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LocationUtils.locaToLatLng(mLastLocation),
                                    Define.MAP_BOUND_POINT_ZOOM);
                            mMap.moveCamera(update);
                        }
                    });
            isFirstGetLocation = true;
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
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Home.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // set Callback listener

        isFirstGetLocation = false;

        //
        /*onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        currenUserRef = FirebaseDatabase.getInstance().getReference(Define.DB_DRIVERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currenUserRef.onDisconnect().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


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
        drivers = FirebaseDatabase.getInstance().getReference(Define.DB_DRIVERS);
        geoFire = new GeoFire(drivers);
        mapService = Common.getGoogleAPI();


    }


    private void Init() {

        // Init firebase
        dbRefe = FirebaseDatabase.getInstance().getReference();

        // Init View
        locationRider_switch = findViewById(R.id.locationRider_switch);

        polyLineList = new ArrayList<>();
        btnPost = findViewById(R.id.btnPost);
        btnFindDriver = findViewById(R.id.btn_find_driver);


        // search view
        et_pickupLocation = findViewById(R.id.et_pick_up_location);
        et_endLocation= findViewById(R.id.et_end_location);

    }

    private void addEven() {

        //// Rider
        locationRider_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean b) {
                if (b) {
//                    startLocationUpdates();
//
//                    // move cam + show maker + update firebase value
//                    displayLocationAndUpdateData();
//                    Snackbar.make(mapFragment.getView(), "You are Online", Snackbar.LENGTH_SHORT).show();


                } else {

                    // remove maker
                    if (carMaker != null)
                        carMaker.remove();
                    mMap.clear();
//                    handler.removeCallbacks(drawPathRunnable);
                    Snackbar.make(mapFragment.getView(), "You are Offline", Snackbar.LENGTH_SHORT).show();
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

                isDriverFound = false;

            }
        });

        btnFindDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDriver();
            }
        });

        searViewEvent();

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

    ///////////


//    private void findDriver() {
//        final DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(Define.DB_ROUTEREQUESTS);
//        GeoFire geoFireDrivers = new GeoFire(drivers);
//
//        GeoQuery geoQuery = geoFireDrivers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
//        geoQuery.removeAllListeners();
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, final GeoLocation location) {
//                //if found
//                if (!isDriverFoundHuy) {
//                    isDriverFoundHuy = true;
//                    driverId = key;
//                    //btnFindDriver.setText("Call Driver");
//                    Toast.makeText(getApplicationContext(), "" + key, Toast.LENGTH_LONG).show();
//
//                }
//                final String[] locationName = new String[1];
//
//                drivers.child(driverId).child("locationName").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        locationName[0] = dataSnapshot.getValue(String.class);
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//                FirebaseDatabase.getInstance().getReference(Define.DB_USERS)
//                        .child(key)
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                final UserApp user = dataSnapshot.getValue(UserApp.class);
//
//                                //Add driver to map
//                                carMaker = mMap.addMarker(new MarkerOptions()
//                                        .position(new LatLng(location.latitude, location.longitude))
//                                        .flat(true)
//                                        .title(locationName[0])
//                                        .snippet("Phone: " + user.getPhoneNumber())
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
//                                carMaker.showInfoWindow();
//
//
//                                // get maker just show to add click listener
//                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                                    @Override
//                                    public void onInfoWindowClick(Marker marker) {
//                                        //Toast.makeText(getApplicationContext(),"infoss",Toast.LENGTH_LONG).show();
//                                        final Dialog dialog = new Dialog(Home.this);
//                                        dialog.setContentView(R.layout.info_user);
//
//                                        btnMessage = dialog.findViewById(R.id.btnMessage);
//                                        btnCall = dialog.findViewById(R.id.btnCall);
//                                        tvName = dialog.findViewById(R.id.tvName);
//                                        tvPhone = dialog.findViewById(R.id.tvPhone);
//
//                                        tvName.setText(user.getName());
//                                        tvPhone.setText("SDT: " + user.getPhoneNumber());
//
//                                        btnMessage.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Toast.makeText(getApplicationContext(), "Open Messenger", Toast.LENGTH_LONG).show();
//                                                dialog.dismiss();
//                                            }
//                                        });
//
//                                        btnCall.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                //Toast.makeText(getApplicationContext(),"Open Call",Toast.LENGTH_LONG).show();
//                                                Intent intent = new Intent(Intent.ACTION_CALL);
//                                                intent.setData(Uri.parse("tel:" + user.getPhoneNumber()));
//                                                if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                                    // TODO: Consider calling
//                                                    //    ActivityCompat#requestPermissions
//                                                    // here to request the missing permissions, and then overriding
//                                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                    //                                          int[] grantResults)
//                                                    // to handle the case where the user grants the permission. See the documentation
//                                                    // for ActivityCompat#requestPermissions for more details.
//                                                    return;
//                                                }
//                                                startActivity(intent);
//                                                dialog.dismiss();
//                                            }
//                                        });
//
//                                        dialog.show();
//                                    }
//                                });
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//                //if still not found driver, increase distance
//                if (!isDriverFoundHuy) {
//                    radius++;
//                    findDriver();
//                }
//
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//
//    }


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

    private void displayLocationAndUpdateData() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // get cur loca
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            if (locationRider_switch.isChecked()) {
                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();

                //Update To Firebase
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                        //Move camera to this location
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
                        //Add Marker
                        if (carMaker != null) {
                            carMaker.remove();
                        }

                        // create maker
                        userMaker = mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_24px))
                                .position(new LatLng(latitude, longitude))
                                .title("You"));
                    }
                });
            }
        } else {
            Log.d("Error", "Cannot get your location");
        }

    }

    private void loadAllAvailableDriver() {
        //load all available Driver in distance 3km
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference("Divers");
        GeoFire geoFireDrivers = new GeoFire(driverLocation);

        GeoQuery geoQuery = geoFireDrivers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Define.DB_USERS)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserApp userApp = dataSnapshot.getValue(UserApp.class);

                                //Add driver to map
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .flat(true)
                                        .title(userApp.getName())
                                        .snippet("Phone: " + userApp.getPhoneNumber())
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
        // automatically handle clicks on the Home/Up button, so long
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
            Intent intent = new Intent(Home.this, PhoneAuthActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
