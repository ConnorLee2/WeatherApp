package com.example.weatherapp.forecast;

import java.util.List;

public class Forecast {
    // Fields
    private List<Weather> weather;
    private Main main;

    // Constructors
    public Forecast() {

    }

    // Methods
    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

}
