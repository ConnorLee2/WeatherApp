package com.example.weatherapp.forecast;

public class List {
    // Fields
    private int dt;
    private Main main;
    private java.util.List<Weather> weather;
    private String dt_txt;

    // Methods
    public int getDt() {
        return dt;
    }

    public Main getMain() {
        return main;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    @Override
    public String toString() {
        return dt_txt + "\n" + main.toString() + "\n" + weather.toString();
    }
}
