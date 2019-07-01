package com.pberbasov.weather2;

import android.annotation.SuppressLint;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.SimpleCursorTreeAdapter;

import java.util.HashMap;

public class MySimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {
    private MainActivity mActivity;
    private final HashMap<Integer, Integer> mGroupMap;
    private String date;
    DB db;

    // Please Note: Here cursor is not provided to avoid querying on main
    // thread.
    @SuppressLint("UseSparseArrays")
    MySimpleCursorTreeAdapter(Context context, Cursor cursor, int groupLayout,
                              String[] groupFrom, int[] groupTo, int childLayout,
                              String[] childrenFrom, int[] childrenTo, DB db) {

        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout,
                childrenFrom, childrenTo);
        mActivity = (MainActivity) context;
        mGroupMap = new HashMap<>();
        this.db = db;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        // Logic to get the child cursor on the basis of selected group.
        int groupPos = groupCursor.getPosition();
        int groupId = groupCursor.getColumnIndex(DB.COLUMN_DATE);
        date = groupCursor.getString(groupId);
        mGroupMap.put(groupId, groupPos);
        Cursor s = db.getAllData(date);
        Log.i("LOG", "group_cursor" + groupId);
        return s;
    }


    HashMap<Integer, Integer> getGroupMap() {
        return mGroupMap;
    }

    String getDate() {
        return this.date;
    }
}
