package com.example.abdelhaf.weather.presentation.ui.adapters;


import android.content.Context;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abdelhaf.weather.R;
import com.example.abdelhaf.weather.domain.controllers.Controller;
import com.example.abdelhaf.weather.domain.controllers.communicator.WeatherCommunicator;
import com.example.abdelhaf.weather.domain.models.WeatherModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<WeatherModel> weatherModels;
    WeatherCommunicator weatherCommunicator;
    Context context;
    View view;
    private final int DISPLAY_LENGTH = 2000;

    //constructor to initialize
    public CitiesAdapter(WeatherCommunicator weatherCommunicator, Context context) {
        this.weatherCommunicator = weatherCommunicator;
        this.weatherModels = weatherCommunicator.getWeatherItems();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        final Holder holder = (Holder) viewHolder;

        holder.city.setText(weatherModels.get(position).city.name);
        holder.degree.setText(String.valueOf(weatherModels.get(position).list.get(0).main.temp) + "°C");

        String url = "http://openweathermap.org/img/w/" + weatherModels.get(position).list.get(0).weather.get(0).icon + ".png";
        holder.icon.setBackground(null);
        Picasso.with(context).load(url)
                .fit()
                .into(holder.icon);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherCommunicator.openDetail(weatherModels.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return weatherModels.size();
    }


    //holder for item row
    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.city)
        TextView city;
        @BindView(R.id.degree)
        TextView degree;
        @BindView(R.id.layout)
        RelativeLayout layout;


        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}