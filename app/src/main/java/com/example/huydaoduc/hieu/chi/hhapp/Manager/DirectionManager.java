package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinder;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.Remote.IGoogleAPI;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DirectionManager {
    private Context context;
    private GoogleMap mMap;
    private DirectionFinderListener listener;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    public DirectionManager(Context context, GoogleMap mMap, DirectionFinderListener listener) {
        this.context = context;
        this.listener = listener;
        this.mMap = mMap;
    }

    //todo: edit parameter
//    public void findPath(String destination, String destination) {
//        String origin = "Dai hoc khoa hoc tu nhien";
//        String destination = "Dai hoc bach khoa hcm";
//        if (origin.isEmpty()) {
//            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (destination.isEmpty()) {
//            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            new DirectionFinder(this, origin, destination, getApplicationContext()).execute();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

    public void findPath(Location curLocation, String destination) {
        LatLng latLng_curLoaction = new LatLng(curLocation.getLatitude(), curLocation.getLongitude());

        if (destination.isEmpty()) {
            Toast.makeText(context, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DirectionFinder directionFinder = new DirectionFinder(listener, getApplicationContext());
            directionFinder.createUrl(latLng_curLoaction, destination);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void drawRoutes(List<Route> routes) {
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

                // Note: point  --- is a pairs not a array
                //       points --- is an array

                // draw
                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }
        }
    }
}
