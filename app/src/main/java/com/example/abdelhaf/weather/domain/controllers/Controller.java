package com.example.abdelhaf.weather.domain.controllers;
import com.example.abdelhaf.weather.domain.models.WeatherGroupModel;
import com.example.abdelhaf.weather.domain.models.WeatherModel;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public class Controller {

    public interface Weather {
        @GET("data/2.5/forecast")
        Observable<WeatherModel> getWeatherServices(@Query("q") String city, @Query("units") String units, @Query("lat") String lat, @Query("lon") String lon);

        @GET("data/2.5/group")
        Observable<WeatherGroupModel> getGroupWeatherServices(@Query("id") String id, @Query("units") String units);


    }

}
