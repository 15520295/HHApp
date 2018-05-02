package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteManager;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.Button;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.Place.SearchActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.SimpleMapActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.NotifyTrip;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.User.UserState;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.bingoogolapple.titlebar.BGATitleBar;

public class CreateRouteActivity extends SimpleMapActivity implements SimpleMapActivity.SimpleMapListener {

    BGATitleBar titleBar;
    private MaterialFancyButton btn_create_route;


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
        // init view
        btn_create_route = findViewById(R.id.btn_create_route);

        btn_start_picker = findViewById(R.id.btn_start_place_picker);
        btn_end_picker = findViewById(R.id.btn_end_place_picker);

        btn_date_picker = findViewById(R.id.btn_date_picker);
        btn_date_picker.setText(TimeUtils.curDateToUserStr());

        btn_time_picker = findViewById(R.id.btn_time_picker);
        btn_time_picker.setText(TimeUtils.curTimeToUserString(15));

        initDateTimePicker();

        titleBar = (BGATitleBar) findViewById(R.id.titlebar);

        // init database
        dbRefe = FirebaseDatabase.getInstance().getReference();
    }

    private void Event() {
        searViewEvent();

        titleBar.setDelegate(new BGATitleBar.Delegate() {
            @Override
            public void onClickLeftCtv() {
                finish();
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

        btn_date_picker.setOnClickListener(view -> {
            datePickerDialog.show(getFragmentManager(),"datePickerDialog");
        });

        btn_time_picker.setOnClickListener(v ->{
            timePickerDialog.show(getFragmentManager(),"timePickerDialog");
        });

        btn_create_route.setOnClickListener(v -> {
            createRouteRequest(autoCompleteRoute);
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
        if (mLastLocation != null) {
            String startAddress = LocationUtils.getLocationAddress(geocoder,mLastLocation);

            getStartPlace().setAddress(startAddress);
            getStartPlace().setLocation(LocationUtils.locaToStr(mLastLocation));
            getStartPlace().setPrimaryText(startAddress);

            btn_start_picker.setText(getStartPlace().getPrimaryText());

            markerManager.draw_PickupPlaceMarker(getStartPlace());
        }

    }


    // Activity Property
    DatabaseReference dbRefe;

    // User Property
    UserState userState;

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

    /**
     * Put Route Request Online
     */
    private void createRouteRequest(Route route) {
        String driverUId = getCurUid();
        //todo: handle price
        Float percentDiscount = 1f;

        String routeRequestUId = dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId).push().getKey();

        // get Route Request from route
//        RouteRequest routeRequest = RouteRequest.func_createDriverRequestFromRoute(routeRequestUId, driverUId, route, selectedDateTime.getTime(), pricePerKm);

        RouteRequest routeRequest = RouteRequest.Builder
                .aRouteRequest(routeRequestUId)
                .setDriverUId(driverUId)
                .setStartPlace(getStartPlace())
                .setEndPlace(getEndPlace())
                .setStartTime(TimeUtils.dateToStr(selectedDateTime.getTime()))
                .setPercentDiscount(percentDiscount)
                .setSummary("sumary")
                .build();

        dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId)
                .child(routeRequestUId).setValue(routeRequest);


        //Listen to Trip Notify - asynchronous with service
        dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId)
                .child(routeRequestUId).child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NotifyTrip notifyTrip = dataSnapshot.getValue(NotifyTrip.class);

                        if(notifyTrip != null && !notifyTrip.isNotified())
                        {
                            // update NotifyTrip value to notified
                            notifyTrip.setNotified(true);
                            dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId)
                                    .child(routeRequestUId)
                                    .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP).setValue(notifyTrip);

                            // change Route state
                            dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId)
                                    .child(routeRequestUId)
                                    .child(Define.DB_ROUTE_REQUESTS_ROUTE_REQUEST_STATE).setValue(RouteRequestState.FOUND_PASSENGER);

                            // notify driver
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // close and add to list
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
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
        timePickerDialog.setAccentColor(ResourcesCompat.getColor(getResources(), R.color.date_picker_bar, null));

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
