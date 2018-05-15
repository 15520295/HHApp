package com.uit.huydaoduc.hieu.chi.hhapp.CostomInfoWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import static com.uit.huydaoduc.hieu.chi.hhapp.R.id.txtRoute;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public CustomInfoWindow(Context context) {
        myView = LayoutInflater.from(context)
                .inflate(R.layout.info_driver, null);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txtRoute = ((TextView) myView.findViewById(R.id.txtRoute));
        txtRoute.setText(marker.getTitle());

        TextView txtTime = ((TextView) myView.findViewById(R.id.txtTime));
        txtTime.setText(marker.getSnippet());

        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
