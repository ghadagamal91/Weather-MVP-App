package com.example.abdelhaf.weather.domain.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.abdelhaf.weather.presentation.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class ApiModule {

    Context context;

    public ApiModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        try {
            return new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request request;
                            // Customize the request

                                request = original.newBuilder()
                                        .header("Content-Type", "application/json")
                                        .header("x-api-key","6a361b3e8cbdae41aebf9ce467ce4c6c")
                                        .build();

                            System.out.println(request.url().toString());
                            Response response = chain.proceed(request);
                            response.cacheResponse();
                            if (response.code() != 200 && response.code() != 201
                                    && response.code() != 204 && response.code() != 403/* success case but no content found */) {
                                return null;
                            }
                            // Customize or return the response

                            return response;
                        }
                    })
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Provides
    @Singleton
    Retrofit provideCall(OkHttpClient okHttpClient) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .client(okHttpClient)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.addConverterFactory(new Gson())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    Picasso providePicasso(OkHttpClient okHttpClient) {

        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Log.e("Picasso", "Failed to load image:" + uri);
                    }
                })
                .build();


        return picasso;
    }


    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }


}