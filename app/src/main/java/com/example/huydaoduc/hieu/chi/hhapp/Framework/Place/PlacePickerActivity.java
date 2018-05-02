package com.example.huydaoduc.hieu.chi.hhapp.Framework.Place;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.CostomInfoWindow.CustomInfoWindow;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class PlacePickerActivity extends FragmentActivity implements
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback
{

    private static final String TAG = "PlacePickerActivity";
    private LatLng garageLocation;
    Button btnSetGarageLocation;

    FusedLocationProviderClient mFusedLocationClient;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;

    SupportMapFragment mapFragment;

    //region ------ My Location Button --------

    @SuppressLint("MissingPermission")
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        btnSetGarageLocation = (Button) findViewById(R.id.btn_set_garage_location);

        btnSetGarageLocation.setOnClickListener(e ->{
            CameraPosition cp = mMap.getCameraPosition();

            Marker marker;
            marker = mMap.addMarker(new MarkerOptions().position(cp.target)
                    .anchor(0.5f,1f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_map_pin_40px)));

            Geocoder geocoder = new Geocoder(PlacePickerActivity.this.getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(cp.target.latitude, cp.target.longitude, 1);
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();

                Log.v("IGA", "Address" + add);
                Toast.makeText(getApplicationContext(),add,Toast.LENGTH_LONG).show();
            }  catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PlacePickerActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // onMapReadyCallback
    }

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

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition cp = mMap.getCameraPosition();

            }
        });

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
                    .addOnSuccessListener(PlacePickerActivity.this, location -> {
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

}
