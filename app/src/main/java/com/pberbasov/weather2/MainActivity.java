package com.pberbasov.weather2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    ExpandableListView lvData;
    DB db;
    Cursor cursor;
    MySimpleCursorTreeAdapter sctAdapter;
    public final static String BROADCAST_ACTION = "com.pberbasov.weather2";

    BroadcastReceiver1 br;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    String latitude;
    String longitude;
    ProgressBar progress;
    Location GPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);

        SharedPreferences mSettings;
        mSettings = getSharedPreferences(LONGITUDE, Context.MODE_PRIVATE);
        latitude = mSettings.getString(LATITUDE, "51.5085300");
        longitude = mSettings.getString(LONGITUDE, "-0.1257400");
        progress = findViewById(R.id.progres);

        startService(new Intent(this, WeatherService.class)
                .putExtra("latitude", latitude)
                .putExtra("longitude", longitude));
        setContentView(R.layout.activity_main);

        TextView refline = findViewById(R.id.weatherGPS);
        db = new DB(this);
        db.open();

        getAdapter();
        GPS = new Location(this, progress, mSettings);
        progress = GPS.progress;
        getBrodcast();
        refline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GPS.onGPS();
            }
        });
    }

    private void getBrodcast() {
        br = new BroadcastReceiver1(this, cursor, latitude, longitude, db, progress, GPS);
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(br);
        db.close();
    }

    @SuppressLint("SimpleDateFormat")
    private void getAdapter() {
        //Создаем или обновляем верхнее дерево
        String[] group = new String[6];
        for (int i = 0; i < 6; i++) {
            group[i] = new SimpleDateFormat("dd.MM").format(new Date().getTime() + i * 86400000);
            int s = db.dataRec(String.valueOf(i + 1), group[i]);
            if (s < 1) db.addRec2(group);
        }
        cursor = db.getDateData();
        String[] groupFrom = {DB.COLUMN_DATE};
        int[] groupTo = {android.R.id.text1};
        // сопоставление данных и View для элементов
        String[] childFrom = {DB.COLUMN_TEMP, DB.COLUMN_TIME_WEATHER, DB.COLUMN_WIND, DB.COLUMN_DESC};
        int[] childTo = {R.id.temp_item, R.id.time, R.id.wind_item, R.id.desc_item};
        lvData = findViewById(R.id.wethertData);
        sctAdapter = new MySimpleCursorTreeAdapter(this, cursor,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, R.layout.my_list_item, childFrom,
                childTo, db);
        lvData.setAdapter(sctAdapter);
        Loader<Cursor> loader = LoaderManager.getInstance(this).getLoader(-1);
        if (loader != null && !loader.isReset()) {
            LoaderManager.getInstance(this).restartLoader(-1, null, this);
        } else {
            LoaderManager.getInstance(this).initLoader(-1, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        Log.d("LOG", "onCreateLoader for loader_id " + id);
        CursorLoader cl;
        cl = new MyCursorLoader(this, db);
        return cl;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.i("LOG", "bax");
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            return db.getDateData();
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
                GPS.onGPS();
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
        return false;
    }

    //действия при наборе в меню поиск
    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        return false;
    }
}

