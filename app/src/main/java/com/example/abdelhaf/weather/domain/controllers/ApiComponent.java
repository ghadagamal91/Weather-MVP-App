package com.example.abdelhaf.weather.domain.controllers;


import com.example.abdelhaf.weather.domain.module.ApiModule;
import com.example.abdelhaf.weather.domain.module.AppModule;
import com.example.abdelhaf.weather.presentation.ui.activities.MainActivity;
import com.example.abdelhaf.weather.presentation.ui.activities.SplashScreenActivity;
import com.example.abdelhaf.weather.presentation.ui.activities.WeatherDetailActivity;

import dagger.Component;

import javax.inject.Singleton;


@Singleton
@Component(modules = {ApiModule.class, AppModule.class})
public interface ApiComponent {


    void inject(MainActivity mainActivity);

    void inject(SplashScreenActivity splashScreenActivity);

    void inject(WeatherDetailActivity weatherDetailActivity);


}


