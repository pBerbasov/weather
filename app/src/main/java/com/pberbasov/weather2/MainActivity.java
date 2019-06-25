package com.pberbasov.weather2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ExpandableListView lvData;
    DB db;
    Cursor cursor;
    SimpleCursorTreeAdapter sctAdapter;
    public final static String BROADCAST_ACTION = "com.pberbasov.weather2";

    // формируем столбцы сопоставления

    BroadcastReceiver br;

    TextView tempNow;
    String tempNowStr;

    TextView pressureNow;
    String pressureNowStr;

    TextView humidityNow;
    String humidityNowStr;

    TextView windNow;
    String windNowStr;

    TextView descripNow;
    String descripNowStr;

    TextView weatherCity;
    String weatherCityStr;

    private static final int PERMISSION_REQUEST_CODE = 10;
    private LocationManager locationManager;
    private String provider;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private SharedPreferences mSettings;

    String latitude;
    String longitude;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences(LATITUDE, Context.MODE_PRIVATE);
        mSettings = getSharedPreferences(LONGITUDE, Context.MODE_PRIVATE);
        latitude = mSettings.getString(LATITUDE, "51.5085300");
        longitude = mSettings.getString(LONGITUDE, "-0.1257400");

        startService(new Intent(this, WeatherService.class)
                .putExtra("latitude", latitude)
                .putExtra("longitude", longitude));
        setContentView(R.layout.activity_main);
        TextView refline = findViewById(R.id.weatherGPS);
        db = new DB(this);
        db.open();
        getAdapter();
        getBrodcast();
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);
        refline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onGPS();
            }
        });
    }

    private void getBrodcast() {
        br = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            public void onReceive(Context context, Intent intent) {
                //Получаем данные из сервиса, если ок, загрузка прошла успешно.
                int ok = intent.getIntExtra("ok", 1);
                if (ok == 0) {
                    //убираем прогресс бар
                    progress = findViewById(R.id.progres);
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
                    applyGPS(latitude, longitude);

                    //Добавляем или обновляем записи в базе данных
                    int updateWeather = db.uppRec(date, temp, wind, description);
                    if (updateWeather < 1) db.addRec(date, temp, wind, description);
                } else
                    //Повторно запускаем сервис если произошла ошибка загрузки данных
                    startService(new Intent(MainActivity.this, WeatherService.class)
                            .putExtra("latitude", latitude)
                            .putExtra("longitude", longitude));
                cursor.requery();

                //Обновляем данные в верхнем центральном окне
                weatherCity = findViewById(R.id.weatherCity);
                weatherCity.setText(getString(R.string.weather_city) + " " + weatherCityStr);

                tempNow = findViewById(R.id.temp);
                tempNow.setText(tempNowStr);

                pressureNow = findViewById(R.id.pressure);
                pressureNow.setText(getString(R.string.pressure) + " " + pressureNowStr);

                humidityNow = findViewById(R.id.humidity);
                humidityNow.setText(getString(R.string.humidity) + " " + humidityNowStr + "%");

                windNow = findViewById(R.id.wind);
                windNow.setText(getString(R.string.wind) + " " + windNowStr + "m/c");

                descripNow = findViewById(R.id.description);
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
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(br);
        db.close();
        cursor.close();
    }

    @SuppressLint("SimpleDateFormat")
    private void getAdapter() {
        //Создаем или обновляем верхнее дерево
        String[] group = new String[6];
        for (int i = 0; i < 6; i++) {
            group[i] = new SimpleDateFormat("dd.MM").format(new Date().getTime() + i * 86400000);
            int s = db.dataRec(String.valueOf(i + 1), group[i]);
            if (s < 1) db.addRec2(group);
            ;
        }
        cursor = db.getDateData();
        startManagingCursor(cursor);
        String[] groupFrom = {DB.COLUMN_DATE};
        int[] groupTo = {android.R.id.text1};
        // сопоставление данных и View для элементов
        String[] childFrom = {DB.COLUMN_TEMP, DB.COLUMN_TIME_WEATHER, DB.COLUMN_WIND, DB.COLUMN_DESC};
        int[] childTo = {R.id.temp_item, R.id.time, R.id.wind_item, R.id.desc_item};

        sctAdapter = new MyAdapter(this, cursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, R.layout.my_list_item, childFrom,
                childTo);
        lvData = findViewById(R.id.wethertData);
        lvData.setAdapter(sctAdapter);
    }

    class MyAdapter extends SimpleCursorTreeAdapter {

        public MyAdapter(Context context, Cursor cursor, int groupLayout,
                         String[] groupFrom, int[] groupTo, int childLayout,
                         String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo,
                    childLayout, childFrom, childTo);
        }

        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // получаем курсор по элементам для конкретной группы
            int idColumn = groupCursor.getColumnIndex(DB.COLUMN_DATE);
            return db.getAllData(groupCursor.getString(idColumn));
        }
    }


    private void requestLocation() {
        // Если пермиссии все-таки нет - просто выйдем, приложение не имеет смысла
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // Получим наиболее подходящий провайдер геолокации по критериям
        // Но можно и самому назначать, какой провайдер использовать
        // В основном это LocationManager.GPS_PROVIDER или LocationManager.NETWORK_PROVIDER
        // Но может быть и LocationManager.PASSIVE_PROVIDER (когда координаты уже кто-то недавно получил)
        provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            // Будем получать геоположение через каждые час или каждые 10 километров
            locationManager.requestLocationUpdates(provider, 3600000, 10000, new LocationListener() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onLocationChanged(Location location) {
                    // Широта
                    String latitude = Double.toString(location.getLatitude());
                    // Долгота
                    String longitude = Double.toString(location.getLongitude());
                    // Точность

                    startService(new Intent(MainActivity.this, WeatherService.class)
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

    private void applyGPS(String latitude, String longitude) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(LATITUDE, latitude);
        editor.putString(LONGITUDE, longitude);
        editor.apply();
    }

    private void onGPS() {
        progress.setVisibility(View.VISIBLE);
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Запросим координаты
            requestLocation();
        } else {
            // Пермиссии нет, будем запрашивать у пользователя
            requestLocationPermissions();
        }     // Обработка нажатия
    }

    // Запрос пермиссии для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            // Запросим эти две пермиссии у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }


    // Это результат запроса у пользователя пермиссии
    @Override
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

    //Создаем меню поиск
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.gps:
                onGPS();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //действие в меню поиск после нажатия кнопки ПОИСК
    @Override
    public boolean onQueryTextSubmit(String query) {
        startService(new Intent(MainActivity.this, WeatherService.class)
                .putExtra("latitude", "")
                .putExtra("longitude", "")
                .putExtra("city", query)
        );
        progress.setVisibility(View.VISIBLE);
        return false;
    }

    //действия при наборе в меню поиск
    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        return false;
    }
}

