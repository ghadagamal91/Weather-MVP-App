package com.example.abdelhaf.weather.presentation.presenters.impl;

import com.example.abdelhaf.weather.domain.interactors.base.ResponseCallback;
import com.example.abdelhaf.weather.domain.interactors.impl.WeatherGroupInteractorImpl;
import com.example.abdelhaf.weather.domain.interactors.impl.WeatherInteractorImpl;
import com.example.abdelhaf.weather.presentation.presenters.MainPresenter;

import retrofit2.Retrofit;

public class WeatherGroupPresenterImpl implements MainPresenter, ResponseCallback.MYCallback {
    PresenterCallBack presenterCallBack;
    ResponseCallback responseCallback;

    public WeatherGroupPresenterImpl(Retrofit retrofit, String ids, PresenterCallBack presenterCallBack) {
        this.presenterCallBack = presenterCallBack;
        responseCallback = new WeatherGroupInteractorImpl(retrofit, ids, this, presenterCallBack);
    }


    @Override
    public void success(Object data) {
        presenterCallBack.updateView(data);
    }

    @Override
    public void error(Throwable t) {
        presenterCallBack.showConnectionError(t);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        responseCallback.unsubscribe();
    }

}

