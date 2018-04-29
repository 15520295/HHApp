package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SearchActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserState;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

//todo: check 1 tai khoan dang nhap 2 may
public class DriverActivity extends SimpleMapActivity
        implements
        SimpleMapActivity.SimpleMapListener,
        NavigationView.OnNavigationItemSelectedListener
//        , AcceptingTripFragment.OnAcceptingFragmentListener
{

    private static final String TAG = "DriverActivity";

    MaterialAnimatedSwitch locationDriver_switch;

    private Button btnGo;

    //------------------------------------ Chi :

    // Activity Property
    DatabaseReference dbRefe;


    // User Property
    UserState userState;

    @Override
    public void OnRealTimeLocationUpdate() {
//        if(userState == UserState.D_RECEIVING_BOOKING_HH)
//            realTimeChecking_DriverRequest();
    }

    @Override
    public void OnMapSetupDone() {

    }

    //region ------ HH Request   --------
//    private void startRealTimeCheckingAndShowRoute() {
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
//                        // Draw route
//                        directionManager.drawRoutes(routes, true);
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
//                                .child(Define.DB_ONLINE_USERS_NOTIFY_TRIP)
//                                .addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            String tripUId = dataSnapshot.getValue(String.class);
//
//                                            if(! TextUtils.isEmpty(tripUId))
//                                            {
//                                                showPassengerRequestAndChangeState(tripUId);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                    }
//                });
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
//        //todo: handle price
//        Float pricePerKm = 1f;
//
//        // get Driver Request from route
//        RouteRequest driverRequest = RouteRequest.func_createDriverRequestFromRoute(route, uid, pricePerKm);
//
//        dbRefe.child(Define.DB_ROUTE_REQUESTS).child(uid).setValue(driverRequest);
//    }
//
//    /**
//     * This will trigger when current location update = POLLING_FREQ_MILLI_SECONDS
//     * For "RouteRequest" we will:
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
//            else if (onlineUser.func_isInThePass(Define.ONLINE_USER_TIMEOUT)) {
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
//                        putRouteRequest(routes.get(0));
//                    }
//                });
//    }
//
//    private void endRealTimeChecking() {
//        if (userState == UserState.D_RECEIVING_BOOKING_HH) {
//            directionManager.removeAllRoutes();
//            markerManager.dropPlaceMarker.remove();
//
//            dbRefe.child(Define.DB_ROUTE_REQUESTS).child(getCurUid()).removeValue();
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
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Trip trip = dataSnapshot.getValue(Trip.class);
//
//                showAcceptingFragment(trip);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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
//
//    @Override
//    public void OnTripAcceptTimeOut() {
//
//    }
//
//    @Override
//    public void OnTripAccepted() {
//
//    }
//
//    private void driverAccepted(String tripUId) {
//        // update driver uid to trip info
//        dbRefe.child(Define.DB_TRIPS)
//                .child(tripUId)
//                .child(Define.DB_TRIPS_DRIVER_UID).setValue(getCurUid());
//    }


    //endregion

    //region ------ Auto Complete  --------
    int END_PLACE_AUTOCOMPLETE_REQUEST_CODE = 1002;
    Button btn_endLocation;
    SavedPlace endPlace;

    private SavedPlace getEndPlace() {
        if (endPlace == null) {
            Log.e(TAG, "endPlace null");
            endPlace = new SavedPlace();
            return endPlace;
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

        setupCheckRealtime = true;
        simpleMapListener = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(DriverActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // onMapReadyCallback

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

        // Chi
        searViewEvent();

        btnGo.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CreateRouteActivity.class);
            DriverActivity.this.startActivity(intent);
        });

        locationDriver_switch.setOnCheckedChangeListener(b -> {
            if (b) {
//                startRealTimeCheckingAndShowRoute();
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
