package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class MapCameraManager {
    private GoogleMap mMap;

    final int MAP_BOUND_ROUTE_PADDING = 110;  /* In dp */
    private static final int MAP_BOUND_POINT_ZOOM = 15;

    public MapCameraManager(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public void moveCamWithRoutes(List<Route> routes) {
        List<LatLng> points = new ArrayList<>();
        for (Route route : routes) {
            //todo handle multi route
            points.add(route.getBounds().getNorthEast());
            points.add(route.getBounds().getSouthWest());

        }
        moveCam(points);
    }

    public void moveCam(List<LatLng> points) {
        // Move the map to Surround the route
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point :
                points) {
            builder.include(point);
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_BOUND_ROUTE_PADDING);
        mMap.animateCamera(cu);
    }

    public void moveCam(LatLng... points) {
        // Move the map to Surround the route
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point :
                points) {
            if(point!= null)
                builder.include(point);
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_BOUND_ROUTE_PADDING);
        mMap.animateCamera(cu);
    }

    public void moveCam(LatLng point) {
        // Move the map to Surround the route

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(point)      // Sets the center of the map to Mountain View
                .zoom(MAP_BOUND_POINT_ZOOM)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
