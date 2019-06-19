package com.pberbasov.weather2;
import com.pberbasov.weather2.jsonWeather.addWeather;
import com.pberbasov.weather2.jsonWeather.model.WeatherDescriptionModel;
import com.pberbasov.weather2.jsonWeather.model.model5Days;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class WeatherService extends Service {
    final String LOG_TAG = "myLogs";
    model5Days model=new model5Days();
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        DownloadWeather();

        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void DownloadWeather() {
        new Thread(new Runnable() {
            public void run() {
                addWeather.getSingleton().getAPI().loadWeather(
                        524901,
                        "metric",
                        "ru",
                        "a4562acf414871a49438dc43193635ee").enqueue(new Callback<model5Days>() {
                    @Override
                    @EverythingIsNonNull
                    public void onResponse(Call<model5Days> call, Response<model5Days> response) {
                        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
                        if(response.body()!=null&&response.isSuccessful()) {
                            model=response.body();
                            WeatherDescriptionModel[] weatherDescription= model.list;
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
                            for (int i = 0; i <weatherDescription.length ; i++) {
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
                    public void onFailure(Call<model5Days> call, Throwable t) {
                        Log.e("LOG", t.toString());
Log.i("LOG","ошибка");
                        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
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
