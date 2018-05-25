package com.uit.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager;

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


public class WaitingPassengerRequestAdapter extends BaseQuickAdapter<PassengerRequest, BaseViewHolder> {

    public WaitingPassengerRequestAdapter(List data) {
        super(R.layout.row_waiting_pas_request, data);
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


        // Event
        helper.addOnClickListener(R.id.btn_accept_trip);

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