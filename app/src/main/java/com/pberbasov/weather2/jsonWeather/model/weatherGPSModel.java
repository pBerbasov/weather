package com.pberbasov.weather2.jsonWeather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class weatherGPSModel {

    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}