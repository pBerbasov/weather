package com.pberbasov.weather2.jsonWeather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddWeather {
    private static AddWeather singleton = null;
    private Interface5days API;

    private AddWeather() {
        API = createAdapter();
    }

    public static AddWeather getSingleton() {
        if (singleton == null) {
            singleton = new AddWeather();
        }

        return singleton;
    }

    public Interface5days getAPI() {
        return API;
    }

    private Interface5days createAdapter() {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return adapter.create(Interface5days.class);
    }
}
