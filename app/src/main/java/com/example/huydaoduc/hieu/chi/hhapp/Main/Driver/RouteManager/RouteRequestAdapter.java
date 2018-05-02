package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteManager;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.R;

import java.util.List;


public class RouteRequestAdapter extends BaseQuickAdapter<RouteRequest, com.chad.library.adapter.base.BaseViewHolder> {

    public RouteRequestAdapter( List data) {
        super(R.layout.route_request_line, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, RouteRequest routeRequest) {
        helper.setText(R.id.tv_time, routeRequest.getStartTime())
                .setText(R.id.tv_start_address, routeRequest.getStartPlace().getAddress())
                .setText(R.id.tv_end_address, routeRequest.getEndPlace().getAddress())
                .setText(R.id.btn_request_state, routeRequest.getRouteRequestState().toString())
                .addOnClickListener(R.id.btn_request_state);

        if (! TextUtils.isEmpty(routeRequest.getEndPlace().getPrimaryText())) {
            helper.setText(R.id.tv_request_title, routeRequest.getEndPlace().getPrimaryText());
        } else {
            helper.setText(R.id.tv_request_title, routeRequest.getEndPlace().getAddress());
        }

//        ((TextView) helper.getView(R.id.tv_request_title)).setSelected(true);

    }


}