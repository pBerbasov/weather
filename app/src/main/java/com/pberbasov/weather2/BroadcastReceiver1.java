package com.pberbasov.weather2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BroadcastReceiver1 extends BroadcastReceiver {
    private String tempNowStr;

    private String pressureNowStr;

    private String humidityNowStr;

    private String windNowStr;

    private String descripNowStr;

    private String weatherCityStr;
    ProgressBar progress;
    Activity main;
    String latitude;
    String longitude;
    DB db;
    Cursor cursor;
    Location GPS;
    public BroadcastReceiver1(Activity MainActviti,Cursor cursor,String latitude,String longitude,DB db,ProgressBar progress,Location GPS) {
        main=MainActviti;
        this.cursor=cursor;
        this.db=db;
        this.latitude=latitude;
        this.longitude=longitude;
        this.progress=progress;
        this.GPS=GPS;
    }
    @SuppressLint("SetTextI18n")
    public void onReceive(Context context, Intent intent) {
        //Получаем данные из сервиса, если ок, загрузка прошла успешно.
        int ok = intent.getIntExtra("ok", 1);
        if (ok == 0) {
            //убираем прогресс бар
            progress = main.findViewById(R.id.progres);
            progress.setVisibility(View.GONE);

            //получаем данные из сервиса
            weatherCityStr = intent.getStringExtra("cityNow");
            String temp = ceil(intent.getStringExtra("temp"));
            String date = intent.getStringExtra("date");
            String wind = intent.getStringExtra("wind");
            String description = intent.getStringExtra("desriptionItem");
            tempNowStr = ceil(intent.getStringExtra("tempNow"));
            pressureNowStr = intent.getStringExtra("pressureNow");
            humidityNowStr = intent.getStringExtra("humidityNow");
            windNowStr = intent.getStringExtra("windNow");
            descripNowStr = intent.getStringExtra("weatherDescrip");
            latitude = intent.getStringExtra("lat");
            longitude = intent.getStringExtra("lon");
            //сохраняем GPS
            GPS.applyGPS(latitude, longitude);

            //Добавляем или обновляем записи в базе данных
            int updateWeather = db.uppRec(date, temp, wind, description);
            if (updateWeather < 1) db.addRec(date, temp, wind, description);
        } else
            //Повторно запускаем сервис если произошла ошибка загрузки данных
            main.startService(new Intent(main, WeatherService.class)
                    .putExtra("latitude", latitude)
                    .putExtra("longitude", longitude));
        cursor.requery();

        //Обновляем данные в верхнем центральном окне
        TextView weatherCity = main.findViewById(R.id.weatherCity);
        weatherCity.setText(main.getString(R.string.weather_city) + " " + weatherCityStr);

        TextView tempNow = main.findViewById(R.id.temp);
        tempNow.setText(tempNowStr);

        TextView pressureNow = main.findViewById(R.id.pressure);
        pressureNow.setText(main.getString(R.string.pressure) + " " + pressureNowStr);

        TextView humidityNow = main.findViewById(R.id.humidity);
        humidityNow.setText(main.getString(R.string.humidity) + " " + humidityNowStr + "%");

        TextView windNow = main.findViewById(R.id.wind);
        windNow.setText(main.getString(R.string.wind) + " " + windNowStr + "m/c");

        TextView descripNow = main.findViewById(R.id.description);
        descripNow.setText(descripNowStr);
    }
    private String ceil(String text) {
        //Округляем температуру
        String tempPlus;
        if ((int) Math.ceil(Double.parseDouble(text)) > 0) {
            tempPlus = "+" + (int) Math.ceil(Double.parseDouble(text)) + "C";
        } else tempPlus = "-" + (int) Math.ceil(Double.parseDouble(text)) + "C";
        return tempPlus;
    }
}
