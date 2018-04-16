//package com.example.huydaoduc.hieu.chi.hhapp.Manager;
//
//import android.Manifest;
//import android.animation.ValueAnimator;
//import android.annotation.SuppressLint;
//import android.content.pm.PackageManager;
//import android.content.res.Resources;
//import android.graphics.Color;
//import android.location.Location;
//import android.os.Handler;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.widget.VectorEnabledTintResources;
//import android.util.Log;
//import android.view.animation.LinearInterpolator;
//import android.widget.Toast;
//
//import com.example.huydaoduc.hieu.chi.hhapp.R;
//import com.example.huydaoduc.hieu.chi.hhapp.Remote.IGoogleAPI;
//import com.firebase.geofire.GeoFire;
//import com.firebase.geofire.GeoLocation;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.JointType;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.maps.model.SquareCap;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseError;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static com.facebook.FacebookSdk.getApplicationContext;
//
//public class LocationManager {
//    private LatLng startPostion, endPosition, currentPosition;
//
//
//
//    private void getDirection() {
//        currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//
//        String requestApi = null;
//        try {
//            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
//                    "mode=driving&" +
//                    "transit_routing_preference=less_driving&" +
//                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
//                    "destination=" + destination + "&" +
//                    "key=" + getResources().getString(R.string.map_api_key);
//            Log.d("Test", requestApi); // Print URL for debug
//
//            mService.getPath(requestApi)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response.body().toString());
//                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject route = jsonArray.getJSONObject(i);
//                                    JSONObject poly = route.getJSONObject("overview_polyline");
//                                    String polyline = poly.getString("points");
//                                    polyLineList = decodePoly(polyline); //decodePoly from Internet (decodepoly encode android)
//
//                                }
//
//                                //Adjusting lounds
//                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                                for (LatLng latLng : polyLineList) {
//                                    builder.include(latLng);
//                                }
//
//                                LatLngBounds bounds = builder.build();
//                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
//                                mMap.animateCamera(mCameraUpdate);
//
//
//                                // polyline Property
//                                polylineOptions = new PolylineOptions();
//                                polylineOptions.color(Color.GRAY);
//                                polylineOptions.width(5);
//                                polylineOptions.startCap(new SquareCap());
//                                polylineOptions.endCap(new SquareCap());
//                                polylineOptions.jointType(JointType.ROUND);
//                                polylineOptions.addAll(polyLineList);
//                                greyPolyline = mMap.addPolyline(polylineOptions);
//
//                                blackPolylineOptions = new PolylineOptions();
//                                blackPolylineOptions.color(Color.rgb(255, 20, 147));
//                                blackPolylineOptions.width(5);
//                                blackPolylineOptions.startCap(new SquareCap());
//                                blackPolylineOptions.endCap(new SquareCap());
//                                blackPolylineOptions.jointType(JointType.ROUND);
//                                blackPolyline = mMap.addPolyline(blackPolylineOptions);
//
//                                mMap.addMarker(new MarkerOptions()
//                                        .position(polyLineList.get(polyLineList.size() - 1))
//                                        .title("Pickup Location"));
//
//                                //Animation
//                                ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0, 100);
//                                polyLineAnimator.setDuration(2000);
//                                polyLineAnimator.setInterpolator(new LinearInterpolator());
//                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                    @Override
//                                    public void onAnimationUpdate(ValueAnimator animation) {
//                                        List<LatLng> points = greyPolyline.getPoints();
//                                        int percentValue = (int) animation.getAnimatedValue();
//                                        int size = points.size();
//                                        int newPoints = (int) (size * (percentValue / 100.0f));
//                                        List<LatLng> p = points.subList(0, newPoints);
//                                        blackPolyline.setPoints(p);
//                                    }
//                                });
//                                polyLineAnimator.start();
//
//                                carMaker = mMap.addMarker(new MarkerOptions().position(currentPosition)
//                                        .flat(true)
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
//
//                                handler = new Handler();
//                                index = -1;
//                                next = 1;
//                                handler.postDelayed(drawPathRunnable, 3000);
//
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    private void drawDirection() {
//        currentPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//        // use requestApi to determine if the api is working correctly --> if not get the URL in the log to Debug
//        String requestApi = null;
//        try {
//            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
//                    "mode=driving&" +
//                    "transit_routing_preference=less_driving&" +
//                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
//                    "destination=" + destination + "&" +
//                    "key=" + getResources().getString(R.string.map_api_key);
//            Log.d("Test", requestApi); // Print URL for debug
//
//
//            // draw direction
//            mService.getPath(requestApi)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response.body().toString());
//                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject route = jsonArray.getJSONObject(i);
//                                    JSONObject poly = route.getJSONObject("overview_polyline");
//                                    String polyline = poly.getString("points");
//                                    polyLineList = decodePoly(polyline); //decodePoly from Internet (decodepoly encode android)
//
//                                }
//
//                                //Adjusting lounds
//                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                                for (LatLng latLng : polyLineList) {
//                                    builder.include(latLng);
//                                }
//
//                                LatLngBounds bounds = builder.build();
//                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
//                                mMap.animateCamera(mCameraUpdate);
//
//                                // polyline Property
//                                polylineOptions = new PolylineOptions();
//                                polylineOptions.color(Color.GRAY);
//                                polylineOptions.width(5);
//                                polylineOptions.startCap(new SquareCap());
//                                polylineOptions.endCap(new SquareCap());
//                                polylineOptions.jointType(JointType.ROUND);
//                                polylineOptions.addAll(polyLineList);
//                                greyPolyline = mMap.addPolyline(polylineOptions);
//
//                                blackPolylineOptions = new PolylineOptions();
//                                blackPolylineOptions.color(Color.BLACK);
//                                blackPolylineOptions.width(5);
//                                blackPolylineOptions.startCap(new SquareCap());
//                                blackPolylineOptions.endCap(new SquareCap());
//                                blackPolylineOptions.jointType(JointType.ROUND);
//                                blackPolyline = mMap.addPolyline(blackPolylineOptions);
//
//                                mMap.addMarker(new MarkerOptions()
//                                        .position(polyLineList.get(polyLineList.size() - 1))
//                                        .title("Pickup Location"));
//
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//
//                        }
//                    });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
