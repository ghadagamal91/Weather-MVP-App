package com.example.abdelhaf.weather;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.example.abdelhaf.weather.domain.controllers.ApiComponent;
import com.example.abdelhaf.weather.domain.controllers.DaggerApiComponent;
import com.example.abdelhaf.weather.domain.module.ApiModule;
import com.example.abdelhaf.weather.domain.module.AppModule;
import com.example.abdelhaf.weather.presentation.ui.activities.MainActivity;

import dagger.Component;


public class App extends Application  {

    ApiComponent apiComponent;
    MainActivity mainActivity;


    @Override
    public void onCreate() {
        super.onCreate();

        apiComponent = DaggerApiComponent.builder()
                .apiModule(new ApiModule(getApplicationContext()))
                .appModule(new AppModule(this))
                .build();

    }

    public ApiComponent getApiComponent() {
        return apiComponent;
    }

    public MainActivity getMainActivity() { return mainActivity; }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}