package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.location.Location;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Step;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Phan Huu Chi on 4/2018
 */
public class LocationUtils {
    /**
     * @param string ex:"-34.8799074,174.7565664"
     */
    public static LatLng stringToLatLng(String string) {
        String[] latlong =  string.split(",");
        return new LatLng(Double.parseDouble(latlong[0]),Double.parseDouble(latlong[1]) );
    }

    public static LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude() );
    }

    public static String latLngToString(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }

    /**
     * Check if a @Location is nearby a @Polyline with a @radius
     */
    public static boolean isNearBy(List<LatLng> polyline, LatLng location, int radius) {

        return PolyUtil.isLocationOnPath(location, polyline, false, radius);
    }

    /**
     * Check if the @startLocation is nearby @polyline and get the path from the @startLocation to the end of
     * the @polyline and check if @endLocation is nearby that path
     */
    public static boolean isMatching(List<LatLng> polyline, LatLng startLocation, LatLng endLocation, int radius) {

        List<LatLng> pathToEnd = bdccGeoAlgorithm.bdccGeoGetPathOfPolyLineEnd(polyline, startLocation, radius);
        if (pathToEnd != null) {
            if (LocationUtils.isNearBy(pathToEnd, endLocation, radius)) {
                return true;
            }
        }
        return false;
    }


    public static List<LatLng> getPointsFromRoute(Route route) {
        List<LatLng> points = new ArrayList<>();

        Leg leg = route.getLegs().get(0);
        for (Step step :
                leg.getSteps()) {
            points.addAll(step.getPoints());
        }
        return points;
    }
}
