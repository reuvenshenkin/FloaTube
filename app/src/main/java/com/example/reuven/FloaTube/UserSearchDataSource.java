package com.example.reuven.FloaTube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reuven on 23/05/2015.
 */
public class UserSearchDataSource {
    private SQLiteDatabase database;
    private UserSearchDatabaseHelper dbHelper;
    private String[] allColumns = { UserSearchDatabaseHelper.COLUMN_ID, UserSearchDatabaseHelper.COLUMN_SEARCH };

    public UserSearchDataSource(Context context) {
        dbHelper = new UserSearchDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public UserSearch createUserSearch(String userSearch) {

        boolean isAlreadyCreated = false;
        for (UserSearch userSearch1 : getAllUserSearch()) {
            if (userSearch.equals(userSearch1.toString())) {
                isAlreadyCreated = true;
            }
        }
        if (!(userSearch.equals("")) && !isAlreadyCreated) {
            ContentValues values = new ContentValues();
            values.put(UserSearchDatabaseHelper.COLUMN_SEARCH, userSearch);
            long insertId = database.insert(UserSearchDatabaseHelper.TABLE_USER_SEARCH, null,
                    values);
            Cursor cursor = database.query(UserSearchDatabaseHelper.TABLE_USER_SEARCH,
                    allColumns, UserSearchDatabaseHelper.COLUMN_ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            UserSearch search = cursorToUserSearch(cursor);
            cursor.close();
            return search;
        }
        return null;
    }

    public void deleteUserSearch(UserSearch userSearch) {
        long id = userSearch.getId();
        //System.out.println("Comment deleted with id: " + id);
        database.delete(UserSearchDatabaseHelper.TABLE_USER_SEARCH, UserSearchDatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }
    public void deleteAllUserSearch(){
        for (UserSearch userSearch:getAllUserSearch()){
            deleteUserSearch(userSearch);
        }
    }

    public List<UserSearch> getAllUserSearch() {
        List<UserSearch> userSearches = new ArrayList<UserSearch>();

        Cursor cursor = database.query(UserSearchDatabaseHelper.TABLE_USER_SEARCH,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserSearch userSearch = cursorToUserSearch(cursor);
            userSearches.add(0,userSearch);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return userSearches;
    }
// making UserSearch from row in the table
    private UserSearch cursorToUserSearch(Cursor cursor) {
        UserSearch userSearch = new UserSearch();
        userSearch.setId(cursor.getLong(0));
        userSearch.setUserSearch(cursor.getString(1));
        return userSearch;
    }


}
