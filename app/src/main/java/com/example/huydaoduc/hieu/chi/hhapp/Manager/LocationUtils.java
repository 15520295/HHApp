package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import android.location.Location;
import android.util.Log;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Step;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Phan Huu Chi on 4/2018
 */
public class LocationUtils {
    private static String TAG = "LocationUtils";

    // ------- Converter --------------
    /**
     * @param string ex:"-34.8799074,174.7565664"
     */
    public static LatLng strToLatLng(String string) {
        String[] latlong =  string.split(",");
        return new LatLng(Double.parseDouble(latlong[0]),Double.parseDouble(latlong[1]) );
    }

    public static String latLngToStr(LatLng latLng) {
        if(latLng == null)
        {
            Log.e(TAG, "Location is null");
            return "0,0";
        }
        return latLng.latitude + "," + latLng.longitude;
    }

    public static LatLng locaToLatLng(Location location) {
        if(location == null)
        {
            Log.e(TAG, "Location is null");
            return new LatLng(0, 0 );
        }
        return new LatLng(location.getLatitude(), location.getLongitude() );
    }

    public static String locaToStr(Location location) {
        if(location == null)
        {
            Log.e(TAG, "Location is null");
            return "0,0";
        }
        return location.getLatitude() + "," + location.getLongitude();
    }

    /**
     * @param center
     * @param radiusInMeters
     * @return a Rectangle bound around the @center, width and height equal to @radius
     */
    public static LatLngBounds pointToBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    // --------------------------------

    /**
     * @return meter
     */
    public static float calcDistance(LatLng startLatLng, LatLng endLatLng) {
        float[] result = new float[1] ;
        Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, endLatLng.latitude, endLatLng.longitude, result);
        return result[0];
    }
    public static float calcDistance(LatLng startLatLng, Location endLoca) {
        return calcDistance(startLatLng,locaToLatLng(endLoca));
    }
    public static float calcDistance(String startLocaStr, String endLocaStr) {
        return calcDistance(strToLatLng(startLocaStr),strToLatLng(endLocaStr));
    }
    public static float calcDistance(String startLocaStr, Location endLoca) {
        return calcDistance(strToLatLng(startLocaStr),locaToLatLng(endLoca));
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
