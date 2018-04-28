package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SavedPlace;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Place.SearchActivity;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.SimpleMapActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.huydaoduc.hieu.chi.hhapp.R;

public class CreateRouteActivity extends SimpleMapActivity implements SimpleMapActivity.SimpleMapListener {

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

        Init();
        Event();
    }

    private void Init() {
        btn_start_picker = findViewById(R.id.btn_start_place_picker);
        btn_end_picker = findViewById(R.id.btn_end_place_picker);

        btn_date_picker = findViewById(R.id.btn_date_picker);

        initDatePicker();
    }

    private void Event() {
        searViewEvent();

        btn_date_picker.setOnClickListener(view -> {
//            datePickerDialog.show(getFragmentManager(),"datepicker");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AutoCompleteIntentResultHandle(requestCode,resultCode,data);
    }

    @Override
    public void OnRealTimeLocationUpdate() {

    }

    //region ------ Auto Complete  --------
    Button btn_date_picker;
//    DatePickerDialog datePickerDialog;


    private void initDatePicker() {
//        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//
//            }
//        };

//        datePickerDialog = DatePickerDialog.newInstance(listener, Calendar.getInstance());
//        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
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
            return new SavedPlace();
        }
        return startPlace;
    }

    private SavedPlace getEndPlace() {
        if (endPlace == null) {
            Log.e(TAG, "endPlace null");
            return new SavedPlace();
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
                    startPlace = new SavedPlace();
                    startPlace.setId(placeId);
                    startPlace.setPrimaryText(placePrimaryText);
                    startPlace.setAddress(placeAddress);
                    startPlace.setLocation(placeLocation);

                    btn_start_picker.setText(placePrimaryText);

                    markerManager.draw_PickupPlaceMarker(startPlace);
                } else if (requestCode == END_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                    endPlace = new SavedPlace();
                    endPlace.setId(placeId);
                    endPlace.setPrimaryText(placePrimaryText);
                    endPlace.setAddress(placeAddress);
                    endPlace.setLocation(placeLocation);

                    btn_end_picker.setText(placePrimaryText);

                    markerManager.draw_DropPlaceMarker(endPlace);
                }

                // Move Camera
                if (startPlace != null && endPlace != null) {
                    cameraManager.moveCam(startPlace.func_getLatLngLocation(), endPlace.func_getLatLngLocation());
                }
                else if (startPlace != null) {
                    cameraManager.moveCam(startPlace.func_getLatLngLocation());
                } else if (endPlace != null) {
                    cameraManager.moveCam(endPlace.func_getLatLngLocation());
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
