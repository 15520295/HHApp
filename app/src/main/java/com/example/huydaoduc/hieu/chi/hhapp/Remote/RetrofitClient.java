package com.example.huydaoduc.hieu.chi.hhapp.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by HuyDaoDuc on 18/03/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // Singleton
    public static Retrofit getClient(String baseURL) {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)           // base URL : the server address
                    .addConverterFactory(ScalarsConverterFactory.create())      // ConverterFactory: the Tool use for convert data from the server
                                                                                // ScalarsConverterFactory : convert data to String
                    .build();


        }
        return retrofit;
    }
}
