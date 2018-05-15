package com.uit.huydaoduc.hieu.chi.hhapp.Framework.Direction;

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
