package com.pberbasov.weather2.jsonWeather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class weatherCity {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("coord") public weatherGPSModel coord;
    public String getCityName() {
        return name;
    }
}
