package com.example.weatherapp.forecast;

public class Weather {
    // Fields
    private int id;
    private String description;
    private String icon;

    // Methods
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return description + icon;
    }
}
