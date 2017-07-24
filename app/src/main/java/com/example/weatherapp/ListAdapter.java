package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.forecast.List;

import java.util.Objects;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private final Context context;
    private final java.util.List<List> list;

    ListAdapter(java.util.List<List> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weekly_forecast_card_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        List forecast = list.get(position);
        holder.weeklyDate.setText(forecast.getDt_txt());
        holder.weeklyWeather.setText(forecast.getWeather().get(0).getDescription());
        holder.weeklyTemperature.setText(getTemperature(forecast));
        holder.weeklyWeatherIcon.setImageResource(getIcon(forecast));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getTemperature(List forecast) {
        // Which temperature unit used?
        SharedPreferences storage = PreferenceManager.getDefaultSharedPreferences(context);
        String measurementPref = storage.getString("pref_key_temperature", "metric");
        String tempUnit = forecast.getMain().getTemp() + "K";
        if (Objects.equals(measurementPref, "metric")) {
            tempUnit = forecast.getMain().getTemp() + "°C";
        } else if (Objects.equals(measurementPref, "imperial")) {
            tempUnit = forecast.getMain().getTemp() + "F";
        }
        return tempUnit;
    }

    private int getIcon(List forecast) {
        String icon = forecast.getWeather().get(0).getIcon().substring(0, 2);
        switch (icon) {
            case "01":
                return R.drawable.sunny;
            case "02":
                return R.drawable.few_clouds;
            case "03":
                return R.drawable.scattered_clouds;
            case "04":
                return R.drawable.cloudy;
            case "09":
                return R.drawable.showers;
            case "10":
                return R.drawable.shower_rain;
            case "11":
                return R.drawable.lightning;
            case "13":
                return R.drawable.snow;
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView weeklyDate;
        final TextView weeklyWeather;
        final TextView weeklyTemperature;
        final ImageView weeklyWeatherIcon;

        MyViewHolder(View view) {
            super(view);
            weeklyDate = (TextView) view.findViewById(R.id.weeklyDate);
            weeklyWeather = (TextView) view.findViewById(R.id.weeklyWeather);
            weeklyTemperature = (TextView) view.findViewById(R.id.weeklyTemperature);
            weeklyWeatherIcon = (ImageView) view.findViewById(R.id.weeklyWeatherIcon);
        }
    }
}