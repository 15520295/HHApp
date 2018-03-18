package com.example.huydaoduc.hieu.chi.hhapp.Common;

import com.example.huydaoduc.hieu.chi.hhapp.Remote.IGoogleAPI;
import com.example.huydaoduc.hieu.chi.hhapp.Remote.RetrofitClient;

/**
 * Created by HuyDaoDuc on 18/03/2018.
 */

public class Common {
    public static final String baseURL = "https://maps.googleapis.com";

    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
