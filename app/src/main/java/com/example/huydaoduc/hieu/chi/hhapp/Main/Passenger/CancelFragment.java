package com.example.huydaoduc.hieu.chi.hhapp.Main.Passenger;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huydaoduc.hieu.chi.hhapp.R;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

public class CancelFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(
                R.layout.fragment_cancel, container, false);

//        TextView tv_start_address = view.findViewById(R.id.tv_start_address);
//        tv_start_address.setText();
//
//        TextView tv_end_address = view.findViewById(R.id.tv_end_address);
//        tv_end_address.setText();

        MaterialFancyButton cancel = view.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getParentFragment().getFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
