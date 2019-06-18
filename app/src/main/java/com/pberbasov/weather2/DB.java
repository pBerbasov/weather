package com.pberbasov.weather2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {

    private static final String DB_NAME = "weather";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "weatherOf5Day";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DATE_WEATHER = "dateWeather";
    public static final String COLUMN_TIME_WEATHER = "timeWeather";
    public static final String COLUMN_TEMP = "tempe";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATE + " text, " +
                    COLUMN_TEMP + " text" +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
      //  return mDB.query(DB_TABLE, null, null, null, null, null, null);
        String query ="SELECT *, strftime('%d.%m',DATE,'unixepoch')" +
                " AS "+COLUMN_DATE_WEATHER+", strftime('%H:%M',DATE,'unixepoch','localtime') " +
                "AS "+COLUMN_TIME_WEATHER+" " +
                "FROM " + DB_TABLE+" " +
                "where "+COLUMN_DATE+">strftime('%s','now')";
        return mDB.rawQuery(query, null);
    }
    public int uppRec(String date, String temp) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TEMP,temp);
        return mDB.update(DB_TABLE, cv, COLUMN_DATE+"= ?",
                new String[] { date });
    }
    public void addRec(String date, String temp) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TEMP, temp);
        mDB.insert(DB_TABLE, null, cv);
    }
    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
