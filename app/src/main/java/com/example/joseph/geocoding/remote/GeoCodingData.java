package com.example.joseph.geocoding.remote;

import com.example.joseph.geocoding.model.GeoCodingResult;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoCodingData {

    public static final String BASE_URL = "https://maps.googleapis.com/";
    public static final String API_KEY = "AIzaSyBjDAZOJ6SpCGIQBv2Zgt0uU5VFZ2UtMI4";

    public static Retrofit create(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;

    }

    public static Observable<GeoCodingResult> searchGeoCode(String address){
        Retrofit retrofit = create();
        GeoCodingService geoCodingService = retrofit.create(GeoCodingService.class);
        return geoCodingService.searchGeoCode(address, API_KEY);
    }

    public static Observable<GeoCodingResult> reverseGeoCode(String latlng) {
        Retrofit retrofit = create();
        GeoCodingService geoCodingService = retrofit.create(GeoCodingService.class);
        return geoCodingService.reverseGeoCode(latlng, API_KEY);
    }

}
