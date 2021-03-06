package com.example.reuven.FloaTube;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by reuven on 13/07/2015.
 */
public class FavoritesDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_FAVORITES ="Favorites";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_THUMBNAIL_URL="thumbnailUrl";
    public static final String COLUMN_VIDEO_ID="videoId";

    private static final String DATABASE_NAME = "DBFavorites";

    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
   // private static final String DATABASE_CREATE = "create table IF NOT EXISTS "+TABLE_FAVORITES+"("+COLUMN_ID +" integer primary key autoincrement,"+COLUMN_TITLE+" text not null,"+COLUMN_THUMBNAIL_URL+" text not null,"+COLUMN_VIDEO_ID+" text not null);";
    private static final String DATABASE_CREATE = "create table IF NOT EXISTS "+TABLE_FAVORITES+"("+COLUMN_ID +" integer primary key autoincrement,"+COLUMN_TITLE+" text not null,"+COLUMN_THUMBNAIL_URL+" text not null,"+COLUMN_VIDEO_ID+" text not null);";
    public FavoritesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
