package com.example.joseph.geocoding.remote;

import com.example.joseph.geocoding.model.GeoCodingResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GeoCodingService {

    @GET("maps/api/geocode/json")
    Observable<GeoCodingResult> searchGeoCode(@Query("address") String address, @Query("key") String key);

    @GET("maps/api/geocode/json")
    Observable<GeoCodingResult> reverseGeoCode(@Query("latlng") String latlng, @Query("key") String key);

}
