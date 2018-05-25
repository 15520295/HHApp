package com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.DefineString;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinderListener;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.Leg;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.ImageUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Place.SavedPlace;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Place.SearchActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.SimpleMapActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.AboutApp;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.AboutUser;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.CurUserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerRequestManager.PassengerRequestManagerActivity;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.Trip;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripType;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripFareInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.OnlineUser;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.User.UserState;
import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.uit.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
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

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

//todo: check 1 tai khoan dang nhap 2 may
//todo: handle every database setValue error with one listener : https://www.youtube.com/watch?v=qG14-gpjwMM
public class PassengerActivity extends SimpleMapActivity
        implements
        SimpleMapActivity.SimpleMapListener,
        NavigationView.OnNavigationItemSelectedListener,
        SelectCarTypeFragment.SelectCarTypeFragmentListener
{
    private static final String TAG = "PassengerActivity";
    private static final int FIND_DRIVER_REQUEST_CODE = 80;
    private static final int SELECT_CAR_TYPE_REQUEST_CODE = 81;

    BGATitleBar titlebar;

    Button btn_cd_note, btn_cd_wait_time;

    private TextView tv_fare, tv_duration, tv_car_type;
    private ImageView iv_car_type;

    MaterialFancyButton btn_findDriver;

    private CardView group_trip_info;

    // Activity Property
    Geocoder geocoder;

    private UserState userState;

    DatabaseReference dbRefe;

    @Override
    public void OnRealTimeLocationUpdate() {

    }

    @Override
    public void OnMapSetupDone() {
        if (mLastLocation != null) {
            String startAddress = LocationUtils.getLocationAddress(geocoder, mLastLocation);
            if (! TextUtils.isEmpty(startAddress)) {
                getPickupPlaceInstance().setAddress(startAddress);
                getPickupPlaceInstance().setLocation(LocationUtils.locaToStr(mLastLocation));
                getPickupPlaceInstance().setPrimaryText(startAddress);

                btn_pickupLocation.setText(getPickupPlaceInstance().getPrimaryText());
                btn_pickupLocation.setSelected(true);

                markerManager.draw_PickupPlaceMarker(getPickupPlaceInstance());

                notifyBtnState();
            }
            notifyBtnState();
        } else {
            btn_pickupLocation.setText(R.string.enter_pick_up);
            btn_pickupLocation.setSelected(true);
        }
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

    Boolean isDriverFound;      // --> use for synchronous purpose
    boolean notFoundHH;      // --> use for synchronous purpose
    boolean hhMode;

    Float estimateFare;

    // new Start Booking

    private void startBooking() {
        //todo: put hhMode to screen
        hhMode = true;
        notFoundHH = false;

        estimateFare = 10000f;

        int waitMinute;
        String waitMinuteStr = btn_cd_wait_time.getText().toString();
        if (waitMinuteStr.equals(DefineString.DEFAULT_WAIT_TIME.first))
            waitMinute = DefineString.DEFAULT_WAIT_TIME.second;
        else
            waitMinute = DefineString.WAIT_TIME_MAP.get(waitMinuteStr);

        String notes = btn_cd_note.getText().toString();
        if(notes.equals(DefineString.NOTES_TO_DRIVER_TITLE))
            notes = null;

        // create a trip
        String tripUId = dbRefe.child(Define.DB_TRIPS).push().getKey();
        String passengerRequestUId = dbRefe.child(Define.DB_PASSENGER_REQUESTS).child(getCurUid()).push().getKey();


        // Set up Trip
        // todo: handle car type, notes
        // create Passenger Request
        PassengerRequest passengerRequest = PassengerRequest.Builder.aPassengerRequest(passengerRequestUId)
                .setPassengerUId(getCurUid())
                .setPickUpSavePlace(getPickupPlaceInstance())
                .setDropOffSavePlace(getDropPlaceInstance())
                .setNote(notes)
                .setWaitMinute(waitMinute)
                .setTripFareInfo(getCurTripFareInfoInstance())
                .setPassengerRequestState(PassengerRequestState.FINDING_DRIVER)
                .build();

        Trip trip = Trip.Builder.aTrip(tripUId)
                .setPassengerUId(getCurUid())
                .setPassengerRequestUId(passengerRequest.getPassengerRequestUId())
                .setTripState(TripState.WAITING_ACCEPT)
                .setTripType(TripType.HH)
                .build();

        Intent intent = new Intent(getApplicationContext(), FindingDriverActivity.class);
        intent.putExtra("trip", trip);
        intent.putExtra("passengerRequest", passengerRequest);
        PassengerActivity.this.startActivityForResult(intent,FIND_DRIVER_REQUEST_CODE);
    }

    private TripFareInfo curTripFareInfo;

    public TripFareInfo getCurTripFareInfoInstance() {
        if (curTripFareInfo == null) {
            curTripFareInfo = new TripFareInfo();
        }
        return curTripFareInfo;
    }

    private void updateDistanceAndDuration() {
        directionManager.findPath(getPickupPlaceInstance().func_getLatLngLocation(), getDropPlaceInstance().func_getLatLngLocation()
                , new DirectionFinderListener() {
                    @Override
                    public void onDirectionFinderStart() {

                    }

                    @Override
                    public void onDirectionFinderSuccess(List<Route> routes) {
                        if (routes == null || routes.size() == 0) {
                            Log.e(TAG, "onDirectionFinderSuccess : routes == null || routes.size() == 0");
                            return;
                        }
                        Leg leg = routes.get(0).getLegs().get(0);

                        getCurTripFareInfoInstance().setDistance((float) leg.getDistance().getValue());
                        getCurTripFareInfoInstance().setDuration((float) leg.getDuration().getValue());

                        updateTripFareInfoView();
                    }
                });
    }

    private void updateTripFareInfoView() {
        getCurTripFareInfoInstance().func_RecalculateEstimateFare();

        tv_fare.setText(getCurTripFareInfoInstance().func_getEstimateFareText());
        tv_fare.setVisibility(View.VISIBLE);
        tv_duration.setText(getCurTripFareInfoInstance().func_getDurationText());

        tv_car_type.setText(getCurTripFareInfoInstance().func_getCarTypeText());

        iv_car_type.setImageResource(Define.CAR_TYPE_ICON_MAP.get(getCurTripFareInfoInstance().getCarType()));
    }

    private void showSelectCarTypeFragment() {
        btn_findDriver.startAnimation(AnimationUtils.loadAnimation(PassengerActivity.this, R.anim.anim_fade_out));

        // create
        ArrayList<TripFareInfo> tripFareInfoList = new ArrayList<>();
        for (CarType carType :
                DefineString.CAR_TYPE_MAP.keySet()) {

            TripFareInfo tripFareInfo = new TripFareInfo(carType, getCurTripFareInfoInstance().func_getStartTimeAsDate(), getCurTripFareInfoInstance().getDuration(), getCurTripFareInfoInstance().getDistance());
            tripFareInfoList.add(tripFareInfo);
        }

        // create fragment
        SelectCarTypeFragment selectCarTypeFragment = SelectCarTypeFragment
                .newInstance(tripFareInfoList, getCurTripFareInfoInstance().getCarType());

        selectCarTypeFragment.setTargetFragment(selectCarTypeFragment.getTargetFragment(), SELECT_CAR_TYPE_REQUEST_CODE);
        selectCarTypeFragment.show(getSupportFragmentManager(), "SelectCarTypeFragment");
    }

    private void showButtonFinder() {
        Animation animation = AnimationUtils.loadAnimation(PassengerActivity.this, R.anim.anim_fade_in);
        animation.setDuration(200);
        btn_findDriver.startAnimation(animation);
    }

    @Override
    public void OnCarTypeSelect(CarType carType) {
        getCurTripFareInfoInstance().setCarType(carType);

        updateTripFareInfoView();

        showButtonFinder();
    }

    @Override
    public void OnCancel() {
        showButtonFinder();
    }

    //endregion

    //region ------ Date Time Picker  --------
    Calendar selectedDateTime;
    DatePickerDialog datePickerDialog;
    Button btn_cd_time_picker, btn_cd_date_picker;
    TimePickerDialog timePickerDialog;

    private void initDateTimePicker() {
        selectedDateTime = Calendar.getInstance();

        btn_cd_date_picker = findViewById(R.id.btn_cd_date_picker);
        btn_cd_date_picker.setText(TimeUtils.curDateToUserDateStr());

        btn_cd_time_picker = findViewById(R.id.btn_cd_time_picker);
        btn_cd_time_picker.setText(TimeUtils.timeToUserTimeStr(selectedDateTime.getTime()));

        // Date
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            btn_cd_date_picker.setText(TimeUtils.dateToUserDateStr(dayOfMonth,monthOfYear,year));

            selectedDateTime.set(year,monthOfYear,dayOfMonth);

            getCurTripFareInfoInstance().setStartTime(TimeUtils.dateToStr(selectedDateTime.getTime()));
            updateTripFareInfoView();
        };

        btn_cd_date_picker.setOnClickListener(view -> {
            if(datePickerDialog.isAdded())
                return;

            datePickerDialog.show(getFragmentManager(),"datePickerDialog");
        });

        datePickerDialog = DatePickerDialog.newInstance(dateSetListener, Calendar.getInstance());
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setAccentColor(ResourcesCompat.getColor(getResources(), R.color.date_picker_bar, null));

        // Time
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute, second) -> {
            btn_cd_time_picker.setText(TimeUtils.timeToUserTimeStr(hourOfDay,minute));

            selectedDateTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
            selectedDateTime.set(Calendar.MINUTE,minute);
            selectedDateTime.set(Calendar.SECOND,0);
            selectedDateTime.set(Calendar.MILLISECOND,0);

            getCurTripFareInfoInstance().setStartTime(TimeUtils.dateToStr(selectedDateTime.getTime()));
            updateTripFareInfoView();
        };

        timePickerDialog = TimePickerDialog.newInstance(timeSetListener, true);
        timePickerDialog.setAccentColor(ResourcesCompat.getColor(getResources(), R.color.date_picker_bar, null));

        btn_cd_time_picker.setOnClickListener(v ->{
            if(timePickerDialog.isAdded())
                return;

            timePickerDialog.show(getFragmentManager(),"timePickerDialog");
        });
    }

    //endregion

    //region ------ Auto Complete  --------

    int PICKUP_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2001;
    int END_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2002;
    Button btn_pickupLocation, btn_dropLocation;

    SavedPlace dropPlace, pickupPlace;

    private SavedPlace getDropPlaceInstance() {
        if (dropPlace == null) {
            Log.e(TAG, "startPlace null");
            dropPlace = new SavedPlace();
            return dropPlace;
        }
        return dropPlace;
    }

    private SavedPlace getPickupPlaceInstance() {
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

                    pickupPlace = new SavedPlace();
                    pickupPlace.setId(placeId);
                    pickupPlace.setLocation(placeLocation);
                    if (placePrimaryText == null && placeLocation != null) {
                        String address = LocationUtils.getLocationAddress(geocoder, LocationUtils.strToLatLng(placeLocation));

                        pickupPlace.setPrimaryText(address);
                        pickupPlace.setAddress(address);
                    } else {
                        pickupPlace.setPrimaryText(placePrimaryText);
                        pickupPlace.setAddress(placeAddress);
                    }

                    btn_pickupLocation.setText(placePrimaryText);

                    markerManager.draw_PickupPlaceMarker(pickupPlace);
                } else if (requestCode == END_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                    dropPlace = new SavedPlace();
                    dropPlace.setId(placeId);
                    dropPlace.setLocation(placeLocation);
                    // if network is slow get address by geocoder
                    if (placePrimaryText == null && placeLocation != null) {
                        String address = LocationUtils.getLocationAddress(geocoder, LocationUtils.strToLatLng(placeLocation));

                        dropPlace.setPrimaryText(address);
                        dropPlace.setAddress(address);
                    } else {
                        dropPlace.setPrimaryText(placePrimaryText);
                        dropPlace.setAddress(placeAddress);
                    }

                    btn_dropLocation.setText(placePrimaryText);
                    group_trip_info.setVisibility(View.VISIBLE);

                    markerManager.draw_DropPlaceMarker(dropPlace);
                }

                // Move Camera
                if (pickupPlace != null && dropPlace != null) {
                    cameraManager.moveCam(50,540,50,310 ,pickupPlace.func_getLatLngLocation(), dropPlace.func_getLatLngLocation());
                    updateDistanceAndDuration();
                }
                else if (pickupPlace != null) {
                    cameraManager.moveCam(pickupPlace.func_getLatLngLocation());
                } else if (dropPlace != null) {
                    cameraManager.moveCam(dropPlace.func_getLatLngLocation());
                }

                notifyBtnState();
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

        NavigationResultHandle(requestCode, resultCode, data);
    }



    private String getCurUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    //endregion

    //region -------------- Button State --------------
    private enum BtnState {
        BOOK,
        ENTER_PICK_UP,
        ENTER_DROP_OFF
    }

    private void mainBtnChangeState(BtnState state) {
        if (state == BtnState.ENTER_PICK_UP) {
            btn_findDriver.setText(R.string.where_do_you_start);
            group_trip_info.setVisibility(View.INVISIBLE);
            btn_findDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_pickupLocation.callOnClick();
                }
            });
        } else if (state == BtnState.ENTER_DROP_OFF) {
            btn_findDriver.setText(R.string.where_are_you_going);
            group_trip_info.setVisibility(View.INVISIBLE);
            btn_findDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_dropLocation.callOnClick();
                }
            });
        } else {
            group_trip_info.setVisibility(View.VISIBLE);
            btn_findDriver.setText(R.string.Book);
            btn_findDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startBooking();
                }
            });
        }

    }

    private void notifyBtnState() {
        if (pickupPlace == null) {
            mainBtnChangeState(BtnState.ENTER_PICK_UP);
        } else if (dropPlace == null) {
            mainBtnChangeState(BtnState.ENTER_DROP_OFF);
        } else {
            mainBtnChangeState(BtnState.BOOK);
        }
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
        // Init firebase
        dbRefe = FirebaseDatabase.getInstance().getReference();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // setup map
        setupCheckRealtime = false;
        simpleMapListener = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PassengerActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // set Callback listener
        isFirstGetLocation = false;

        myLocationButton_padBot = 150;
        myLocationButton_padRight = 50;

        geocoder = new Geocoder(this, Locale.getDefault());

        // default value
        isDriverFound = false;

        //
        Init();
        addEven();
        notifyBtnState();
    }

    private void Init() {
        // Init View
        titlebar = findViewById(R.id.titlebar);

        initNavigation();

        btn_findDriver = findViewById(R.id.btn_cancel);

        btn_cd_wait_time = findViewById(R.id.btn_cd_wait_time);
        btn_cd_wait_time.setText(DefineString.DEFAULT_WAIT_TIME.first);
        btn_cd_wait_time.setSelected(true);

        btn_cd_note = findViewById(R.id.btn_cd_note);
        btn_cd_note.setText(DefineString.NOTES_TO_DRIVER_TITLE);

        initDateTimePicker();

        group_trip_info = findViewById(R.id.group_trip_info);
        tv_fare = findViewById(R.id.tv_fare);
        tv_duration = findViewById(R.id.tv_duration);
        tv_car_type = findViewById(R.id.tv_car_type);
        iv_car_type = findViewById(R.id.iv_car_type);

        // search view
        btn_pickupLocation = findViewById(R.id.btn_pick_up_location);
        btn_dropLocation = findViewById(R.id.btn_end_location);
    }

    private void addEven() {
        titlebar.setDelegate(new BGATitleBar.Delegate() {
            @Override
            public void onClickLeftCtv() {
                drawer_layout.openDrawer(GravityCompat.START);
            }

            @Override
            public void onClickTitleCtv() {

            }

            @Override
            public void onClickRightCtv() {
                Intent intent = new Intent(getApplicationContext(), PassengerRequestManagerActivity.class);
                PassengerActivity.this.startActivity(intent);
                finish();
            }

            @Override
            public void onClickRightSecondaryCtv() {

            }
        });

        btn_findDriver.setOnClickListener(v -> startBooking());

        btn_cd_wait_time.setOnClickListener(e ->{
            ArrayList<String> other = new ArrayList<>();

            for (String key : DefineString.WAIT_TIME_MAP.keySet()) {
                other.add(key);
            }

            new MaterialDialog.Builder(this)
                    .title(DefineString.DEFAULT_WAIT_TIME.first)
                    .items(other)
                    .itemsGravity(GravityEnum.CENTER)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            btn_cd_wait_time.setText(other.get(which));
                        }
                    })
                    .widgetColorRes(R.color.title_bar_background_color_blue)
                    .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                    .show();
        });

        btn_cd_note.setOnClickListener(e ->{
            String prefill = null;
            if (! btn_cd_note.getText().toString().equals(DefineString.NOTES_TO_DRIVER_TITLE)) {
                prefill = btn_cd_note.getText().toString();
            }

            new MaterialDialog.Builder(this)
                    .title(DefineString.NOTES_TO_DRIVER_TITLE)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(DefineString.NOTES_TO_DRIVER_HINT, prefill, (dialog, input) ->
                    {
                        if (!TextUtils.isEmpty(input)) {
                            btn_cd_note.setText(input.toString());
                        } else {
                            btn_cd_note.setText(DefineString.NOTES_TO_DRIVER_TITLE);
                        }
                    })
                    .titleColor(getResources().getColor(R.color.title_bar_background_color_blue))
                    .positiveColor(getResources().getColor(R.color.title_bar_background_color_blue))
                    .positiveText(getResources().getString(R.string.confirm).toUpperCase())
                    .negativeText(getResources().getString(R.string.cancel).toUpperCase())
                    .widgetColorRes(R.color.title_bar_background_color_blue)
                    .buttonRippleColorRes(R.color.title_bar_background_color_blue)
                    .show();
        });

        group_trip_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCarTypeFragment();

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


    //region Navigation Bar

    private static final int ABOUT_USER_REQUEST_CODE = 70;

    NavigationView navigationView;
    DrawerLayout drawer_layout;
    View headerLayout;

    private void initNavigation() {
        // Navigation bar
        drawer_layout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);

        updateNavUserInfo();

        RelativeLayout group_Avatar = headerLayout.findViewById(R.id.group_Avatar);
        group_Avatar.setOnClickListener(v ->{
            Intent i = new Intent(PassengerActivity.this, AboutUser.class);
            PassengerActivity.this.startActivityForResult(i, ABOUT_USER_REQUEST_CODE);
        });
    }

    public void updateNavUserInfo() {
        UserInfo userInfo = CurUserInfo.getInstance().getUserInfo();

        ImageView iv_Avatar = headerLayout.findViewById(R.id.iv_Avatar);
        TextView txtName = headerLayout.findViewById(R.id.txtName);
        TextView txtPhone = headerLayout.findViewById(R.id.txtPhone);

        if (userInfo.getPhoto() != null) {
            iv_Avatar.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getPhoto()));
        }
        txtName.setText(userInfo.getName());
        txtPhone.setText(userInfo.getPhoneNumber());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent i = new Intent(PassengerActivity.this, AboutApp.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            PassengerActivity.this.startActivity(i);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PassengerActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.nav_share){

            Intent i = new Intent(Intent.ACTION_SEND);

            i.setType("text/plain");
            String shareBody = "https://play.google.com/store/apps/details?id=com.example.huydaoduc.hieu.chi.hhapp";
            String shareName = "SBike";
            i.putExtra(Intent.EXTRA_TEXT,shareBody);
            i.putExtra(Intent.EXTRA_SUBJECT,shareName);

            startActivity(Intent.createChooser(i, "Sharing"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void NavigationResultHandle(int requestCode, int resultCode, Intent data) {
        if (requestCode == ABOUT_USER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                updateNavUserInfo();
            }
        }
    }

    //endregion

}