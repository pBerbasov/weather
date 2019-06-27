package com.pberbasov.weather2.jsonWeather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherCity {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("coord") public WeatherGPSModel coord;
    public String getCityName() {
        return name;
    }
}
