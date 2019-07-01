package com.pberbasov.weather2.jsonWeather.model;

import com.google.gson.annotations.SerializedName;

public class Model5Days {
    @SerializedName("list")
    public WeatherDescriptionModel[] list;
    @SerializedName("city")
    public WeatherCity city;

}
