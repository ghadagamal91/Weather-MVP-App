package com.example.abdelhaf.weather.presentation.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdelhaf.weather.App;
import com.example.abdelhaf.weather.R;
import com.example.abdelhaf.weather.domain.controllers.Controller;
import com.example.abdelhaf.weather.domain.controllers.communicator.WeatherCommunicator;
import com.example.abdelhaf.weather.domain.controllers.communicator.WeatherDetailCommunicator;
import com.example.abdelhaf.weather.domain.models.WeatherModel;
import com.example.abdelhaf.weather.presentation.presenters.MainPresenter;
import com.example.abdelhaf.weather.presentation.presenters.impl.WeatherPresenterImpl;
import com.example.abdelhaf.weather.presentation.ui.adapters.CitiesAdapter;
import com.example.abdelhaf.weather.presentation.ui.adapters.CustomAdapter;
import com.example.abdelhaf.weather.presentation.ui.adapters.WeatherAdapter;
import com.example.abdelhaf.weather.presentation.utils.Constants;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Retrofit;
import rx.observers.TestObserver;

public class WeatherDetailActivity extends AppCompatActivity implements WeatherDetailCommunicator, MainPresenter.PresenterCallBack {
    private Unbinder unbinder;
    MainPresenter mainPresenter;
    @Inject
    Retrofit retrofit;
    @Inject
    SharedPreferences sharedPreferences;
    @BindArray(R.array.capital_cities)
    String[] capital_cities;
    ArrayList<WeatherModel> weatherModels;
    WeatherModel weatherModel;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.city)
    TextView city;
    @BindView(R.id.degree)
    TextView degree;
    @BindView(R.id.humidity)
    TextView humidity;
    @BindView(R.id.wind_speed)
    TextView windSpeed;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.add)
    Button addToMainScreen;
    String url = "http://openweathermap.org/img/w/";
    ArrayList<WeatherModel> listOfModels;

    String desc = "";
    WeatherAdapter weatherAdapter;
    String cityName;
    boolean existModel = false;
    WeatherModel savedModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        unbinder = ButterKnife.bind(this);
        App app = (App) getApplication();
        app.getApiComponent().inject(this);
        //weatherModel = (WeatherModel) getIntent().getSerializableExtra(Constants.WEATHER_MODEL);
        cityName = (String) getIntent().getSerializableExtra(Constants.CITY);
        listOfModels = new ArrayList<>();



        mainPresenter = new WeatherPresenterImpl(retrofit, cityName, null, null, WeatherDetailActivity.this);


        listOfModels = getSavedData();
        existModel = checkExist();
        if(existModel)
        {
            addToMainScreen.setBackgroundResource(android.R.drawable.ic_input_delete);
        }else
        {
            addToMainScreen.setBackgroundResource(android.R.drawable.ic_input_add);
        }
        addToMainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(existModel)
                {
                   listOfModels.remove(savedModel);
                    saveData(listOfModels);
                    Toast.makeText(getBaseContext(), getString(R.string.city_deleted), Toast.LENGTH_LONG).show();

                }else

                if (weatherModel != null) {
                    listOfModels.add(weatherModel);
                    saveData(listOfModels);
                    Toast.makeText(getBaseContext(), getString(R.string.city_added), Toast.LENGTH_LONG).show();

                }
            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);


        Timer timer = new Timer();
        MyTimer myTimer = new MyTimer();
        timer.schedule(myTimer, 5000, 5000);

    }

    @Override
    public void showLoading(boolean show) {

    }

    @Override
    public void showConnectionError(Throwable throwable) {
//        Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
//        finish();

        if (existModel == true) {


            this.weatherModel = savedModel;
            setWeatherDetailItems(weatherModel.list);
            if (weatherModel.list != null && weatherModel.list.size() > 0) {
                weatherAdapter = new WeatherAdapter(WeatherDetailActivity.this, this);
                recyclerView.setAdapter(weatherAdapter);
            }
            renderModel(weatherModel);
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();

            finish();
        }


    }

    @Override
    public void updateView(Object object) {
        WeatherModel weatherModel = (WeatherModel) object;
        this.weatherModel = weatherModel;
       existModel= checkExist();
        setWeatherDetailItems(weatherModel.list);
        if (weatherModel.list != null && weatherModel.list.size() > 0) {
            weatherAdapter = new WeatherAdapter(WeatherDetailActivity.this, this);
            recyclerView.setAdapter(weatherAdapter);
        }
        renderModel(weatherModel);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainPresenter != null)
            mainPresenter.onDestroy();
        unbinder.unbind();
    }



    @Override
    public ArrayList<WeatherModel.list> getWeatherDetailItems() {
        return weatherModel.list;
    }

    @Override
    public void setWeatherDetailItems(ArrayList<WeatherModel.list> weatherItems) {

        this.weatherModel.list = weatherItems;
    }

    class MyTimer extends TimerTask {

        public void run() {

            //This runs in a background thread.
            //We cannot call the UI from this thread, so we must call the main UI thread and pass a runnable
            runOnUiThread(new Runnable() {

                public void run() {
                    Resources res = getResources();
                    final TypedArray myImages;

                    if (desc.toLowerCase().contains(getString(R.string.clear_sky))) {
                        myImages = res.obtainTypedArray(R.array.images_clear_sky);
                    } else if (desc.toLowerCase().contains(getString(R.string.cloud))) {
                        myImages = res.obtainTypedArray(R.array.images_cloudy);
                    } else if (desc.toLowerCase().contains(getString(R.string.rain))) {
                        myImages = res.obtainTypedArray(R.array.images_rain);
                    } else if (desc.toLowerCase().contains(getString(R.string.snow))) {
                        myImages = res.obtainTypedArray(R.array.images_snow);
                    } else {
                        myImages = res.obtainTypedArray(R.array.images);
                    }
                    final Random random = new Random();
                    int randomInt = random.nextInt(myImages.length());

                    // Generate the drawableID from the randomInt
                    int drawableID = myImages.getResourceId(randomInt, -1);
                    if (layout != null)
                        layout.setBackgroundResource(drawableID);

                }
            });
        }
    }


    public void renderModel(WeatherModel weatherModel) {
        city.setText(weatherModel.city.name);
        if (weatherModel.list != null && weatherModel.list.size() > 0)

            degree.setText(weatherModel.list.get(0).main.temp + "°C");
        humidity.setText(weatherModel.list.get(0).main.humidity + "%");
        windSpeed.setText(weatherModel.list.get(0).wind.speed + "MPS");
        description.setText(weatherModel.list.get(0).weather.get(0).description);
        desc = weatherModel.list.get(0).weather.get(0).description;
        url = url + weatherModel.list.get(0).weather.get(0).icon + ".png";
        Picasso.with(this).load(url)
                .fit()
                .into(icon);
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

    public boolean checkExist() {
        boolean exist = false;

        for (int i = 0; i < listOfModels.size(); i++) {
            if (listOfModels.get(i).city.name.equalsIgnoreCase(cityName)) {
                exist = true;
                savedModel=listOfModels.get(i);
            }
        }

        return exist;
    }
}
