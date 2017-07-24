package com.example.weatherapp;

class City {
    // Fields
    private int id;
    private String name;
    private String country;

    // Constructor
    public City() {

    }

    // Methods
    @Override
    public String toString() {
        return name + "\n" + country;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getCountry() {
        return country;
    }
}
