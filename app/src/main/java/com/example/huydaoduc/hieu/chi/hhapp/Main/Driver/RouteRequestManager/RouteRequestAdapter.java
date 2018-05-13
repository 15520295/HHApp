package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.huydaoduc.hieu.chi.hhapp.DefineString;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.R;

import java.util.List;


public class RouteRequestAdapter extends BaseQuickAdapter<RouteRequest, com.chad.library.adapter.base.BaseViewHolder> {

    public RouteRequestAdapter( List data) {
        super(R.layout.row_route_request, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, RouteRequest routeRequest) {
        helper.setText(R.id.tv_time, TimeUtils.dateToUserDateTimeStr(routeRequest.getStartTime()))
                .setText(R.id.tv_pick_up_address, routeRequest.getStartPlace().getAddress())
                .setText(R.id.tv_drop_off_address, routeRequest.getEndPlace().getAddress());

        String title;
        String startPlace, endPlace;
        startPlace =  LocationUtils.getPrimaryTextFromAddress(routeRequest.getStartPlace().getAddress());
        endPlace =  LocationUtils.getPrimaryTextFromAddress(routeRequest.getEndPlace().getAddress());
        title = startPlace + " - " + endPlace;
        helper.setText(R.id.tv_request_title, title);

        // State
        helper.setText(R.id.btn_request_state, DefineString.ROUTE_REQUEST_STATE_MAP.get(routeRequest.getRouteRequestState()));
        if (routeRequest.getRouteRequestState() == RouteRequestState.FOUND_PASSENGER) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_check, true);
            helper.setVisible(R.id.iv_pause, false);
        } else if (routeRequest.getRouteRequestState() == RouteRequestState.FINDING_PASSENGER) {
            helper.setVisible(R.id.prb_finding_passenger, true);
            helper.setVisible(R.id.iv_check, false);
            helper.setVisible(R.id.iv_pause, false);
        } else if (routeRequest.getRouteRequestState() == RouteRequestState.PAUSE) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_check, false);
            helper.setVisible(R.id.iv_pause, true);
        }

        if (! routeRequest.func_isInTheFuture()) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_pause, false);
            helper.setText(R.id.btn_request_state, DefineString.ROUTE_REQUEST_STATE_MAP.get(RouteRequestState.DONE));
        }


        // Event
        helper.addOnClickListener(R.id.btn_request_state)
            .addOnClickListener(R.id.iv_menu);


        // moving text
        helper.getView(R.id.tv_request_title).setOnClickListener(v -> {
            if(v.isSelected())
                v.setSelected(false);
            else
                v.setSelected(true);
        });
        helper.getView(R.id.tv_pick_up_address).setOnClickListener(v -> {
            if(v.isSelected())
                v.setSelected(false);
            else
                v.setSelected(true);
        });
        helper.getView(R.id.tv_drop_off_address).setOnClickListener(v -> {
            if(v.isSelected())
                v.setSelected(false);
            else
                v.setSelected(true);
        });
    }
}