package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerRequestManager;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.huydaoduc.hieu.chi.hhapp.DefineString;
import com.example.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequestState;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripFareInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;

import java.util.List;


public class PassengerRequestAdapter extends BaseQuickAdapter<PassengerRequest, BaseViewHolder> {

    public PassengerRequestAdapter(List data) {
        super(R.layout.row_passenger_request, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PassengerRequest passengerRequest) {
        TripFareInfo tripFareInfo = passengerRequest.getTripFareInfo();

        helper.setText(R.id.tv_time, tripFareInfo.getStartTime())
                .setText(R.id.tv_pick_up_address, passengerRequest.getPickUpSavePlace().getAddress())
                .setText(R.id.tv_drop_off_address, passengerRequest.getDropOffSavePlace().getAddress());

        //todo: change to 2 path
        if (! TextUtils.isEmpty(passengerRequest.getDropOffSavePlace().getPrimaryText())) {
            helper.setText(R.id.tv_request_title, passengerRequest.getDropOffSavePlace().getPrimaryText());
        } else {
            helper.setText(R.id.tv_request_title, passengerRequest.getDropOffSavePlace().getAddress());
        }

        // State
        PassengerRequestState state = passengerRequest.getPassengerRequestState();
        helper.setText(R.id.btn_request_state, DefineString.REQUEST_STATE_MAP.get(state));
        if (state == PassengerRequestState.FOUND_DRIVER) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_check, true);
            helper.setVisible(R.id.iv_pause, false);
        } else if (state == PassengerRequestState.FOUND_DRIVER) {
            helper.setVisible(R.id.prb_finding_passenger, true);
            helper.setVisible(R.id.iv_check, false);
            helper.setVisible(R.id.iv_pause, false);
        } else if (state == PassengerRequestState.PAUSE) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_check, false);
            helper.setVisible(R.id.iv_pause, true);
        } else if (passengerRequest.func_isInTheFuture()) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_check, false);
            helper.setVisible(R.id.iv_pause, false);
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