package com.pberbasov.weather2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

    private static final String DB_NAME = "weather";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE_WEATHER = "weatherOf5Day";
    private static final String DB_TABLE_DAY = "weatherDate";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DATE_WEATHER = "dateWeather";
    public static final String COLUMN_TIME_WEATHER = "timeWeather";
    public static final String COLUMN_TEMP = "tempe";
    public static final String COLUMN_WIND = "wind";
    public static final String COLUMN_DESC = "descr";

    private static final String DB_CREATE_POGODA =
            "create table " + DB_TABLE_WEATHER + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATE + " text, " +
                    COLUMN_TEMP + " text, " +
                    COLUMN_WIND + " text, " +
                    COLUMN_DESC + " text" +
                    ");";
    private static final String DB_CREATE_DAY =
            "create table " + DB_TABLE_DAY + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATE + " text" +
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
        if (mDBHelper != null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_WEATHER
    public Cursor getAllData(String date) {
        String query = "SELECT *, strftime('%d.%m',DATE,'unixepoch','localtime')" +
                " AS " + COLUMN_DATE_WEATHER + ", strftime('%H:%M',DATE,'unixepoch','localtime') " +
                "AS " + COLUMN_TIME_WEATHER + " " +
                "FROM " + DB_TABLE_WEATHER + " " +
                "WHERE " + COLUMN_DATE + ">strftime('%s','now') AND " + COLUMN_DATE_WEATHER + "='" + date + "'";
        return mDB.rawQuery(query, null);
    }

    //получаем близжайшие 6 дат
    public Cursor getDateData() {
        String query = "SELECT * FROM " + DB_TABLE_DAY;
        return mDB.rawQuery(query, null);
    }

    //обновляем таблицу с погодой
    public int uppRec(String date, String temp, String wind, String desc) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TEMP, temp);
        cv.put(COLUMN_WIND, wind);
        cv.put(COLUMN_DESC, desc);
        return mDB.update(DB_TABLE_WEATHER, cv, COLUMN_DATE + "= ?",
                new String[]{date});
    }

    //обновляем таблицу с датами
    public int dataRec(String id, String date) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATE, date);
        return mDB.update(DB_TABLE_DAY, cv, COLUMN_ID + "= ?",
                new String[]{id});
    }

    //добавляем записи о погоде
    public void addRec(String date, String temp, String wind, String desc) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TEMP, temp);
        cv.put(COLUMN_WIND, wind);
        cv.put(COLUMN_DESC, desc);
        mDB.insert(DB_TABLE_WEATHER, null, cv);
    }

    //добавляем записи о датах
    public void addRec2(String... date) {
        ContentValues cv2 = new ContentValues();
        for (int i = 0; i < date.length; i++) {
            cv2.put(COLUMN_DATE, date[i]);
            mDB.insert(DB_TABLE_DAY, null, cv2);
        }
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
            db.execSQL(DB_CREATE_POGODA);
            db.execSQL(DB_CREATE_DAY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
