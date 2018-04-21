//package com.example.huydaoduc.hieu.chi.hhapp.Main;
//
//import android.Manifest;
//import android.animation.ValueAnimator;
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.location.Location;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.util.Log;
//import android.view.View;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.animation.LinearInterpolator;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.firebase.geofire.GeoFire;
//import com.firebase.geofire.GeoLocation;
//import com.firebase.geofire.GeoQuery;
//import com.firebase.geofire.GeoQueryEventListener;
//import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.JointType;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.maps.model.SquareCap;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public  MapsActivity extends AppCompatActivity
//        implements OnMapReadyCallback {
//
//    GoogleMap mGoogleMap;
//    SupportMapFragment mapFrag;
//    LocationRequest mLocationRequest;
//    Location mLastLocation;
//    Marker mCurrLocationMarker;
//    FusedLocationProviderClient mFusedLocationClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        getSupportActionBar().setTitle("Map Location Activity");
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFrag.getMapAsync(this);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        //stop location updates when Activity is no longer active
//        if (mFusedLocationClient != null) {
//            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//        }
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap)
//    {
//        mGoogleMap=googleMap;
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(120000); // two minute interval
//        mLocationRequest.setFastestInterval(120000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                //Location Permission already granted
//                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//                mGoogleMap.setMyLocationEnabled(true);
//            } else {
//                //Request Location Permission
//                checkLocationPermission();
//            }
//        }
//        else {
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//            mGoogleMap.setMyLocationEnabled(true);
//        }
//    }
//
//    LocationCallback mLocationCallback = new LocationCallback(){
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            for (Location location : locationResult.getLocations()) {
//                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
//                mLastLocation = location;
//                if (mCurrLocationMarker != null) {
//                    mCurrLocationMarker.remove();
//                }
//
//                //Place current location marker
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//
//                //move map camera
//                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
//            }
//        };
//
//    };
//
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
//    private void checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                new AlertDialog.Builder(this)
//                        .setTitle("Location Permission Needed")
//                        .setMessage("This app needs the Location permission, please accept to use location functionality")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Prompt the user once explanation has been shown
//                                ActivityCompat.requestPermissions(MapsActivity.this,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        MY_PERMISSIONS_REQUEST_LOCATION );
//                            }
//                        })
//                        .create()
//                        .show();
//
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION );
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // location-related task you need to do.
//                    if (ContextCompat.checkSelfPermission(this,
//                            Manifest.permission.ACCESS_FINE_LOCATION)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//                        mGoogleMap.setMyLocationEnabled(true);
//                    }
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//
//    }
//
//}
