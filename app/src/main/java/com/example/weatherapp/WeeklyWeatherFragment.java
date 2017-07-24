package com.example.weatherapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.weatherapp.app.AppController;
import com.example.weatherapp.forecast.WeeklyForecast;
import com.google.gson.Gson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;


public class WeeklyWeatherFragment extends Fragment {

    private static final String TAG = "WeeklyWeather";
    private SharedPreferences storage;
    private WeeklyForecast weeklyForecast;
    private List<com.example.weatherapp.forecast.List> list;

    public WeeklyWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_weather, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // Used to do final UI modifications after onCreate and onCreateView
        super.onActivityCreated(savedInstanceState);
        storage = PreferenceManager.getDefaultSharedPreferences(getActivity());
        JSONObjectRequest();
    }

    private void JSONObjectRequest() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = makeURL();

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
                                loadForecastData();
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

    private String makeURL() {
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
        Log.d(TAG, measurementPref);
        if (measurementPref.isEmpty()) {
            url = apiURL + cityID + apiKey;
        } else {
            url = apiURL + cityID + unit + apiKey;
        }

        return url;
    }

    private void loadForecastData() {
        // Gets the forecast data loads it into a list adapter which outputs
        // card views into the recycler view
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        ListAdapter lAdapter = new ListAdapter(list, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(lAdapter);
    }
}
