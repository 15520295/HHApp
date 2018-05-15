package com.uit.huydaoduc.hieu.chi.hhapp.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by HuyDaoDuc on 18/03/2018.
 */

public interface IGoogleAPI {
    @GET
    Call<String> getPath(@Url String url);

    /*
    Retrofit 2: https://www.youtube.com/watch?v=R4XU8yPzSx0

    + Call<String> getPath(@Url String url)
    <=> String getPath(String url) : a method return a String, with a parameter String is Url
    We need to wrap this method in Call for asynchronous
    (if we don't it will freeze UI thread until it return the date from the server)

    + @GET : this annotation show that we need to get data from the server

    + @Url : the url parameter consider it like a Url ( Url is the path after @GET, Full_Url = Base_Url + Url )


     */
}
