package com.pberbasov.weather2.jsonWeather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class weatherWeatherModel {
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("icon")
    @Expose
    private String icon;

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
