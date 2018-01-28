package com.example.abdelhaf.weather.presentation.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.Manifest;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.abdelhaf.weather.App;
import com.example.abdelhaf.weather.R;
import com.example.abdelhaf.weather.domain.controllers.Controller;
import com.example.abdelhaf.weather.domain.controllers.communicator.WeatherCommunicator;
import com.example.abdelhaf.weather.domain.models.WeatherGroupModel;
import com.example.abdelhaf.weather.domain.models.WeatherModel;
import com.example.abdelhaf.weather.presentation.presenters.MainPresenter;
import com.example.abdelhaf.weather.presentation.presenters.impl.WeatherGroupPresenterImpl;
import com.example.abdelhaf.weather.presentation.presenters.impl.WeatherPresenterImpl;
import com.example.abdelhaf.weather.presentation.ui.adapters.CitiesAdapter;
import com.example.abdelhaf.weather.presentation.ui.adapters.CustomAdapter;
import com.example.abdelhaf.weather.presentation.utils.Constants;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements LocationListener, MainPresenter.PresenterCallBack, WeatherCommunicator {
    private Unbinder unbinder;
    MainPresenter mainPresenter;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    @BindArray(R.array.capital_cities)
    String[] capital_cities;
    @BindView(R.id.search_box)
    public AutoCompleteTextView search_box;
    @BindView(R.id.down_arrow)
    public ImageView down_arrow;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    MainActivity activity;
    LocationManager locationManager;
    String provider;
    Geocoder geocoder;
    String firstCity;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public double longitude, latitude;
    ArrayList<WeatherModel> weatherModels;
    private RecyclerView.LayoutManager mLayoutManager;
    CitiesAdapter citiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        App app = (App) getApplication();
        app.getApiComponent().inject(this);
        app.setMainActivity(this);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        recyclerView.setLayoutManager(mLayoutManager);
        weatherModels = new ArrayList<>();
        weatherModels = getSavedData();

        if (sharedPreferences.getString(Constants.MODEL0, "").length() == 0) {
            boolean check = checkLocationPermission();
            if (check) {
                callLocationService();
            } else {
                firstCity = "London";
            }

            sharedPreferences.edit().putString(Constants.ISFIRSTRUN, "false").commit();
        } else {


            String url_part = "";
            for (int i = 0; i < weatherModels.size(); i++) {
                url_part = url_part + "," + weatherModels.get(i).city.id;

            }
            url_part = url_part.replaceFirst(",", "");

//            if (weatherModels.size() == 1) {
//                mainPresenter = new WeatherPresenterImpl(retrofit, weatherModels.get(0).city.name, null, null, MainActivity.this);
//
//            } else
            mainPresenter = new WeatherGroupPresenterImpl(retrofit, url_part, MainActivity.this);

        }
        search_box.setThreshold(1);

        search_box.setAdapter(new CustomAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, capital_cities));

        search_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_box.showDropDown();
            }
        });

        down_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_box.showDropDown();
            }
        });

        search_box.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
                firstCity = search_box.getText().toString();
                intent.putExtra(Constants.CITY, search_box.getText().toString());
                startActivity(intent);


            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void showLoading(boolean show) {

    }

    @Override
    public void showConnectionError(Throwable throwable) {

        Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        handleOfflineMode();
    }

    @Override
    public void updateView(Object object) {

        if (object instanceof WeatherModel) {
            WeatherModel weatherModel = (WeatherModel) object;
            firstCity = weatherModel.city.name;
            weatherModels.add(weatherModel);


        } else if (object instanceof WeatherGroupModel) {
            WeatherGroupModel weatherGroupModel = (WeatherGroupModel) object;
            for (int i = 0; i < weatherGroupModel.list.size(); i++) {
                weatherModels.get(i).city.name = weatherGroupModel.list.get(i).name;
                weatherModels.get(i).list.get(0).main.temp = weatherGroupModel.list.get(i).main.temp;
                weatherModels.get(i).list.get(0).weather.get(0).icon = weatherGroupModel.list.get(i).weather.get(0).icon;

            }
        }
        saveData(weatherModels);
        setWeatherItems(weatherModels);

        citiesAdapter = new CitiesAdapter(MainActivity.this, this);
        recyclerView.setAdapter(citiesAdapter);

    }

    @Override
    public void onLocationChanged(Location location) {


        longitude = location.getLongitude();
        latitude = location.getLatitude();


    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    callLocationService();
                    sharedPreferences.edit().putString(Constants.PERMISSION_GRANTED, "PERMISSION_GRANTED").commit();
                } else {

                    firstCity = "London";
                    mainPresenter = new WeatherPresenterImpl(retrofit, search_box.getText().toString(), null, null, MainActivity.this);

                }


                return;
            }

        }
    }

    public void callLocationService() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            List<Address> addresses;
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            geocoder = new Geocoder(this, Locale.getDefault());
            locationManager.requestLocationUpdates(provider, 20000, 1, this);


            if (location != null)
                onLocationChanged(location);

            if (latitude != 0.0 && longitude != 0.0) {
                mainPresenter = new WeatherPresenterImpl(retrofit, search_box.getText().toString(), String.valueOf(latitude), String.valueOf(longitude), MainActivity.this);

            } else {
                Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainPresenter != null)
            mainPresenter.onDestroy();
        unbinder.unbind();
    }

    @Override
    public ArrayList<WeatherModel> getWeatherItems() {
        return weatherModels;
    }

    @Override
    public void setWeatherItems(ArrayList<WeatherModel> weatherItems) {

        this.weatherModels = weatherItems;
    }

    @Override
    public void openDetail(WeatherModel weatherModel) {


        Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
        intent.putExtra(Constants.CITY, weatherModel.city.name);

        startActivity(intent);
    }


    public ArrayList<WeatherModel> getSavedData() {
        ArrayList<WeatherModel> tmpModels = new ArrayList<>();
        String json = "";
        Gson gson = new Gson();
        json = sharedPreferences.getString(Constants.MODEL0, null);
        if (json != null && json.length() > 0) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }


        json = sharedPreferences.getString(Constants.MODEL1, null);
        if (json != null && json.length() > 0) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }

        json = sharedPreferences.getString(Constants.MODEL2, null);
        if (json != null && json.length() > 0) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }

        json = sharedPreferences.getString(Constants.MODEL3, null);
        if (json != null && json.length() > 0) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }

        json = sharedPreferences.getString(Constants.MODEL4, null);
        if (json != null && json.length() > 0) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }

        return tmpModels;
    }

    public void saveData(ArrayList<WeatherModel> list) {
        Gson gson = new Gson();


        for (int k = 0; k < 5; k++) {
            sharedPreferences.edit().putString("MODEL" + k, "").commit();
        }
        for (int i = 0; i < list.size(); i++) {
            String json = gson.toJson(list.get(i));
            sharedPreferences.edit().putString("MODEL" + i, json).commit();

        }
    }


    public void handleOfflineMode() {

        ArrayList<WeatherModel> savedWeatherData = getSavedData();
        if (savedWeatherData.size() > 0) {
            setWeatherItems(savedWeatherData);
            citiesAdapter = new CitiesAdapter(MainActivity.this, this);
            recyclerView.setAdapter(citiesAdapter);
        }

    }

    public void updateList() {

        ArrayList<WeatherModel> savedWeatherData = getSavedData();

        setWeatherItems(savedWeatherData);
        citiesAdapter = new CitiesAdapter(MainActivity.this, this);
        recyclerView.setAdapter(citiesAdapter);


    }

}

