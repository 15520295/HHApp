package com.uit.huydaoduc.hieu.chi.hhapp.Framework;

import android.content.Context;

import com.uit.huydaoduc.hieu.chi.hhapp.Define;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction.Route;
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
    private Context context;

    public MapCameraManager(Context applicationContext, GoogleMap mMap) {
        this.context = applicationContext;
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
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, Define.MAP_BOUND_ROUTE_PADDING);
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
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width, height, Define.MAP_BOUND_ROUTE_PADDING);
        mMap.animateCamera(cu);
    }

    public void moveCam(int left,int top, int right, int bottom, LatLng... points) {
        // Move the map to Surround the route
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point :
                points) {
            if(point!= null)
                builder.include(point);
        }

        LatLngBounds bounds = builder.build();
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, Define.MAP_BOUND_ROUTE_PADDING);
        mMap.setPadding(left,top,right,bottom);
        mMap.animateCamera(cu);
    }

    public void moveCam(LatLng point) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(point)      // Sets the center of the map to Mountain View
                .zoom(Define.MAP_BOUND_POINT_ZOOM)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
