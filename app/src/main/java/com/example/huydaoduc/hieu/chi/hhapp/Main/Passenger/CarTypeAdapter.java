package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.huydaoduc.hieu.chi.hhapp.DefineString;
import com.example.huydaoduc.hieu.chi.hhapp.R;

import java.util.List;

public class CarTypeAdapter extends BaseQuickAdapter<CarTypeInfo, BaseViewHolder> {

    public CarTypeAdapter(List data) {
        super(R.layout.row_car_type, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, CarTypeInfo carTypeInfo) {
        helper.setText(R.id.tv_car_type, DefineString.CAR_TYPE_MAP.get(carTypeInfo.getCarType()))
                .setText(R.id.tv_fare, carTypeInfo.getEstimateFareText())
                .setText(R.id.tv_duration, carTypeInfo.getDurationText());

        // if select change background color

    }
}