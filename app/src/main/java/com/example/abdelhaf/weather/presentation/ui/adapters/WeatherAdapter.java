package com.example.abdelhaf.weather.presentation.ui.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abdelhaf.weather.R;
import com.example.abdelhaf.weather.domain.controllers.communicator.WeatherCommunicator;
import com.example.abdelhaf.weather.domain.controllers.communicator.WeatherDetailCommunicator;
import com.example.abdelhaf.weather.domain.models.WeatherModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<WeatherModel.list> list;
    WeatherDetailCommunicator weatherCommunicator;
    Context context;
    View view;

    //constructor to initialize
    public WeatherAdapter(WeatherDetailCommunicator weatherCommunicator, Context context) {
        this.weatherCommunicator = weatherCommunicator;
        this.list = weatherCommunicator.getWeatherDetailItems();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        final Holder holder = (Holder) viewHolder;
        holder.degree.setText(list.get(position).main.temp_max + "°C");
        holder.description.setText(list.get(position).weather.get(0).description);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormatter.parse(list.get(position).dt_txt);
        } catch (ParseException e) {

        }

        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.getDefault());
        String displayTimeValue = timeFormatter.format(date);
        String displayDayValue = dayFormat.format(date);
        holder.day.setText(displayDayValue);
        holder.hour.setText(displayTimeValue);
        String url = "http://openweathermap.org/img/w/" + list.get(position).weather.get(0).icon + ".png";
        holder.icon.setBackground(null);
        Picasso.with(context).load(url)
                .fit().into(holder.icon);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.degree)
        TextView degree;

        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.hour)
        TextView hour;
        @BindView(R.id.description)
        TextView description;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}