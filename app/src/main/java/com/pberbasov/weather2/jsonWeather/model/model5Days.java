package com.pberbasov.weather2.jsonWeather.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class model5Days {
    @SerializedName("list") public WeatherDescriptionModel[] list;
    @SerializedName("cod")
    @Expose private String cod;

    public String getCod(){
        return cod;
    }
}
