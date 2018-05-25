package com.uit.huydaoduc.hieu.chi.hhapp.Main.Passenger.PassengerRequestManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.uit.huydaoduc.hieu.chi.hhapp.DefineString;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.LocationUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Framework.TimeUtils;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequestState;
import com.uit.huydaoduc.hieu.chi.hhapp.Model.Trip.TripFareInfo;
import com.uit.huydaoduc.hieu.chi.hhapp.R;

import java.util.List;


public class PassengerRequestAdapter extends BaseQuickAdapter<PassengerRequest, BaseViewHolder> {

    public PassengerRequestAdapter(List data) {
        super(R.layout.row_passenger_request, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PassengerRequest passengerRequest) {
        TripFareInfo tripFareInfo = passengerRequest.getTripFareInfo();

        helper.setText(R.id.tv_time, tripFareInfo.getStartTime())
                .setText(R.id.tv_estimate_fare, tripFareInfo.func_getEstimateFareText())
                .setText(R.id.tv_pick_up_address, passengerRequest.getPickUpSavePlace().getAddress())
                .setText(R.id.tv_drop_off_address, passengerRequest.getDropOffSavePlace().getAddress());

        String title;
        String startPlace, endPlace;
        startPlace =  LocationUtils.getPrimaryTextFromAddress(passengerRequest.getPickUpSavePlace().getAddress());
        endPlace =  LocationUtils.getPrimaryTextFromAddress(passengerRequest.getDropOffSavePlace().getAddress());
        title = startPlace + " - " + endPlace;
        helper.setText(R.id.tv_request_title, title);

        // State
        PassengerRequestState state = passengerRequest.getPassengerRequestState();
        helper.setText(R.id.btn_request_state, DefineString.PASSENGER_REQUEST_STATE_MAP.get(state));
        if (state == PassengerRequestState.FOUND_DRIVER) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_check, true);
            helper.setVisible(R.id.iv_pause, false);
        } else if (state == PassengerRequestState.FINDING_DRIVER) {
            helper.setVisible(R.id.prb_finding_passenger, true);
            helper.setVisible(R.id.iv_check, false);
            helper.setVisible(R.id.iv_pause, false);
        } else if (state == PassengerRequestState.PAUSE) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_check, false);
            helper.setVisible(R.id.iv_pause, true);
        }

        long passTime = TimeUtils.getPassTime(passengerRequest.getTripFareInfo().getStartTime())/60;
        int waitMin = passengerRequest.getWaitMinute();
        if (passTime > waitMin) {
            helper.setVisible(R.id.prb_finding_passenger, false);
            helper.setVisible(R.id.iv_pause, false);
            helper.setText(R.id.btn_request_state, DefineString.PASSENGER_REQUEST_STATE_MAP.get(PassengerRequestState.TIME_OUT));
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