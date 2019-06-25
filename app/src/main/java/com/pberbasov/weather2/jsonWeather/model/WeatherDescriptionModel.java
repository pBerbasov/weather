package com.pberbasov.weather2.jsonWeather.model;

import com.google.gson.annotations.SerializedName;

public class WeatherDescriptionModel {
    @SerializedName("dt")
    public int dt;
    @SerializedName("main")
    public mainWeatherModel main;
    @SerializedName("wind")
    public weatherWindModel wind;
    @SerializedName("weather")
    public weatherWeatherModel[] weather;
}
