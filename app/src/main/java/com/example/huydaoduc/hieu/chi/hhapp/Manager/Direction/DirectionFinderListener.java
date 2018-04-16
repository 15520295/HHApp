package com.example.huydaoduc.hieu.chi.hhapp.Manager.Direction;

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
