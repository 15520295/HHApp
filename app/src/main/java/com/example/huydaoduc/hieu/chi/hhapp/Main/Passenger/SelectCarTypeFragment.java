package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.huydaoduc.hieu.chi.hhapp.Model.Car.CarType;
import com.example.huydaoduc.hieu.chi.hhapp.Model.Trip.TripFareInfo;
import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.ArrayList;
import java.util.List;

public class SelectCarTypeFragment extends DialogFragment {

    List<TripFareInfo> tripFareInfoList;

    RecyclerView recycler_view_car_type;
    MaterialFancyButton btn_cancel;
    CarType curCarType;

    SelectCarTypeFragmentListener mListener;

    public SelectCarTypeFragment() {

    }

    public interface SelectCarTypeFragmentListener {
        void OnCarTypeSelect(CarType carType);
        void OnCancel();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectCarTypeFragmentListener) {
            mListener = (SelectCarTypeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRouteRequestManagerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.OnCancel();
        mListener = null;

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AcceptingTripFragment.
     */
    public static SelectCarTypeFragment newInstance(ArrayList<TripFareInfo> tripFareInfoList, CarType curCarType) {
        SelectCarTypeFragment fragment = new SelectCarTypeFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList("tripFareInfoList", tripFareInfoList);
        args.putSerializable("curCarType", curCarType);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tripFareInfoList = getArguments().getParcelableArrayList("tripFareInfoList");
            curCarType = (CarType) getArguments().get("curCarType");
        }
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_car_type, container, false);

        // change Dialog background
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0xA0000000));

        // change size to match parent
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getDialog().getWindow().setContentView(root);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        // Init view
        // recycler
        recycler_view_car_type = view.findViewById(R.id.recycler_view_car_type);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view_car_type.setLayoutManager(llm);

        btn_cancel = view.findViewById(R.id.btn_cancel);

        // Init value
        TripFareInfoAdapter adapter = new TripFareInfoAdapter(tripFareInfoList, curCarType);
        adapter.setOnItemChildClickListener((adapter1, v, position) -> {
                    CarType carType = tripFareInfoList.get(position).getCarType();
                    if (carType == null) {

                    } else {
                        mListener.OnCarTypeSelect(carType);
                        SelectCarTypeFragment.this.dismiss();
                    }
                });
        recycler_view_car_type.setAdapter(adapter);

        // Event
        btn_cancel.setOnClickListener(v -> {
            mListener.OnCancel();
            dismiss();
        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
    }

}
