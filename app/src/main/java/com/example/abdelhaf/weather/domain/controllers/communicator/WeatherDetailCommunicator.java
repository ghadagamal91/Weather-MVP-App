package com.example.abdelhaf.weather.domain.controllers.communicator;

import com.example.abdelhaf.weather.domain.models.WeatherModel;

import java.util.ArrayList;


public interface WeatherDetailCommunicator {

    public ArrayList<WeatherModel.list>  getWeatherDetailItems();

    void setWeatherDetailItems(ArrayList<WeatherModel.list> weatherItems);


}
