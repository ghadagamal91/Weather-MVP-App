package com.example.abdelhaf.weather.domain.controllers.communicator;

import com.example.abdelhaf.weather.domain.models.WeatherModel;

import java.util.ArrayList;



public interface WeatherCommunicator {

    ArrayList<WeatherModel> getWeatherItems();

    void setWeatherItems(ArrayList<WeatherModel> weatherItems);

    void openDetail(WeatherModel weatherModel);

    void deleteModel(WeatherModel weatherModel);
}
