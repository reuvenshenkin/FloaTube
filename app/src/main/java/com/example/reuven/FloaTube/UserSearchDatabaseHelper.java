package com.example.reuven.FloaTube;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by reuven on 23/05/2015.
 */
public class UserSearchDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_USER_SEARCH ="UserSearch";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SEARCH = "search";

    private static final String DATABASE_NAME = "DBUserSearch";

    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table IF NOT EXISTS "+TABLE_USER_SEARCH+"("+COLUMN_ID +" integer primary key autoincrement,"+COLUMN_SEARCH+" text not null);";

    public UserSearchDatabaseHelper(Context context) {
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
