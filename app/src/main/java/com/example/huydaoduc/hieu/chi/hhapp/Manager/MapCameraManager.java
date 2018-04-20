package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Leg;
import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class MapCameraManager {
    private GoogleMap mMap;

    final int MAP_BOUND_PADDING = 100;  /* In dp */

    public MapCameraManager(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public void moveCam(List<Route> routes) {
        for (Route route : routes) {
            for (Leg leg : route.getLegs()) {
                // Move the map to Surround the route
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(route.getBounds().getNorthEast());
                builder.include(route.getBounds().getSouthWest());
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_BOUND_PADDING);
                mMap.animateCamera(cu);
            }
        }
    }

    public void moveCam(LatLng point) {

    }
}
