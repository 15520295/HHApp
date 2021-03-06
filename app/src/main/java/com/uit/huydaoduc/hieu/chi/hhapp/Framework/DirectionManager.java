package com.uit.huydaoduc.hieu.chi.hhapp.Framework;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinder;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.DirectionFinderListener;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.Leg;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DirectionManager {
    private static final String TAG = "DirectionManager";
    private Context context;
    private GoogleMap mMap;         // this only use for draw route

    private List<Polyline> polylinePaths = new ArrayList<>();

    public List<Polyline> getPolylinePaths() {
        return polylinePaths;
    }

    public DirectionManager(Context context, GoogleMap mMap) {
        this.context = context;
        this.mMap = mMap;
    }

    /**
     * if use this constructor u can't use draw route
     */
    public DirectionManager(Context context) {
        this.context = context;
    }

    /**
     * 1 Location (LatLng) - 1 Place (Address)
     * Find Direction after done, it will raise the @listener
     */
    public void findPath(Location startLocation, String endPlaceAddress, DirectionFinderListener listener) {
        LatLng latLng_startLocation = LocationUtils.locaToLatLng(startLocation);

        if (endPlaceAddress.isEmpty()) {
            Toast.makeText(context, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            DirectionFinder directionFinder = new DirectionFinder(listener, context);

            directionFinder.createUrl(latLng_startLocation, endPlaceAddress);

            directionFinder.execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1 Location (LatLng) - 1 Location (LatLng)
     */
    public void findPath(LatLng latLng_curLocation, LatLng latLng_endLocation, DirectionFinderListener listener) {
        try {

            DirectionFinder directionFinder = new DirectionFinder(listener, context);

            directionFinder.createUrl(latLng_curLocation, latLng_endLocation);

            directionFinder.execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void drawRoutes(List<Route> routes, boolean removeAll) {
        if (mMap == null) {
            Log.e(TAG,"mMap null");
            return;
        }

        if (removeAll) {
            removeAllRoutes();
        }

        polylinePaths = new ArrayList<>();

        for (Route route : routes) {

            for (Leg leg : route.getLegs()) {
                Log.i(TAG, "Duration + Distance: " + leg.getDuration().getValue() + ", " + leg.getDistance().getValue());

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

    public void removeAllRoutes() {
        if (polylinePaths != null)
            for (Polyline polyline :
                    polylinePaths) {
                polyline.remove();
            }
    }
}
