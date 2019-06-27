package com.pberbasov.weather2.jsonWeather.model;

import com.google.gson.annotations.SerializedName;

public class model5Days {
    @SerializedName("list")
    public WeatherDescriptionModel[] list;
    @SerializedName("city")
    public weatherCity city;

}
