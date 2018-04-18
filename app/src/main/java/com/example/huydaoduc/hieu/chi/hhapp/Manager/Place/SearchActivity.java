package com.example.huydaoduc.hieu.chi.hhapp.Manager.Place;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

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
public class SearchActivity extends AppCompatActivity implements PlaceAutocompleteAdapter.PlaceAutoCompleteInterface, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,OnClickListener,SavedPlaceListener {
    Context mContext;
    GoogleApiClient mGoogleApiClient;

    LinearLayout mParent;
    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    PlaceAutocompleteAdapter mAdapter;
    List<SavedAddress> mSavedAddressList;
//    PlaceSavedAdapter mSavedAdapter;
    LatLngBounds bounds;


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
            bounds = toBounds(new LatLng(cur_lat, cur_lng),radius);

        initViews();
    }

    /*
        Initialize Views
    */
    private void initViews(){
        mRecyclerView = (RecyclerView)findViewById(R.id.list_search);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);

        mSearchEdittext = (EditText)findViewById(R.id.search_et);
        mClear = (ImageView)findViewById(R.id.clear);
        mClear.setOnClickListener(this);

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
                if (count > 0) {
                    mClear.setVisibility(View.VISIBLE);
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
//                    mClear.setVisibility(View.GONE);
//                    if (mSavedAdapter != null && mSavedAddressList.size() > 0) {
//                        mRecyclerView.setAdapter(mSavedAdapter);
//                    }
                }
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
//                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e("", "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    @Override
    public void onClick(View v) {
        SearchActivity.this.finish();
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

    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.CusPlaceAutocomplete> mResultList, int position) {
        if(mResultList!=null){
            try {
                final PlaceAutocompleteAdapter.CusPlaceAutocomplete place = mResultList.get(position);

                //Do the things here on Click.....
                Intent returnIntent  = new Intent();
                returnIntent.putExtra("place_id",String.valueOf(place.placeId));
                returnIntent.putExtra("place_primary_text", String.valueOf(place.primaryText));
                setResult(SearchActivity.RESULT_OK, returnIntent );
                finish();
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
}
