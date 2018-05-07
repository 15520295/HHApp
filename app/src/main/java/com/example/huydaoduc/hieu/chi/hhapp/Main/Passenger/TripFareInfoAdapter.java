package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.huydaoduc.hieu.chi.hhapp.Define;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripFareInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;

import java.util.List;

public class TripFareInfoAdapter extends BaseQuickAdapter<TripFareInfo, BaseViewHolder> {

    private CarType curCarType;

    public TripFareInfoAdapter(List data, CarType curCarType) {
        super(R.layout.row_trip_fare, data);
        this.curCarType = curCarType;
    }


    @Override
    protected void convert(BaseViewHolder helper, TripFareInfo tripFareInfo) {
        helper.setText(R.id.tv_car_type, tripFareInfo.func_getCarTypeText())
                .setText(R.id.tv_duration, tripFareInfo.func_getDurationText())
                .setImageResource(R.id.iv_car_type, Define.CAR_TYPE_ICON_MAP.get(tripFareInfo.getCarType()))
                .addOnClickListener(R.id.row_trip_fare);

        // if have estimate fare then show it
        if (tripFareInfo.getEstimateFare() != 0f) {
            helper.setText(R.id.tv_fare, tripFareInfo.func_getEstimateFareText())
                    .setVisible(R.id.tv_fare,true);
        }
        // if the row is selected change it background color
        if (tripFareInfo.getCarType() == curCarType) {
            helper.setBackgroundColor(R.id.row_trip_fare, mContext.getResources().getColor(R.color.selected_car_type_row));
        }

    }
}