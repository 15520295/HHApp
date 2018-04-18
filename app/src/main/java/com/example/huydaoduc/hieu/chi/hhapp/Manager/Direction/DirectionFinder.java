package com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.huydaoduc.hieu.chi.hhapp.Common.Common;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.example.huydaoduc.hieu.chi.hhapp.Remote.IGoogleAPI;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 * Customize by Phan Huu Chi on 4/2018 : use Retrofit 2 instead
 *                                       alternative parseJSon method
 *                                       whole new Object Class from JSon
 *                                       multi finding method by splitting Create URL method
 */
public class DirectionFinder {
    private static final String TAG = "DirectionFinder";
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static String GOOGLE_API_KEY;
    private DirectionFinderListener listener;

    private IGoogleAPI mapService;

    String urlRequest;

    public DirectionFinder(DirectionFinderListener listener, Context context) {
        urlRequest = null;

        this.listener = listener;

        this.GOOGLE_API_KEY = context.getResources().getString(R.string.map_api_key);
        mapService = Common.getGoogleAPI();
    }

    /**
     * 1 place - 1 place
     */
    public void createUrl(String origin, String destination) throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        urlRequest =  DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination
                + "&mode=driving"
                + "&mode=driving"
                + "&key=" + GOOGLE_API_KEY;
    }

    /**
     * 1 point - 1 place
     */
    public void createUrl(LatLng latLng , String destination) throws UnsupportedEncodingException {
        String urlOrigin = latLng.latitude + "," + latLng.longitude;
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        urlRequest =  DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination
                + "&mode=driving"
                + "&mode=driving"
                + "&key=" + GOOGLE_API_KEY;
    }

    /**
     * 1 point - 1 point
     */
    public void createUrl(LatLng start_latLng , LatLng end_latLng) throws UnsupportedEncodingException {
        String url_start = start_latLng.latitude + "," + start_latLng.longitude;
        String url_end = end_latLng.latitude + "," + end_latLng.longitude;


        urlRequest =  DIRECTION_URL_API + "origin=" + url_start + "&destination=" + url_end
                + "&mode=driving"
                + "&mode=driving"
                + "&key=" + GOOGLE_API_KEY;
    }


    // NOTE: must call "createUrl" method before execute
    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();

        // setup the request get data ( set up the call )
        if(urlRequest == null)
        {
            Log.e(TAG,  "Error: urlRequest == null");
            return;
        }
        Call<String> call = mapService.getPath(urlRequest);

        // enqueue: execute asynchronously . User a Callback to get respond from the server
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    String responseString = response.body();    // get response object
                                                                // the response String is the whole JSON file have info of the direction


                    parseJSon(responseString);                  // parse JSON
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //todo: check
            }
        });

    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routeList = new ArrayList<Route>();
        final JSONObject jSONObject = new JSONObject(data);
        JSONArray routeJSONArray = jSONObject.getJSONArray("routes");
        Route route;
        JSONObject routesJSONObject;
        for (int m = 0; m < routeJSONArray.length(); m++) {
            route = new Route();
            routesJSONObject = routeJSONArray.getJSONObject(m);
            JSONArray legsJSONArray;
            route.setSummary(routesJSONObject.getString("summary"));

            JSONObject boundJSONObject = routesJSONObject.getJSONObject("bounds");
            Bound routeBound = new Bound();
            JSONObject routBoundNorthEast = boundJSONObject.getJSONObject("northeast");
            routeBound.setNorthEast(new LatLng(routBoundNorthEast.getDouble("lat"), routBoundNorthEast.getDouble("lng")));
            JSONObject routBoundSouthWest = boundJSONObject.getJSONObject("southwest");
            routeBound.setSouthWest(new LatLng(routBoundSouthWest.getDouble("lat"), routBoundSouthWest.getDouble("lng")));
            route.setBounds(routeBound);

            // Get LEGS Info
            legsJSONArray = routesJSONObject.getJSONArray("legs");
            JSONObject legJSONObject;
            Leg leg;
            JSONArray stepsJSONArray;
            for (int b = 0; b < legsJSONArray.length(); b++) {
                leg = new Leg();
                legJSONObject = legsJSONArray.getJSONObject(b);
                leg.setDistance(new Distance(legJSONObject.optJSONObject("distance").optString("text"), legJSONObject.optJSONObject("distance").optLong("value")));
                leg.setDuration(new Duration(legJSONObject.optJSONObject("duration").optString("text"), legJSONObject.optJSONObject("duration").optLong("value")));

                JSONObject legStartLocationJSONObject = legJSONObject.getJSONObject("start_location");
                leg.setStartLocation(new LatLng(legStartLocationJSONObject.getDouble("lat"), legStartLocationJSONObject.getDouble("lng")));
                JSONObject legEndLocationJSONObject = legJSONObject.getJSONObject("end_location");
                leg.setEndLocation(new LatLng(legEndLocationJSONObject.getDouble("lat"), legEndLocationJSONObject.getDouble("lng")));

                leg.setStartAddress(legJSONObject.getString("start_address"));
                leg.setEndAddress(legJSONObject.getString("end_address"));

                // Get STEPS Info
                stepsJSONArray = legJSONObject.getJSONArray("steps");
                JSONObject stepJSONObject, stepDurationJSONObject, legPolyLineJSONObject, stepStartLocationJSONObject, stepEndLocationJSONObject;
                Step step;
                String encodedString;
                LatLng stepStartLocationLatLng, stepEndLocationLatLng;
                for (int i = 0; i < stepsJSONArray.length(); i++) {

                    stepJSONObject = stepsJSONArray.getJSONObject(i);
                    step = new Step();
                    JSONObject stepDistanceJSONObject = stepJSONObject.getJSONObject("distance");
                    step.setDistance(new Distance(stepDistanceJSONObject.getString("text"), stepDistanceJSONObject.getLong("value")));
                    stepDurationJSONObject = stepJSONObject.getJSONObject("duration");
                    step.setDuration(new Duration(stepDurationJSONObject.getString("text"), stepDurationJSONObject.getLong("value")));
                    stepEndLocationJSONObject = stepJSONObject.getJSONObject("end_location");
                    stepEndLocationLatLng = new LatLng(stepEndLocationJSONObject.getDouble("lat"), stepEndLocationJSONObject.getDouble("lng"));
                    step.setEndLocation(stepEndLocationLatLng);
                    step.setHtmlInstructions(stepJSONObject.getString("html_instructions"));

                    legPolyLineJSONObject = stepJSONObject.getJSONObject("polyline");
                    encodedString = legPolyLineJSONObject.getString("points");
                    step.setPoints(decodePolyLine(encodedString));

                    stepStartLocationJSONObject = stepJSONObject.getJSONObject("start_location");
                    stepStartLocationLatLng = new LatLng(stepStartLocationJSONObject.getDouble("lat"), stepStartLocationJSONObject.getDouble("lng"));
                    step.setStartLocation(stepStartLocationLatLng);
                    leg.addStep(step);
                }
                route.addLeg(leg);
            }
            routeList.add(route);
        }

        listener.onDirectionFinderSuccess(routeList);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
