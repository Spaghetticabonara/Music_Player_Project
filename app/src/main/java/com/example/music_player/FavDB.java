package com.example.music_player;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "favdb";
    private static String TABLE_NAME = "favtable";
    public static String KEY_ID = "id";
    public static String ITEM_TITLE = "itemtitle";
    public static String FAV_STATUS = "favStatus";

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " TEXT, " + ITEM_TITLE + " TEXT, "
            + FAV_STATUS + " TEXT)";

    public FavDB (Context context) { super(context, DATABASE_NAME, null, DB_VERSION); }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //create empty table
    public void insertEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int x = 1; x < 11; x++) {
            cv.put(KEY_ID, x);
            cv.put(FAV_STATUS, "0");

            db.insert(TABLE_NAME, null, cv);
        }
    }
}
