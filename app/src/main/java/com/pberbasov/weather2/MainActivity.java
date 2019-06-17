package com.pberbasov.weather2;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    SimpleCursorAdapter scAdapter;
    ListView lvData;
    DB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DB(this);
        db.open();

        // формируем столбцы сопоставления
        String[] from = new String[] { DB.COLUMN_DATE_WEATHER, DB.COLUMN_TIME_WEATHER, DB.COLUMN_TEMP };
        int[] to = new int[] { R.id.weatherDate,R.id.weatherTime, R.id.weatherTemp };

        // создаем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        lvData = (ListView) findViewById(R.id.wethertData);
        lvData.setAdapter(scAdapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(lvData);

        // создаем лоадер для чтения данных
        LoaderManager.getInstance(this).initLoader(0,null, this);
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
