package com.example.weatherapp;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private City defaultCity;
    private Boolean currentWeather;
    private SharedPreferences storage;
    private Boolean inSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = PreferenceManager.getDefaultSharedPreferences(this);

        // Get what the user searched and set the location to that city
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            // Load json into a list of cities
            String string = loadJSONFromAsset();
            List<City> list = stringJSONToList(string);

            // Do search
            City city = doMySearch(query, list);
            if (city != null) {
                // save city into preferences and populate location card
                updateLocation(city);
                saveDefaultCity(city);
            } else {
                // City not found
                ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.constraintLayout);
                Snackbar snack = Snackbar.make(cl, "Please check your search parameters", Snackbar.LENGTH_LONG);
                snack.show();
            }
        }

        // Initialise
        loadDefaultCity();
        if (defaultCity != null) {
            loadDefaultForecastView();
            setUpWeatherForecastView();
        } else {
            // Tell user to go choose their city
            CardView cardView = (CardView) findViewById(R.id.weatherForecastView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.constraintLayout);
                    Snackbar snack = Snackbar.make(cl, "Please add your city first via the search bar!", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // When tapping any of the action buttons on the action bar
        switch (item.getItemId()) {
            case R.id.action_settings:
                // load up settings activity
                FrameLayout fl = (FrameLayout) findViewById(R.id.content);
                fl.setElevation(30);
                inSettings = true;
                Fragment fragment = new SettingsFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return false;
        }
    }

    private String loadJSONFromAsset() {
        // Read local json file
        String json;
        try {
            InputStream is = getResources().openRawResource(R.raw.citylist);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private List<City> stringJSONToList(String string) {
        // Convert json array string into a list of cities
        Type listType = new TypeToken<List<City>>() {
        }.getType();
        return new Gson().fromJson(string, listType);
    }

    private City doMySearch(String query, List list) {
        for (int i = 0; i < list.size(); i++) {
            City city = (City) list.get(i);
            if (city.getName().equals(query)) {
                return city;
            }
        }
        return null;
    }

    private void updateLocation(City city) {
        // Update location card view
        TextView cityText = (TextView) findViewById(R.id.cityTextView);
        TextView countryText = (TextView) findViewById(R.id.countryTextView);

        // Set text
        cityText.setText(city.getName());
        countryText.setText(city.getCountry());
    }

    private void saveDefaultCity(City city) {
        // Save object to preferences
        SharedPreferences.Editor editor = storage.edit();
        Gson gson = new Gson();
        String json = gson.toJson(city);
        editor.putString("city", json);
        editor.apply();
    }

    private void loadDefaultCity() {
        // Load default city from preferences
        Gson gson = new Gson();
        String json = storage.getString("city", "");
        defaultCity = gson.fromJson(json, City.class);

        // Check if city is not empty
        if (defaultCity != null) {
            updateLocation(defaultCity);
        }
    }

    private void loadDefaultForecastView() {
        // Load default forecast view from preferences
        currentWeather = storage.getBoolean("pref_key_forecast_view", true);
        currentWeather = !currentWeather;
        changeForecastView();
    }

    private void saveDefaultForecastView(Boolean bool) {
        // Save default view
        SharedPreferences.Editor editor = storage.edit();
        editor.putBoolean("pref_key_forecast_view", bool);
        editor.apply();
    }

    private void setUpWeatherForecastView() {
        // Create on click listener for weather forecast card view
        CardView weatherForecastCardView = (CardView) findViewById(R.id.weatherForecastView);
        weatherForecastCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeForecastView();
            }
        });
    }

    private void changeForecastView() {
        // swap card view text and fragment between current and weekly
        Fragment fragment = new CurrentWeatherFragment();
        TextView weatherForecast = (TextView) findViewById(R.id.weatherForecast);

        // Change text view
        if (currentWeather) {
            weatherForecast.setText(R.string.weekly_weather_forecast);
            currentWeather = false;
            fragment = new WeeklyWeatherFragment();
        } else {
            weatherForecast.setText(R.string.current_weather_forecast);
            currentWeather = true;
        }

        saveDefaultForecastView(currentWeather);
        loadUpFragment(fragment);
    }

    private void loadUpFragment(Fragment fragment) {
        // Load up fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.weatherContent, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (inSettings) {
            // Remove settings fragment from view
            inSettings = false;
            getFragmentManager().popBackStack();
            FrameLayout fl = (FrameLayout) findViewById(R.id.content);
            fl.setElevation(0);
            if (defaultCity != null) {
                loadDefaultForecastView();
            }
        } else {
            // Ask user if they wish to exit the app
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Quit the app?")
                    // Add buttons to dialog
                    .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // ?
                        }
                    });

            // Create the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
