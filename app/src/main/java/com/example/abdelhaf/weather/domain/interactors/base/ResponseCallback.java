package com.example.abdelhaf.weather.domain.interactors.base;


public interface ResponseCallback {
    void unsubscribe();

    interface MYCallback {
        void success(Object data);

        void error(Throwable t);
    }

}
