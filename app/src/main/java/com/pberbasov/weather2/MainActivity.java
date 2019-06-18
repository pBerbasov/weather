package com.pberbasov.weather2;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    SimpleCursorAdapter scAdapter;
    ListView lvData;
    DB db;

    public final static String BROADCAST_ACTION = "com.pberbasov.weather2";

    // формируем столбцы сопоставления
    String[] from = new String[] { DB.COLUMN_DATE_WEATHER, DB.COLUMN_TIME_WEATHER, DB.COLUMN_TEMP };
    int[] to = new int[] { R.id.weatherDate,R.id.weatherTime, R.id.weatherTemp };

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startService(new Intent(this, WeatherService.class));
        setContentView(R.layout.activity_main);
        db = new DB(this);
        db.open();
        getAdapter();
        br = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            public void onReceive(Context context, Intent intent) {
                int ok =  intent.getIntExtra("ok",1);
                if(ok==0) {
                    String temp = ceil(intent.getStringExtra("temp"));
                    String date = intent.getStringExtra("date");
                    Log.d("LOG", date + "-" + temp);
                    int s=db.uppRec(date, temp);
                    if (s<1) db.addRec(date,temp);
                    tempNowStr=ceil(intent.getStringExtra("tempNow"));
                    pressureNowStr=intent.getStringExtra("pressureNow");
                    humidityNowStr=intent.getStringExtra("humidityNow");
                    windNowStr=intent.getStringExtra("windNow");
                    descripNowStr=intent.getStringExtra("weatherDescrip");
                }
                Objects.requireNonNull(LoaderManager.getInstance(MainActivity.this).getLoader(0)).forceLoad();
                tempNow=findViewById(R.id.temp);
                tempNow.setText(tempNowStr);

                pressureNow=findViewById(R.id.pressure);
                pressureNow.setText(getString(R.string.pressure)+" "+pressureNowStr);

                humidityNow=findViewById(R.id.humidity);
                humidityNow.setText(getString(R.string.humidity)+" "+humidityNowStr+"%");

                windNow=findViewById(R.id.wind);
                windNow.setText(getString(R.string.wind)+" "+windNowStr+ "m/c");

                descripNow=findViewById(R.id.description);
                descripNow.setText(descripNowStr);
            }
            private String ceil(String text){
                String tempPlus;
                if((int)Math.ceil(Double.parseDouble(text))>0){
                    tempPlus="+"+(int)Math.ceil(Double.parseDouble(text))+"C";
                }
                else tempPlus="-"+(int)Math.ceil(Double.parseDouble(text))+"C";
                return tempPlus;
            }
        };
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

private void getAdapter(){
    // создаем адаптер и настраиваем список
    scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
    lvData = findViewById(R.id.wethertData);
    lvData.setAdapter(scAdapter);
    lvData.setSelection(2);

    registerForContextMenu(lvData);

    // создаем лоадер для чтения данных
    LoaderManager.getInstance(MainActivity.this).initLoader(0,null, this);
}
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        scAdapter.swapCursor(null);
    }
    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            return db.getAllData();
        }

    }
}
