package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.location.Location;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Step;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

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
     *
     * @param polyline
     * @param location
     * @param radius
     * @return
     */
    public static boolean isNearBy(List<LatLng> polyline, LatLng location, int radius) {

//        for (LatLng polyCoords : polyline) {
//            float[] results = new float[1];
//            Location.distanceBetween(location.latitude, location.longitude,
//                    polyCoords.latitude, polyCoords.longitude, results);
//
//            if (results[0] < radius) {
//                // If distance is less than "radius" meters, this is your polyline
//                return true;
//            }
//        }

        return bdccGeoDistanceAlgorithm.bdccGeoDistanceCheckWithRadius(polyline, location, radius);
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
