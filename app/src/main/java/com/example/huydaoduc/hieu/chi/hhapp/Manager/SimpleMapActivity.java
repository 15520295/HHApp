package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.huydaoduc.hieu.chi.hhapp.CostomInfoWindow.CustomInfoWindow;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class SimpleMapActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback {

    protected String TAG = "SimpleMapActivity";

    protected FusedLocationProviderClient mFusedLocationClient;
    protected LocationCallback mLocationCallback;
    protected LocationRequest mLocationRequest;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected GoogleMap mMap;

    protected SupportMapFragment mapFragment;

    public SimpleMapListener simpleMapListener;

    public interface SimpleMapListener {
        void OnRealTimeLocationUpdate();
    }


    //region ------ Setup Activity (Fixed)  --------
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
    protected void setUpLocation() {
        firstGetLocationCheck();

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
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
            Log.e(TAG,"OnConnectionFailedListener");
        };

        // Init
        mGoogleApiClient = new GoogleApiClient.Builder(this.getApplicationContext())
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
    protected void buildFusedLocationProviderClient() {
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

                    simpleMapListener.OnRealTimeLocationUpdate();
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
    protected void GetLocationInDeviceOnlyMode() {

    }

    /**
     * Use when need stop checking
     */
    protected void stopLocationUpdate() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Use this for resume
     */
    @SuppressLint("MissingPermission")
    protected void resumeLocationUpdate() {
        if (mGoogleApiClient != null && mFusedLocationClient != null) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } else {
            buildGoogleApiClient();
        }
    }


    protected boolean isFirstGetLocation;
    @SuppressLint("MissingPermission")
    /**
     * Use this to get Location first time and get it very quick
     * !! Only work when there is a "last location" information on cache whether there is your app or others
     */
    protected void firstGetLocationCheck() {
        // first get location handle
        if(!isFirstGetLocation)
        {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
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

    //region ------ My Location Button --------

    @SuppressLint("MissingPermission")
    protected void initMyLocationButton() {
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

    //region ------ Camera Manager --------

    protected MapCameraManager cameraManager;

    protected void initCameraManager() {
        cameraManager = new MapCameraManager(mMap);
    }

    //endregion

    //region ------ Direction Manager --------

    protected DirectionManager directionManager;

    /**
     * Call only when map is ready
     */
    protected void initDirectionManager() {
        directionManager = new DirectionManager(this, mMap);
    }

    //endregion

    //region ------ Makers --------

    protected MarkerManager markerManager;

    protected void initMarkerManager() {
        GoogleMap.OnMarkerClickListener listener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                return false;
            }
        };

        markerManager = new MarkerManager(mMap, listener);
    }

    //endregion

}
