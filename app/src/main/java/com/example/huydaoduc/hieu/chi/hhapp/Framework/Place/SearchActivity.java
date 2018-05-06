package com.example.huydaoduc.hieu.chi.hhapp.Framework.Place;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anuj.sharma on 4/6/2016.
 * Customize by Phan Huu Chi on 4/2018 : ALTERNATIVE search with bound = "current_location + radius"  -- use GeoDataClient instead of GeoDataAPI changes made in PlaceAutocompleteAdapter( getAutocomplete )
 *                                       result will depend on the "bound" ( not STRICT to the bound ) see more: https://developers.google.com/android/reference/com/google/android/gms/location/places/GeoDataClient.BoundsMode
 *
 *                                       result also depend on the country by using AutocompleteFilter
 *
 *                                      note: result only return up to 5 place ( Google doc )
 *                                      if want more place use web service backend search: -- load file Json and parse to object
 *                                      https://stackoverflow.com/questions/7255523/google-places-api-number-of-results
 *                                      https://developers.google.com/places/web-service/search
 *
 *                                      View Update:
 *                                      Primary text, Secondary text split
 *
 *                                      add Saved Place
 *
 */
//todo: change to dynamic country + add saved place
//todo: sap xep dua tren khoan cach( xem grab )
public class SearchActivity extends AppCompatActivity implements
        PlaceAutocompleteAdapter.PlaceAutoCompleteInterface,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        SavedPlaceListener {

    private static final String TAG = "SearchActivity";

    Context mContext;
    GoogleApiClient mGoogleApiClient;

    LinearLayout mParent;
    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    PlaceAutocompleteAdapter mAdapter;
    List<SavedAddress> mSavedAddressList;
//    PlaceSavedAdapter mSavedAdapter;
    LatLngBounds bounds;

    ProgressBar loadingBar;

    EditText mSearchEdittext;
    ImageView mClear;

    Button btn_place_picker;

    int PLACE_PICKER_REQUEST = 123;


    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        mContext = SearchActivity.this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        Double cur_lat;
        Double cur_lng;
        Double radius;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                cur_lat = 0.0;
                cur_lng = 0.0;
                radius = 0.0;
            } else {
                cur_lat = extras.getDouble("cur_lat");
                cur_lng = extras.getDouble("cur_lng");
                radius = extras.getDouble("radius");
            }
        } else {
            cur_lat = (Double) savedInstanceState.getSerializable("cur_lat");
            cur_lng = (Double) savedInstanceState.getSerializable("cur_lng");
            radius = (Double) savedInstanceState.getSerializable("radius");
        }

        // Handle null
        if (cur_lat == 0.0 && cur_lng == 0.0) {
            bounds = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
        }
        else
            bounds = LocationUtils.pointToBounds(new LatLng(cur_lat, cur_lng),radius);

        initViews();
    }

    /*
        Initialize Views
    */
    private void initViews(){
        loadingBar = findViewById(R.id.prb_loading);
        mRecyclerView = (RecyclerView)findViewById(R.id.list_search);

        mRecyclerView.setHasFixedSize(true);
        llm = new FixRecyclerViewManager(mContext);
        mRecyclerView.setLayoutManager(llm);


        mSearchEdittext = (EditText)findViewById(R.id.search_et);
        mClear = (ImageView)findViewById(R.id.clear);

        btn_place_picker = findViewById(R.id.btn_place_picker);

        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEdittext.setText("");
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(e ->
                SearchActivity.this.finish());

        // use filter instead of bound
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("VN")
                .build();


        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.row_placesearch,
                mGoogleApiClient, bounds, typeFilter, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        /**
         * Occurs when Place result returned from adapter
         * Occurs only when result return > 0
         */
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> stopLoadingProcess());

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        btn_place_picker.setOnClickListener(e -> {
            try {
                Intent intent = builder.build(SearchActivity.this);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                SearchActivity.this.startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e1) {
                e1.printStackTrace();
            }
//            Intent intent = new Intent(SearchActivity.this,PlacePickerActivity.class);
//            SearchActivity.this.startActivity(intent);
        });


        findPlacesEvent();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getApplicationContext(),data);
                // Return result
                Intent returnIntent  = new Intent();
                returnIntent.putExtra("place_id", place.getId());
                returnIntent.putExtra("place_primary_text", place.getAddress());
                returnIntent.putExtra("place_location", LocationUtils.latLngToStr(place.getLatLng()));
                returnIntent.putExtra("place_address", place.getAddress());

                setResult(SearchActivity.RESULT_OK, returnIntent );

                SearchActivity.this.finish();
            }
        }
    }

    public void findPlacesEvent() throws IndexOutOfBoundsException {
        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runLoadingProcess();

                // check null text
                if (TextUtils.isEmpty(mSearchEdittext.getText().toString())) {
                    mClear.setVisibility(View.GONE);
                    stopLoadingProcess();
                }
                if (count > 0) {
                    mClear.setVisibility(View.VISIBLE);
                }
            }

            private Timer timer=new Timer();
            private final long DELAY = 700; // milliseconds -- waiting 1s after text change to filter list

            @Override
            public void afterTextChanged(Editable s) {
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        // do what you need here afterTextChanged
                        if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                            mAdapter.getFilter().filter(s.toString());
                        } else if (!mGoogleApiClient.isConnected()) {
                            Log.e(TAG, "mGoogleApiClient NOT CONNECTED");
                        }
                    }
                };

                // this for wait 0.5 second
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message message = mHandler.obtainMessage();
                                message.sendToTarget();
                            }
                        },
                        DELAY
                );


            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    /**
     * On Place Click
     * @param mResultList
     * @param position
     */
    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.CusPlaceAutocomplete> mResultList, int position) {
        if(mResultList!=null){
            try {
                final PlaceAutocompleteAdapter.CusPlaceAutocomplete place = mResultList.get(position);
                String placeId = String.valueOf(place.placeId);
                String placePrimaryText = String.valueOf(place.primaryText);
                String placeFullText = String.valueOf(place.fullText);

                Places.GeoDataApi.getPlaceById(mGoogleApiClient, String.valueOf(place.placeId))
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                LatLng placeLocation = null;
                                if (places.getStatus().isSuccess()) {
                                    placeLocation = places.get(0).getLatLng();

                                    // Return result
                                    Intent returnIntent  = new Intent();
                                    returnIntent.putExtra("place_id", placeId);
                                    returnIntent.putExtra("place_primary_text", placePrimaryText);
                                    returnIntent.putExtra("place_location", LocationUtils.latLngToStr(placeLocation));
                                    returnIntent.putExtra("place_address", placeFullText);

                                    setResult(SearchActivity.RESULT_OK, returnIntent );

                                    SearchActivity.this.finish();
                                }
                                places.release();
                            }
                        });



            }
            catch (Exception e){

            }

        }
    }

    @Override
    public void onSavedPlaceClick(ArrayList<SavedAddress> mResultList, int position) {
        if(mResultList!=null){
            try {
                Intent data = new Intent();
                data.putExtra("lat",String.valueOf(mResultList.get(position).getLatitude()));
                data.putExtra("lng", String.valueOf(mResultList.get(position).getLongitude()));
                setResult(SearchActivity.RESULT_OK, data);
                finish();
            }
            catch (Exception e){
                Log.e(TAG,e.getMessage());
            }
        }
    }



    private void runLoadingProcess() {
        if(loadingBar.getVisibility() == View.INVISIBLE)
            loadingBar.setVisibility(View.VISIBLE);

    }

    private void stopLoadingProcess() {
        // todo: not done
        if(loadingBar.getVisibility() == View.VISIBLE)
            loadingBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void finish() {
        super.finish();
        SearchActivity.this.overridePendingTransition(R.anim.anim_activity_none, android.R.anim.fade_out);
    }
}
