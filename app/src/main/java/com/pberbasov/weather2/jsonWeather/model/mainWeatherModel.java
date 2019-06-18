package com.pberbasov.weather2.jsonWeather.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class mainWeatherModel {
    @SerializedName("temp")
    @Expose private Double temp;
    @SerializedName("pressure")
    @Expose private String pressure;
    @SerializedName("humidity")
    @Expose private String humidity;

    public Double getTemp(){
        return temp;
    }
    public String getPressure(){
        return pressure;
    }
    public String getHumidity(){
        return humidity;
    }
}
