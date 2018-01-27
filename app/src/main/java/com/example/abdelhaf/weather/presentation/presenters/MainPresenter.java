package com.example.abdelhaf.weather.presentation.presenters;

public interface MainPresenter extends LifeCyclePresenter {
    interface PresenterCallBack {
        void showLoading(boolean show);

        void showConnectionError(Throwable throwable);

        void updateView(Object object);

    }
}
