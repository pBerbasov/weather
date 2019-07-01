package com.pberbasov.weather2.jsonWeather.model;

import com.google.gson.annotations.SerializedName;

public class WeatherDescriptionModel {
    @SerializedName("dt")
    public int dt;
    @SerializedName("main")
    public MainWeatherModel main;
    @SerializedName("wind")
    public WeatherWindModel wind;
    @SerializedName("weather")
    public WeatherWeatherModel[] weather;
}
