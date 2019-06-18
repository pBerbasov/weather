package com.pberbasov.weather2.jsonWeather.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class weatherWindModel {
    @SerializedName("speed")
    @Expose private String speed;
    @SerializedName("deg")
    @Expose private String deg;
    public String getSpeed(){
        return speed;
    }
    public String getDeg(){
        return deg;
    }
}
