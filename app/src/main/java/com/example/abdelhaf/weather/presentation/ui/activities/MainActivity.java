package com.example.abdelhaf.weather.presentation.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

public class MainActivity extends AppCompatActivity implements LocationListener, MainPresenter.PresenterCallBack,WeatherCommunicator {
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
    public double longitude,latitude;
    ArrayList<WeatherModel>weatherModels;
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
//        citiesAdapter = new CitiesAdapter(MainActivity.this,this);
//        recyclerView.setAdapter(citiesAdapter);

        weatherModels=new ArrayList<>();
        weatherModels=getSavedData();
        String x=sharedPreferences.getString(Constants.MODEL0,"");
        if(sharedPreferences.getString(Constants.MODEL0,"").length()==0) {
            boolean check = checkLocationPermission();
            if (check) {
                callLocationService();
            } else {
                firstCity = "London";
            }

            sharedPreferences.edit().putString(Constants.ISFIRSTRUN,"false").commit();
        }else
        {


            String url_part=String.valueOf(weatherModels.get(0).city.id);
            for (int i=1;i<weatherModels.size()-1;i++)
            {
                url_part=url_part+","+weatherModels.get(i).city.id;

            }

            mainPresenter = new WeatherGroupPresenterImpl(retrofit, url_part, MainActivity.this);

        }
        search_box.setThreshold(1);
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, capital_cities);
        search_box.setAdapter(new CustomAdapter<String>(this.getApplication(), android.R.layout.simple_spinner_dropdown_item, capital_cities));

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



                ArrayList<WeatherModel> tmpModels=getSavedData();
                weatherModels=tmpModels;
                boolean exist=false;

                for(int i=0;i<tmpModels.size();i++)
                {
                    if(tmpModels.get(i).city.name.equalsIgnoreCase(search_box.getText().toString()))
                        exist=true;
                }
                if(tmpModels.size()<5&&exist==false)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setMessage(getText(R.string.alert_Message));
                    builder.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //
                            mainPresenter = new WeatherPresenterImpl(retrofit, search_box.getText().toString(),null,null, MainActivity.this);

                           if(weatherModels.size()!=0)
                           {
                                Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
                                intent.putExtra(Constants.WEATHER_MODEL, weatherModels.get(weatherModels.size() - 1));
                                startActivity(intent);
                            }
                            else
                           {
                               Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                           }
                        }
                    })
                            .setNegativeButton(getText(R.string.cancel) , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this,WeatherDetailActivity.class);
                                    intent.putExtra(Constants.CITY,search_box.getText().toString());
                                    startActivity(intent);

                                }
                            });

                    // Create the AlertDialog object and return it
                    builder.create().show();
                }else
                {
                    Intent intent = new Intent(MainActivity.this,WeatherDetailActivity.class);
                    intent.putExtra(Constants.CITY,search_box.getText().toString());
                    startActivity(intent);
                }


            }
        });


        Toast.makeText(getBaseContext(), firstCity, Toast.LENGTH_LONG).show();

    }

    @Override
    public void showLoading(boolean show) {

    }

    @Override
    public void showConnectionError(Throwable throwable) {

        handleOfflineMode();
    }

    @Override
    public void updateView(Object object) {
        WeatherModel weatherModel = (WeatherModel) object;
        ArrayList<String> list = new ArrayList<String>();

        firstCity=weatherModel.city.name;
        weatherModels.add(weatherModel);
        saveData(weatherModels);

        setWeatherItems(weatherModels);
        citiesAdapter = new CitiesAdapter(MainActivity.this,this);
        recyclerView.setAdapter(citiesAdapter);


        Toast.makeText(getBaseContext(), firstCity, Toast.LENGTH_LONG).show();
        String x = "xyz";

    }

    @Override
    public void onLocationChanged(Location location) {


        longitude=location.getLongitude();
        latitude=location.getLatitude();


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
                    sharedPreferences.edit().putString(Constants.PERMISSION_GRANTED,"PERMISSION_GRANTED").commit();
                }

                 else {

                    firstCity="London";
                    mainPresenter = new WeatherPresenterImpl(retrofit, firstCity,null,null, MainActivity.this);

                }


                return;
            }

        }
    }
public void callLocationService()
{

    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
        List<Address>  addresses;
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager.requestLocationUpdates(provider, 20000, 1, this);


        if (location != null)
            onLocationChanged(location);

        if(latitude!=0.0&&longitude!=0.0)
        mainPresenter = new WeatherPresenterImpl(retrofit, search_box.getText().toString(),String.valueOf(latitude),String.valueOf(longitude), MainActivity.this);

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

        this.weatherModels=weatherItems;
    }

    @Override
    public void openDetail(WeatherModel weatherModel) {

        Intent intent = new Intent(MainActivity.this,WeatherDetailActivity.class);
        if(weatherModel.list!=null&&weatherModel.list.size()>0) {
            intent.putExtra(Constants.WEATHER_MODEL, weatherModel);
        }else
        {
            intent.putExtra(Constants.CITY,weatherModel.city.name);
        }
        startActivity(intent);
    }

    @Override
    public void deleteModel(WeatherModel weatherModel) {

        weatherModels.remove(weatherModel);

        saveData(weatherModels);

    }

//    public void saveDataCity1(WeatherModel weatherModel)
//    {
//
//
//            sharedPreferences.edit().putString(Constants.CITY1,weatherModel.city.name).commit();
//            sharedPreferences.edit().putString(Constants.ID1,String.valueOf(weatherModel.city.id)).commit();
//            sharedPreferences.edit().putString(Constants.DESCRIPTION1,weatherModel.list.get(0).weather.get(0).description).commit();
//            sharedPreferences.edit().putString(Constants.HUMIDITY1,String.valueOf(weatherModel.list.get(0).main.humidity)).commit();
//            sharedPreferences.edit().putString(Constants.TEMP1,String.valueOf(weatherModel.list.get(0).main.temp)).commit();
//            sharedPreferences.edit().putString(Constants.SPEED1,String.valueOf(weatherModel.list.get(0).wind.speed)).commit();
//
//
//    }

    public ArrayList<WeatherModel> getSavedData()
    {
        ArrayList<WeatherModel> tmpModels= new ArrayList<>();
        String json=null;
        Gson gson = new Gson();
         json = sharedPreferences.getString(Constants.MODEL0, null);
        if(json!=null) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }

        json = sharedPreferences.getString(Constants.MODEL0, null);
        if(json!=null) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }

        json = sharedPreferences.getString(Constants.MODEL1, null);
        if(json!=null) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }

        json = sharedPreferences.getString(Constants.MODEL2, null);
        if(json!=null) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }

        json = sharedPreferences.getString(Constants.MODEL3, null);
        if(json!=null) {
            WeatherModel weatherModel = gson.fromJson(json, WeatherModel.class);
            tmpModels.add(weatherModel);
        }
        return  tmpModels;
    }

    public void saveData(ArrayList<WeatherModel> list)
    {
        Gson gson = new Gson();


        for(int k=0;k<5;k++)
        {
            sharedPreferences.edit().putString("MODEL"+k,"").commit();
        }
        for(int i=0;i<list.size();i++)
        {
            String json = gson.toJson(list.get(i));
            sharedPreferences.edit().putString("MODEL"+i,json).commit();

        }
    }


    public void handleOfflineMode()
    {

            ArrayList<WeatherModel> savedWeatherData = getSavedData();
            if(savedWeatherData.size()>0)
            {
            setWeatherItems(savedWeatherData);
                citiesAdapter = new CitiesAdapter(MainActivity.this,this);
                recyclerView.setAdapter(citiesAdapter);
        }

    }
}

