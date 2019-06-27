package com.pberbasov.weather2;

import com.pberbasov.weather2.jsonWeather.AddWeather;
import com.pberbasov.weather2.jsonWeather.model.WeatherDescriptionModel;
import com.pberbasov.weather2.jsonWeather.model.Model5Days;
import com.pberbasov.weather2.jsonWeather.model.WeatherCity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class WeatherService extends Service {
    Model5Days model = new Model5Days();

    String latitude;
    String longitude;
    String city;

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        //получаем данные из активити
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        city = intent.getStringExtra("city");
        DownloadWeather();
        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        super.onDestroy();

    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    void DownloadWeather() {
        new Thread(new Runnable() {
            public void run() {
                AddWeather.getSingleton().getAPI().loadWeather(
                        city,
                        latitude,
                        longitude,
                        "metric",
                        getString(R.string.local),
                        "a4562acf414871a49438dc43193635ee").enqueue(new Callback<Model5Days>() {
                    @Override
                    @EverythingIsNonNull
                    public void onResponse(Call<Model5Days> call, Response<Model5Days> response) {
                        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                        if (response.body() != null && response.isSuccessful()) {
                            model = response.body();
                            //получаем данные retofit из Json и отправляем их в Активити
                            WeatherDescriptionModel[] weatherDescription = model.list;
                            WeatherCity city = model.city;
                            intent.putExtra("cityNow",
                                    String.valueOf(city.getCityName()));
                            intent.putExtra("lat",
                                    String.valueOf(city.coord.getLat()));
                            intent.putExtra("lon",
                                    String.valueOf(city.coord.getLon()));
                            intent.putExtra("tempNow",
                                    String.valueOf(weatherDescription[0].main.getTemp()));
                            intent.putExtra("pressureNow",
                                    String.valueOf(weatherDescription[0].main.getPressure()));
                            intent.putExtra("humidityNow",
                                    String.valueOf(weatherDescription[0].main.getHumidity()));
                            intent.putExtra("windNow",
                                    String.valueOf(weatherDescription[0].wind.getSpeed()));
                            intent.putExtra("weatherDescrip",
                                    String.valueOf(weatherDescription[0].weather[0].getDescription()));
                            for (int i = 0; i < weatherDescription.length; i++) {
                                intent.putExtra("ok",
                                        0);
                                intent.putExtra("date",
                                        String.valueOf(weatherDescription[i].dt));
                                intent.putExtra("temp",
                                        String.valueOf(weatherDescription[i].main.getTemp()));
                                intent.putExtra("desriptionItem",
                                        String.valueOf(weatherDescription[i].weather[0].getDescription()));
                                intent.putExtra("wind",
                                        String.valueOf(weatherDescription[i].wind.getSpeed()));
                                sendBroadcast(intent);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<Model5Days> call, Throwable t) {

                        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                        //отправляем 1 для сообщения об ошибке
                        intent.putExtra("ok",
                                1);
                        sendBroadcast(intent);
                    }
                });
                stopSelf();
            }
        }).start();
    }
}
