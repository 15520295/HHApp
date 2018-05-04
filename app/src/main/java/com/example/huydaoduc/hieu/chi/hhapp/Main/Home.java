package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Common.Common;
import com.example.huydaoduc.hieu.chi.hhapp.CostomInfoWindow.CustomInfoWindow;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinder;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Model.UserApp;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.Remote.IGoogleAPI;
import com.example.huydaoduc.hieu.chi.hhapp.ActivitiesAuth.PhoneAuthActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//todo: check 1 tai khoan dang nhap 2 may
public class Home extends AppCompatActivity
        implements
        // Direction Finder
        DirectionFinderListener,
        // My Location Button
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,

        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final String TAG = "RiderActivity";

    // store all info in the map
    private GoogleMap mMap;

    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    private static int UPDATE_INTERVAL = 3000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire geoFire;

    MaterialAnimatedSwitch locationRider_switch;
    SupportMapFragment mapFragment;

    DatabaseReference onlineRef, currenUserRef;

    //Car animation
    private List<LatLng> polyLineList;
    private Marker carMaker, userMaker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPostion, endPosition, currentPosition;
    private int index, next;
    private Button btnPost, btnFindDriver, btnMessage, btnCall;
    private TextView tvName, tvPhone;
    private PlaceAutocompleteFragment startLocationAutoComplete;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;

    private IGoogleAPI mapService;

    TabHost host;

    boolean isDriverFound = false;
    String driverId = "";
    int radius = 1; //1km
    int distance = 1; //3km

    private TextView txtName;
    private TextView txtPhone;
    private CircleImageView avatar;



    //------------------------------------ Chi :

    //------ DirectionFinder --------
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
//        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {

            for (Leg leg : route.getLegs()) {
                // Move the map to Surround the route
                final int MAP_BOUND_PADDING = 180;  /* In dp */
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(route.getBounds().getNorthEast());
                builder.include(route.getBounds().getSouthWest());
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_BOUND_PADDING);
                mMap.animateCamera(cu);

                Toast.makeText(getApplicationContext(), "Duration + Distance: " + leg.getDuration().getValue() + ", " + leg.getDistance().getValue(), Toast.LENGTH_LONG)
                        .show();

