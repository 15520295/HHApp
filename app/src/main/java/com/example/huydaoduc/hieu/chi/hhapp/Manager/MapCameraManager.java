package com.example.huydaoduc.hieu.chi.hhapp.Manager;

import com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class MapCameraManager {
    private GoogleMap mMap;

    final int MAP_BOUND_ROUTE_PADDING = 100;  /* In dp */
    private static final int MAP_BOUND_POINT_PADDING = 200;

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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if(point!= null)
            builder.include(point);

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_BOUND_POINT_PADDING);
        mMap.animateCamera(cu);
    }

//    public void moveCam(LatLng point) {
//        final float[] v = new float[1];
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
//        valueAnimator.setDuration(3000);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                v[0] = animation.getAnimatedFraction();
//                lng = v[0] * endPosition.longitude + (1 - v[0]) * startPostion.longitude;
//                lat = v[0] * endPosition.latitude + (1 - v[0]) * startPostion.latitude;
//                LatLng newPos = new LatLng(lat, lng);
//                carMaker.setPosition(newPos);
//                carMaker.setAnchor(0.5f, 0.5f);
//                carMaker.setRotation(getBearing(startPostion, newPos));
//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
//                        new CameraPosition.Builder()
//                                .target(newPos)
//                                .zoom(15.5f)
//                                .build())
//                );
//            }
//        });
//        valueAnimator.start();
//        handler.postDelayed(this, 3000);
//    }
}
