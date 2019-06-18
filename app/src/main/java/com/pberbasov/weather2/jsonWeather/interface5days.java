package com.pberbasov.weather2.jsonWeather;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Query;
import com.pberbasov.weather2.jsonWeather.model.model5Days;

public interface interface5days {
    @GET("data/2.5/forecast")
    Call<model5Days> loadWeather(@Query("id") int cityCountry,
                                 @Query("units") String metric,
                                 @Query("lang") String lang,
                                 @Query("appid") String keyApi);

}
