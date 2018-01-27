package com.example.abdelhaf.weather.domain.interactors.impl;

import android.content.SharedPreferences;

import com.example.abdelhaf.weather.domain.controllers.Controller;
import com.example.abdelhaf.weather.domain.interactors.base.DefaultSubscriber;
import com.example.abdelhaf.weather.domain.interactors.base.ResponseCallback;
import com.example.abdelhaf.weather.domain.interactors.base.UseCase;
import com.example.abdelhaf.weather.domain.models.WeatherModel;
import com.example.abdelhaf.weather.presentation.presenters.MainPresenter;

import retrofit2.Retrofit;
import rx.Observable;

public class WeatherGroupInteractorImpl extends UseCase implements ResponseCallback {
    private MYCallback mCallback;
    MainPresenter.PresenterCallBack presenterCallBack;
    Controller.Weather weatherServices;
    String ids;
    SharedPreferences pref;
String unit="metric";
    public WeatherGroupInteractorImpl(Retrofit retrofit, String ids, MYCallback mCallback, MainPresenter.PresenterCallBack presenterCallBack) {
        this.mCallback = mCallback;
        this.presenterCallBack = presenterCallBack;
        this.ids = ids;


        weatherServices = retrofit.create(Controller.Weather.class);

        this.execute(new WeatherSubscriber());
    }

    @Override
    protected Observable buildUseCaseObservable() {
        presenterCallBack.showLoading(true);

            return this.weatherServices.getGroupWeatherServices(ids,unit);


    }



    private final class WeatherSubscriber extends DefaultSubscriber<WeatherModel> {

        @Override
        public void onCompleted() {

            presenterCallBack.showLoading(false);
        }

        @Override
        public void onError(Throwable e) {

            mCallback.error(e);
        }

        @Override
        public void onNext(WeatherModel weatherModel) {

            mCallback.success(weatherModel);
        }
    }
}