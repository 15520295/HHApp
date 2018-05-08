package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteManager;

import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.huydaoduc.hieu.chi.hhapp.DefineString;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
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

        if (! TextUtils.isEmpty(routeRequest.getEndPlace().getPrimaryText())) {
            helper.setText(R.id.tv_request_title, routeRequest.getEndPlace().getPrimaryText());
        } else {
            helper.setText(R.id.tv_request_title, routeRequest.getEndPlace().getAddress());
        }

        ((TextView) helper.getView(R.id.tv_request_title)).setSelected(true);

        // State
        helper.setText(R.id.btn_request_state, DefineString.REQUEST_STATE_MAP.get(routeRequest.getRouteRequestState()));
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
        } else if (routeRequest.getRouteRequestState() == RouteRequestState.TIME_OUT) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_check, false);
            helper.setVisible(R.id.iv_pause, false);
        }


        // Event
        helper.addOnClickListener(R.id.btn_request_state)
            .addOnClickListener(R.id.iv_menu);
    }
}