package com.pberbasov.weather2.jsonWeather;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class addWeather {
        private static addWeather singleton = null;
        private interface5days API;

        private addWeather() {
            API = createAdapter();
        }

        public static addWeather getSingleton() {
            if(singleton == null) {
                singleton = new addWeather();
            }

            return singleton;
        }

        public interface5days getAPI() {
            return API;
        }

        private interface5days createAdapter() {
            Retrofit adapter = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return adapter.create(interface5days.class);
        }
    }