//            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
//                    .title(route.startAddress)
//                    .position(route.startLocation)));
//            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
//                    .title(route.endAddress)
//                    .position(route.endLocation)));

                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);

                int stepsize = leg.getSteps().size();
                for (int i = 0; i < stepsize; i++) {
                    int pontsize = leg.getSteps().get(i).getPoints().size();
                    for (int j = 0; j < pontsize; j++)
                        polylineOptions.add(leg.getSteps().get(i).getPoints().get(j));
                }


                // Note: points  --- is a pairs not a array

                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }
        }
    }


    private void findPath() {
        String origin = "Dai hoc khoa hoc tu nhien";
        String destination = "Dai hoc bach khoa hcm";
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination, getApplicationContext()).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //------ My Location Button --------
    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }


    //------ Auto Complete  --------
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private void AutoCompleEvent() {
        startLocationAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                startLocationAutoComplete.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(getApplicationContext(), "An error occurred: " + status, Toast.LENGTH_LONG)
                    .show();
            }
        });
    }

    private void StartAutoComplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    //------------------------------------

    // ve xe nho nho
    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            if (index < polyLineList.size() - 1) {
                index++;
                next = index + 1;
            }
            if (index < polyLineList.size() - 1) {
                startPostion = polyLineList.get(index);
                endPosition = polyLineList.get(next);
            }

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v = animation.getAnimatedFraction();
                    lng = v * endPosition.longitude + (1 - v) * startPostion.longitude;
                    lat = v * endPosition.latitude + (1 - v) * startPostion.latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    carMaker.setPosition(newPos);
                    carMaker.setAnchor(0.5f, 0.5f);
                    carMaker.setRotation(getBearing(startPostion, newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(newPos)
                                    .zoom(15.5f)
                                    .build())
                    );
                }
            });
            valueAnimator.start();
            handler.postDelayed(this, 3000);
        }
    };

    // xet quay dau xe
    private float getBearing(LatLng startPostion, LatLng endPosition) {
        double lat = Math.abs(startPostion.latitude - endPosition.latitude);
        double lng = Math.abs(startPostion.longitude - endPosition.longitude);

        if (startPostion.latitude < endPosition.latitude && startPostion.longitude < endPosition.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        } else if (startPostion.latitude >= endPosition.latitude && startPostion.longitude < endPosition.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        } else if (startPostion.latitude >= endPosition.latitude && startPostion.longitude >= endPosition.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        } else if (startPostion.latitude < endPosition.latitude && startPostion.longitude >= endPosition.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        }
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);      // set Callback listener

        //
        /*onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        currenUserRef = FirebaseDatabase.getInstance().getReference("Drivers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currenUserRef.onDisconnect().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        // nut ping vi tri


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        txtName = (TextView) header.findViewById(R.id.txtName);
        txtPhone = (TextView) header.findViewById(R.id.txtPhone);
        avatar = (CircleImageView) header.findViewById(R.id.imageViewAvatar) ;


        txtName.setText("Name: " + MainActivity.nameUser);
        txtPhone.setText("Phone: "+MainActivity.phoneNumber);
        Bitmap bitmap = decodeBase64(MainActivity.avatar );
        if(!MainActivity.avatar.equals(""))
            avatar.setImageBitmap(bitmap);
        //
        Init();
        addEven();
        setUpLocation();
        //loadInfor();

        //Geo Fire
        drivers = FirebaseDatabase.getInstance().getReference("Drivers");
        geoFire = new GeoFire(drivers);
        mapService = Common.getGoogleAPI();


    }


    ///// Moved
    private void drawDirection() {
        currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        // use requestApi to determine if the api is working correctly --> if not get the URL in the log to Debug
        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + destination + "&" +
                    "key=" + getResources().getString(R.string.map_api_key);
            Log.d("Test", requestApi); // Print URL for debug


            ///// Draw direction
            // setup the request get data ( set up the call )
            Call<String> call = mapService.getPath(requestApi);
            // enqueue: execute asynchronously . User a Callback to get respond from the server
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        String responseString = response.body();        // get response object
                        // the response String is the whole JSON file have info of the direction

                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polyLineList = decodePoly(polyline); //decodePoly from Internet 3

                        }

                        //Adjusting lounds
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latLng : polyLineList) {
                            builder.include(latLng);
                        }

                        LatLngBounds bounds = builder.build();
                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                        mMap.animateCamera(mCameraUpdate);

                        // polyline Property
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.GRAY);
                        polylineOptions.width(5);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.endCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polyLineList);
                        greyPolyline = mMap.addPolyline(polylineOptions);

                        blackPolylineOptions = new PolylineOptions();
                        blackPolylineOptions.color(Color.BLACK);
                        blackPolylineOptions.width(5);
                        blackPolylineOptions.startCap(new SquareCap());
                        blackPolylineOptions.endCap(new SquareCap());
                        blackPolylineOptions.jointType(JointType.ROUND);
                        blackPolyline = mMap.addPolyline(blackPolylineOptions);

                        mMap.addMarker(new MarkerOptions()
                                .position(polyLineList.get(polyLineList.size() - 1))
                                .title("Pickup Location"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDirection() {
        currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + destination + "&" +
                    "key=" + getResources().getString(R.string.map_api_key);
            Log.d("Test", requestApi); // Print URL for debug
            mapService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline); //decodePoly from Internet (decodepoly encode android)

                                }

                                //Adjusting lounds
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList) {
                                    builder.include(latLng);
                                }

                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                mMap.animateCamera(mCameraUpdate);


                                // polyline Property
                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyline = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(Color.rgb(255, 20, 147));
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size() - 1))
                                        .title("Pickup Location"));

                                //Animation
                                ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0, 100);
                                polyLineAnimator.setDuration(2000);
                                polyLineAnimator.setInterpolator(new LinearInterpolator());
                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        List<LatLng> points = greyPolyline.getPoints();
                                        int percentValue = (int) animation.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);
                                    }
                                });
                                polyLineAnimator.start();

                                carMaker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                handler = new Handler();
                                index = -1;
                                next = 1;
                                handler.postDelayed(drawPathRunnable, 3000);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ///////////


    private void findDriver() {
        final DatabaseReference drivers = FirebaseDatabase.getInstance().getReference("RouteRequest");
        GeoFire geoFireDrivers = new GeoFire(drivers);

        GeoQuery geoQuery = geoFireDrivers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                //if found
                if (!isDriverFound) {
                    isDriverFound = true;
                    driverId = key;
                    //btnFindDriver.setText("Call Driver");
                    Toast.makeText(getApplicationContext(), "" + key, Toast.LENGTH_LONG).show();

                }
                final String[] locationName = new String[1];

                drivers.child(driverId).child("locationName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        locationName[0] = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final UserApp user = dataSnapshot.getValue(UserApp.class);

                                //Add driver to map
                                carMaker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .flat(true)
                                        .title(locationName[0])
                                        .snippet("Phone: " + user.getPhoneNumber())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                                carMaker.showInfoWindow();


                                // get maker just show to add click listener
                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        //Toast.makeText(getApplicationContext(),"infoss",Toast.LENGTH_LONG).show();
                                        final Dialog dialog = new Dialog(Home.this);
                                        dialog.setContentView(R.layout.info_user);

                                        btnMessage = dialog.findViewById(R.id.btnMessage);
                                        btnCall = dialog.findViewById(R.id.btnCall);
                                        tvName = dialog.findViewById(R.id.tvName);
                                        tvPhone = dialog.findViewById(R.id.tvPhone);

                                        tvName.setText(user.getName());
                                        tvPhone.setText("SDT: " + user.getPhoneNumber());

                                        btnMessage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(getApplicationContext(), "Open Messenger", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }
                                        });

                                        btnCall.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //Toast.makeText(getApplicationContext(),"Open Call",Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(Intent.ACTION_CALL);
                                                intent.setData(Uri.parse("tel:" + user.getPhoneNumber()));
                                                if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                    // TODO: Consider calling
                                                    //    ActivityCompat#requestPermissions
                                                    // here to request the missing permissions, and then overriding
                                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                    //                                          int[] grantResults)
                                                    // to handle the case where the user grants the permission. See the documentation
                                                    // for ActivityCompat#requestPermissions for more details.
                                                    return;
                                                }
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog.show();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //if still not found driver, increase distance
                if (!isDriverFound) {
                    radius++;
                    findDriver();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }


    private void Init() {

        // Init View
        locationRider_switch = findViewById(R.id.locationRider_switch);

        polyLineList = new ArrayList<>();
        btnPost = findViewById(R.id.btnPost);
        btnFindDriver = findViewById(R.id.btn_find_driver);
        startLocationAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_startLocation);
        // placeAutoComplete ??

    }

    private void addEven() {

        //// Rider
        locationRider_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean b) {
                if (b) {
                    startLocationUpdates();

                    // move cam + show maker + update firebase value
                    displayLocationAndUpdateData();
                    Snackbar.make(mapFragment.getView(), "You are Online", Snackbar.LENGTH_SHORT).show();
                } else {

                    stopLocationUpdate();

                    // remove maker
                    if (carMaker != null)
                        carMaker.remove();
                    mMap.clear();
//                    handler.removeCallbacks(drawPathRunnable);
                    Snackbar.make(mapFragment.getView(), "You are Offline", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        // search diem
        AutoCompleEvent();


        // post Pick up point and destination -- not done
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destination = destination.replace(" ", "+"); //Replace space with + for fetch data
                Toast.makeText(getApplicationContext(), "SS", Toast.LENGTH_SHORT).show();
                //Log.d("EDMTDEV", destination);
                getDirection();

            }
        });

        btnFindDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDriver();
            }
        });

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    private void displayLocationAndUpdateData() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // get cur loca
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            if (locationRider_switch.isChecked()) {
                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();

                //Update To Firebase
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                        //Move camera to this location
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
                        //Add Marker
                        if (carMaker != null) {
                            carMaker.remove();
                        }

                        // create maker
                        userMaker = mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_24px))
                                .position(new LatLng(latitude, longitude))
                                .title("You"));
                    }
                });
            }
        } else {
            Log.d("Error", "Cannot get your location");
        }

    }

    private void loadAllAvailableDriver() {
        //load all available Driver in distance 3km
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference("Divers");
        GeoFire geoFireDrivers = new GeoFire(driverLocation);

        GeoQuery geoQuery = geoFireDrivers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserApp userApp = dataSnapshot.getValue(UserApp.class);

                                //Add driver to map
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .flat(true)
                                        .title(userApp.getName())
                                        .snippet("Phone: " + userApp.getPhoneNumber())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= 3) {
                    distance++;
                    loadAllAvailableDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void startLocationUpdates() {
        // ask permission
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // get current location
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                builGoogleApiClinet();
                createLocationRequest();
                if (locationRider_switch.isChecked())
                    displayLocationAndUpdateData();
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user_info) {

            Intent i = new Intent(Home.this, AboutUser.class);
            Home.this.startActivity(i);
            // Handle the camera action
        } else if (id == R.id.nav_about) {


            Intent i = new Intent(Home.this, AboutApp.class);
            Home.this.startActivity(i);


        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Home.this, PhoneAuthActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_share){

            Intent i = new Intent(Intent.ACTION_SEND);

            i.setType("text/plain");
            String shareBody = "link";
            String shareName = "SBike";
            i.putExtra(Intent.EXTRA_TEXT,shareBody);
            i.putExtra(Intent.EXTRA_SUBJECT,shareName);

            startActivity(Intent.createChooser(i, "Sharing"));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void stopLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // GoogleApiClient ??
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void builGoogleApiClinet() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocationAndUpdateData();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocationAndUpdateData();
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


        // My Location Button
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
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
