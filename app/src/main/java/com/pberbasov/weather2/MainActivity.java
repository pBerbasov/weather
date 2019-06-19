package com.pberbasov.weather2;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SimpleCursorTreeAdapter scAdapter;
    ExpandableListView lvData;
    DB db;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    String wind = intent.getStringExtra("wind");
                    String description = intent.getStringExtra("desriptionItem");
                    int s=db.uppRec(date, temp, wind,description);
                    if (s<1) db.addRec(date,temp,wind,description);
                    tempNowStr=ceil(intent.getStringExtra("tempNow"));
                    pressureNowStr=intent.getStringExtra("pressureNow");
                    humidityNowStr=intent.getStringExtra("humidityNow");
                    windNowStr=intent.getStringExtra("windNow");
                    descripNowStr=intent.getStringExtra("weatherDescrip");
                }
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

private void getAdapter() {
    String[] group = new String[] { "19.06", "20.06", "21.06", "22.06","23.06","24.06"};
    db.addRec2(group);
    Cursor cursor =db.getDateData();
    startManagingCursor(cursor);
    String[] groupFrom={ DB.COLUMN_DATE };
    int[] groupTo = { android.R.id.text1 };
    // сопоставление данных и View для элементов
    String[] childFrom = { DB.COLUMN_TEMP, DB.COLUMN_TIME_WEATHER, DB.COLUMN_WIND, DB.COLUMN_DESC };
    int[] childTo = { R.id.temp_item, R.id.time , R.id.wind_item, R.id.desc_item};

    SimpleCursorTreeAdapter sctAdapter = new MyAdapter(this, cursor,
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
}
