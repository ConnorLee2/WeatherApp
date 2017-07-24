package com.example.weatherapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.weatherapp.app.AppController;
import com.example.weatherapp.forecast.Forecast;
import com.example.weatherapp.forecast.List;
import com.example.weatherapp.forecast.WeeklyForecast;
import com.google.gson.Gson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class CurrentWeatherFragment extends Fragment {
    private static final String TAG = "CurrentWeather";
    private SharedPreferences storage;
    private Forecast currentForecast;
    private WeeklyForecast weeklyForecast;
    private java.util.List<List> list;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_weather, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // Used to do final UI modifications after onCreate and onCreateView
        super.onActivityCreated(savedInstanceState);
        storage = PreferenceManager.getDefaultSharedPreferences(getActivity());
        CurrentWeatherRequest();
        TomorrowWeatherRequest();
    }

    private void CurrentWeatherRequest() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = makeCurrentURL();

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Moshi moshi = new Moshi.Builder().build();
                        JsonAdapter<Forecast> jsonAdapter = moshi.adapter(Forecast.class);
                        try {
                            currentForecast = jsonAdapter.fromJson(response.toString());
                            loadCurrentForecastData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void TomorrowWeatherRequest() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = makeTomorrowURL();

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Moshi moshi = new Moshi.Builder().build();
                        JsonAdapter<WeeklyForecast> jsonAdapter = moshi.adapter(WeeklyForecast.class);
                        try {
                            weeklyForecast = jsonAdapter.fromJson(response.toString());
                            if (weeklyForecast != null) {
                                list = weeklyForecast.getList();
                                loadTomorrowForecastData();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private String makeCurrentURL() {
        // apiURL + cityId + metric + apiKey
        String url;
        String apiURL = "http://api.openweathermap.org/data/2.5/weather?id=";
        String apiKey = "&APPID=c4c0909c8b3156e6b6a1e14222dbe0c7";

        // Load default city from preferences and get city ID
        Gson gson = new Gson();
        String json = storage.getString("city", "");
        City city = gson.fromJson(json, City.class);
        String cityID = Integer.toString(city.getId());

        // Metric
        String measurementPref = storage.getString("pref_key_temperature", "metric");
        String unit = "&units=" + measurementPref;
        if (measurementPref.isEmpty()) {
            url = apiURL + cityID + apiKey;
        } else {
            url = apiURL + cityID + unit + apiKey;
        }

        return url;
    }

    private String makeTomorrowURL() {
        // apiURL + cityId + metric + apiKey
        String url;
        String apiURL = "http://api.openweathermap.org/data/2.5/forecast?id=";
        String apiKey = "&APPID=08eb9fd35f4642d5b65b8cd586a9c22e";

        // Load default city from preferences and get city ID
        Gson gson = new Gson();
        String json = storage.getString("city", "");
        City city = gson.fromJson(json, City.class);
        String cityID = Integer.toString(city.getId());

        // Metric
        String measurementPref = storage.getString("pref_key_temperature", "metric");
        String unit = "&units=" + measurementPref;
        if (measurementPref.isEmpty()) {
            url = apiURL + cityID + apiKey;
        } else {
            url = apiURL + cityID + unit + apiKey;
        }

        return url;
    }

    private void loadCurrentForecastData() {
        // Find views
        ImageView weatherIcon = (ImageView) getActivity().findViewById(R.id.weatherIcon);
        TextView weatherDescription = (TextView) getActivity().findViewById(R.id.weatherDescription);
        TextView temperature = (TextView) getActivity().findViewById(R.id.temperature);

        // Which temperature unit used?
        String measurementPref = storage.getString("pref_key_temperature", "metric");
        String tempUnit = currentForecast.getMain().getTemp() + "K";
        if (Objects.equals(measurementPref, "metric")) {
            tempUnit = currentForecast.getMain().getTemp() + "°C";
        } else if (Objects.equals(measurementPref, "imperial")) {
            tempUnit = currentForecast.getMain().getTemp() + "F";
        }

        // Which weather icon?
        String icon = currentForecast.getWeather().get(0).getIcon().substring(0, 2);
        weatherIcon.setImageResource(determineWeatherIcon(icon));

        // Edit views
        weatherDescription.setText(currentForecast.getWeather().get(0).getDescription());
        temperature.setText(tempUnit);
    }

    private int tomorrowPosition() {
        // 24 hours = 86400 seconds
        // Calculate tomorrow's position and return it
        int tomorrow = (list.get(0).getDt()) + 86400;

        for (int i = 1; i < list.size(); i++) {
            List forecast = list.get(i);
            if (forecast.getDt() == tomorrow) {
                return i;
            }
        }

        return 0;
    }

    private void loadTomorrowForecastData() {
        // Load tomorrow's data
        List tomorrowForecast = list.get(tomorrowPosition());

        // Find views
        ImageView tomorrowIcon = (ImageView) getActivity().findViewById(R.id.tomorrowIcon);
        TextView tomorrowDescription = (TextView) getActivity().findViewById(R.id.tomorrowDescription);
        TextView tomorrowTemp = (TextView) getActivity().findViewById(R.id.tomorrowTemp);

        // Which temperature unit used?
        String measurementPref = storage.getString("pref_key_temperature", "metric");
        String tempUnit = tomorrowForecast.getMain().getTemp() + "K";
        if (Objects.equals(measurementPref, "metric")) {
            tempUnit = tomorrowForecast.getMain().getTemp() + "°C";
        } else if (Objects.equals(measurementPref, "imperial")) {
            tempUnit = tomorrowForecast.getMain().getTemp() + "F";
        }

        // Which weather icon?
        String icon = tomorrowForecast.getWeather().get(0).getIcon().substring(0, 2);
        tomorrowIcon.setImageResource(determineWeatherIcon(icon));

        // Edit views
        tomorrowDescription.setText(tomorrowForecast.getWeather().get(0).getDescription());
        tomorrowTemp.setText(tempUnit);
    }

    private int determineWeatherIcon(String icon) {
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
}
