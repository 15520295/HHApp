package com.uit.huydaoduc.hieu.chi.hhapp.Main.Driver;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uit.huydaoduc.hieu.chi.hhapp.R;

public class AcceptingTripFragment extends DialogFragment {

    float fare;
    float distance;
    String pickUpAddress;
    String dropOffAddress;
    String note;

    TextView tv_fare;
    TextView tv_distance;
    TextView tv_pick_up_address;
    TextView tv_drop_off_address;
    TextView tv_note;
    Button btn_accept;

    private OnAcceptingFragmentListener mListener;

    public AcceptingTripFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AcceptingTripFragment.
     */
    public static AcceptingTripFragment newInstance(float distance, String pickUpAddress,
                                                    String dropOffAddress, String note,
                                                    float fare) {
        AcceptingTripFragment fragment = new AcceptingTripFragment();

        Bundle args = new Bundle();
        args.putFloat("distance", distance);
        args.putString("pickUpAddress", pickUpAddress);
        args.putString("dropOffAddress", dropOffAddress);
        args.putString("note", note);
        args.putFloat("fare", fare);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            distance = getArguments().getFloat("distance");
            pickUpAddress = getArguments().getString("pickUpAddress");
            dropOffAddress = getArguments().getString("dropOffAddress");
            note = getArguments().getString("note");
            fare = getArguments().getFloat("fare");
        }


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accepting_trip, container, false);

        // change Dialog background
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0xBB333333));

        // change size to match parent
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getDialog().getWindow().setContentView(root);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        // Init view
        tv_fare = view.findViewById(R.id.tv_fare);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_pick_up_address = view.findViewById(R.id.tv_pick_up_address);
        tv_drop_off_address = view.findViewById(R.id.tv_drop_off_address);
        tv_note = view.findViewById(R.id.tv_note);
        btn_accept = view.findViewById(R.id.btn_accept);

        // Init value
        tv_fare.setText(Float.toString(fare) + "VND");
        tv_distance.setText(Float.toString(distance / 1000) + "KM");
        tv_pick_up_address.setText(pickUpAddress);
        tv_drop_off_address.setText(dropOffAddress);
        tv_note.setText(note);

        btn_accept.setOnClickListener(e ->{
            mListener.OnTripAccepted();
            dismiss();
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
    }



    public interface OnAcceptingFragmentListener {
        // TODO: Update argument type and name
        void OnTripAcceptTimeOut();
        void OnTripAccepted();
    }

}
