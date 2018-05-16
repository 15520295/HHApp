package com.uit.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.DefineString;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinderListener;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Place.SearchActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.SimpleMapActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserState;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.bingoogolapple.titlebar.BGATitleBar;

public class CreateRouteActivity extends SimpleMapActivity implements SimpleMapActivity.SimpleMapListener {

    BGATitleBar titleBar;
    private MaterialFancyButton btn_create_route;
    private Button btn_select_car_type;

    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        // setup map
        setupCheckRealtime = false;
        simpleMapListener = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CreateRouteActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        isFirstGetLocation = false;

        geocoder = new Geocoder(this, Locale.getDefault());

        Init();
        Event();
        notifyBtnState();
    }

    //region -------------- Button State --------------
    private enum BtnState {
        CREATE,
        ENTER_START_POINT,
        ENTER_END_POINT
    }

    private void mainBtnChangeState(BtnState state) {
        if (state == BtnState.ENTER_START_POINT) {
            btn_create_route.setText("Choose your Start point");
            btn_create_route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_start_picker.callOnClick();
                }
            });
        } else if (state == BtnState.ENTER_END_POINT) {
            btn_create_route.setText("Choose your End point");
            btn_create_route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_end_picker.callOnClick();
                }
            });
        } else {
            btn_create_route.setText("Create Route");
            btn_create_route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createRouteRequest(autoCompleteRoute);
                }
            });
        }

    }

    private void notifyBtnState() {
        if (startPlace == null) {
            mainBtnChangeState(BtnState.ENTER_START_POINT);
        } else if (endPlace == null) {
            mainBtnChangeState(BtnState.ENTER_END_POINT);
        } else {
            mainBtnChangeState(BtnState.CREATE);
        }
    }
    //endregion

    private void Init() {
        // init view
        btn_select_car_type = findViewById(R.id.btn_select_car_type);
        btn_select_car_type.setText(DefineString.CAR_TYPE_MAP.get(selectedCarType));
        btn_create_route = findViewById(R.id.btn_create_route);

        btn_start_picker = findViewById(R.id.btn_start_place_picker);
        btn_end_picker = findViewById(R.id.btn_end_place_picker);

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

        btn_select_car_type.setOnClickListener(v ->{
            showSelectCarTypeFragment();
        });

        btn_create_route.setOnClickListener(v -> {
            createRouteRequest(autoCompleteRoute);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AutoCompleteIntentResultHandle(requestCode, resultCode, data);
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
            String startAddress = LocationUtils.getLocationAddress(geocoder, mLastLocation);
            if (! TextUtils.isEmpty(startAddress)) {
                getStartPlace().setAddress(startAddress);
                getStartPlace().setLocation(LocationUtils.locaToStr(mLastLocation));
                getStartPlace().setPrimaryText(startAddress);

                btn_start_picker.setText(getStartPlace().getPrimaryText());

                markerManager.draw_PickupPlaceMarker(getStartPlace());

                notifyBtnState();
            }
            notifyBtnState();
        } else {
            btn_start_picker.setText(R.string.enter_pick_up);
            btn_start_picker.setSelected(true);
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
        // todo: price v2
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
                .setCarType(selectedCarType)
                .setPercentDiscount(percentDiscount)
                .setSummary("summary")
                .build();

        dbRefe.child(Define.DB_ROUTE_REQUESTS)
                .child(driverUId)
                .child(routeRequestUId)
                .setValue(routeRequest);

//        //Listen to Trip Notify - asynchronous with service
//        dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId)
//                .child(routeRequestUId).child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        NotifyTrip notifyTrip = dataSnapshot.getValue(NotifyTrip.class);
//
//                        if(notifyTrip != null && !notifyTrip.isNotified())
//                        {
//                            // update NotifyTrip value to notified
//                            notifyTrip.setNotified(true);
//                            dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId)
//                                    .child(routeRequestUId)
//                                    .child(Define.DB_ROUTE_REQUESTS_NOTIFY_TRIP).setValue(notifyTrip);
//
//                            // change Route state
//                            dbRefe.child(Define.DB_ROUTE_REQUESTS).child(driverUId)
//                                    .child(routeRequestUId)
//                                    .child(Define.DB_ROUTE_REQUESTS_ROUTE_REQUEST_STATE).setValue(RouteRequestState.FOUND_PASSENGER);
//
//                            // notify driver
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        // close and add to list
        Intent returnIntent = new Intent();
        returnIntent.putExtra("routeRequest", routeRequest);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    //region ------ Select car type --------
    CarType selectedCarType = CarType.BIKE;

    private void showSelectCarTypeFragment() {
        ArrayList<String> carTypeStr = new ArrayList<>();
        ArrayList<CarType> carTypes = new ArrayList<>();
        for (CarType carType : DefineString.CAR_TYPE_MAP.keySet()) {
            carTypeStr.add(DefineString.CAR_TYPE_MAP.get(carType));
            carTypes.add(carType);
        }

        new MaterialDialog.Builder(this)
                .title(R.string.select_car_type)
                .titleGravity(GravityEnum.START)
                .items(carTypeStr)
                .itemsGravity(GravityEnum.START)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        selectedCarType = carTypes.get(which);
                        updateButtonView();
                    }
                })
                .widgetColorRes(R.color.title_bar_background_color_blue)
                .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                .show();
    }

    private void updateButtonView() {
        btn_select_car_type.setText(DefineString.CAR_TYPE_MAP.get(selectedCarType));

        Drawable img = getApplicationContext().getResources().getDrawable(Define.CAR_TYPE_ICON_MAP.get(selectedCarType));
        btn_select_car_type.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
    }

    //endregion

    //region ------ Date Time Picker  --------
    Calendar selectedDateTime;
    Button btn_date_picker, btn_time_picker;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    private void initDateTimePicker() {
        btn_date_picker = findViewById(R.id.btn_date_picker);
        btn_date_picker.setText(TimeUtils.curDateToUserDateStr());

        btn_time_picker = findViewById(R.id.btn_time_picker);
        btn_time_picker.setText(TimeUtils.curDateToUserDateStr());

        selectedDateTime = Calendar.getInstance();
        selectedDateTime.add(Calendar.MINUTE, 10);
        btn_time_picker.setText(TimeUtils.timeToUserTimeStr(selectedDateTime.getTime()));

        // Date
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            btn_date_picker.setText(TimeUtils.dateToUserDateStr(dayOfMonth, monthOfYear, year));

            selectedDateTime.set(year, monthOfYear, dayOfMonth);
        };

        datePickerDialog = DatePickerDialog.newInstance(dateSetListener, Calendar.getInstance());
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setAccentColor(ResourcesCompat.getColor(getResources(), R.color.date_picker_bar, null));

        btn_date_picker.setOnClickListener(view -> {
            if (datePickerDialog.isAdded())
                return;

            datePickerDialog.show(getFragmentManager(), "datePickerDialog");
        });

        // Time
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute, second) -> {
            btn_time_picker.setText(TimeUtils.timeToUserTimeStr(hourOfDay, minute));

            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);
            selectedDateTime.set(Calendar.SECOND, 0);
            selectedDateTime.set(Calendar.MILLISECOND, 0);
        };

        timePickerDialog = TimePickerDialog.newInstance(timeSetListener, true);
        timePickerDialog.setAccentColor(ResourcesCompat.getColor(getResources(), R.color.date_picker_bar, null));

        btn_time_picker.setOnClickListener(v -> {
            if (timePickerDialog.isAdded())
                return;

            timePickerDialog.show(getFragmentManager(), "timePickerDialog");
        });
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

                // check get place return null
                if (TextUtils.isEmpty(placePrimaryText)) {
                    new MaterialDialog.Builder(this)
                            .content(R.string.can_not_get_location)
                            .positiveText(R.string.ok)
                            .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                            .positiveColor(getResources().getColor(R.color.title_bar_background_color_blue))
                            .widgetColorRes(R.color.title_bar_background_color_blue)
                            .buttonRippleColorRes(R.color.title_bar_background_color_blue)
                            .show();
                    return;
                }

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

                notifyBtnState();
            }
        }
    }

    private void StartAutoCompleteIntent(int requestCode) {
        Intent intent = new Intent(CreateRouteActivity.this, SearchActivity.class);
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
