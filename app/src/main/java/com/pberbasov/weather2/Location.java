package com.pberbasov.weather2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ProgressBar;

import static android.content.Context.LOCATION_SERVICE;
import static com.pberbasov.weather2.MainActivity.LATITUDE;
import static com.pberbasov.weather2.MainActivity.LONGITUDE;

public class Location {
    private static final int PERMISSION_REQUEST_CODE = 10;
    ProgressBar progress;
    private Activity mainActivity;
    private SharedPreferences mSettings;

    Location(Activity mainActivity, ProgressBar progress, SharedPreferences mSettings) {
        this.mainActivity = mainActivity;
        this.progress = progress;
        this.mSettings = mSettings;

    }

    void onGPS() {
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Запросим координаты
            requestLocation();
        } else {
            // Пермиссии нет, будем запрашивать у пользователя
            requestLocationPermissions();
        }     // Обработка нажатия
    }

    private void requestLocation() {
        // Если пермиссии все-таки нет - просто выйдем, приложение не имеет смысла
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationManager locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // Получим наиболее подходящий провайдер геолокации по критериям
        // Но можно и самому назначать, какой провайдер использовать
        // В основном это LocationManager.GPS_PROVIDER или LocationManager.NETWORK_PROVIDER
        // Но может быть и LocationManager.PASSIVE_PROVIDER (когда координаты уже кто-то недавно получил)
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            // Будем получать геоположение через каждые час или каждые 10 километров
            locationManager.requestLocationUpdates(provider, 3600000, 10000, new LocationListener() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onLocationChanged(android.location.Location location) {
                    // Широта
                    String latitude = Double.toString(location.getLatitude());
                    // Долгота
                    String longitude = Double.toString(location.getLongitude());
                    // Точность

                    mainActivity.startService(new Intent(mainActivity, WeatherService.class)
                            .putExtra("latitude", latitude)
                            .putExtra("longitude", longitude)
                            .putExtra("city", "")
                    );
                    //сохраняем в память GPS
                    applyGPS(latitude, longitude);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }
    }

    void applyGPS(String latitude, String longitude) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(LATITUDE, latitude);
        editor.putString(LONGITUDE, longitude);
        editor.apply();
    }

    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.CALL_PHONE)) {
            // Запросим эти две пермиссии у пользователя
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }


    // Это результат запроса у пользователя пермиссии
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        // Это та самая пермиссия, что мы запрашивали?
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Пермиссия дана
                requestLocation();
            }
        }
    }

}
