package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinder;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.DirectionFinderListener;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DirectionManager {
    private Context context;
    private GoogleMap mMap;

    private List<Polyline> polylinePaths = new ArrayList<>();

    public List<Polyline> getPolylinePaths() {
        return polylinePaths;
    }

    public DirectionManager(Context context, GoogleMap mMap) {
        this.context = context;
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

    /**
     * 1 Location - 1 Place
     * Find Direction after done, it will raise the @listener
     */
    public void findPath(Location startLocation, String destination, DirectionFinderListener listener) {
        LatLng latLng_startLocation = LocationUtils.locaToLatLng(startLocation);

        if (destination.isEmpty()) {
            Toast.makeText(context, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            DirectionFinder directionFinder = new DirectionFinder(listener, context);

            directionFinder.createUrl(latLng_startLocation, destination);

            directionFinder.execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1 Location - 1 Location
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

        if (removeAll) {
            removeAllRoutes();
        }

        polylinePaths = new ArrayList<>();

        for (Route route : routes) {

            for (Leg leg : route.getLegs()) {
                //todo: show Duration + Distance
                Toast.makeText(context, "Duration + Distance: " + leg.getDuration().getValue() + ", " + leg.getDistance().getValue(), Toast.LENGTH_LONG)
                        .show();

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