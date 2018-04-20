package com.example.huydaoduc.hieu.chi.hhapp.Manager.Place;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

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
        llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);


        mSearchEdittext = (EditText)findViewById(R.id.search_et);
        mClear = (ImageView)findViewById(R.id.clear);
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


        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch,
                mGoogleApiClient, bounds, typeFilter);
        mRecyclerView.setAdapter(mAdapter);

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
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }

                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
//                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
     * Occurs when Place result returned from adapter
     * Occurs only when result return > 0
     */
    @Override
    public void OnPlaceResultReturn() {
        // todo: start and end location
        stopLoadingProcess();
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

                                    //todo: loading animation

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
