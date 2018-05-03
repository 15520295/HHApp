package com.example.huydaoduc.hieu.chi.hhapp.Main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.huydaoduc.hieu.chi.hhapp.R;

public class Fragment1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(
                R.layout.fragment_1, container, false);

        Button cancel = view.findViewById(R.id.btnCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getParentFragment().getFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
