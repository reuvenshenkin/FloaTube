package com.example.reuven.FloaTube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reuven on 13/07/2015.
 */
public class HistoryDataSource {
    private SQLiteDatabase database;
    private HistoryDatabaseHelper dbHelper;
    private String[] allColumns = { HistoryDatabaseHelper.COLUMN_ID, HistoryDatabaseHelper.COLUMN_TITLE , HistoryDatabaseHelper.COLUMN_THUMBNAIL_URL, HistoryDatabaseHelper.COLUMN_VIDEO_ID };

    public HistoryDataSource(Context context) {
        dbHelper = new HistoryDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public VideoItem createRow(VideoItem videoItem) {


        boolean isAlreadyCreated = false;
        for (VideoItem videoItem1 : getAllRows()) {
            if (videoItem.getId().equals(videoItem1.getId())) {
                isAlreadyCreated = true;
            }
        }
        if ( !isAlreadyCreated) {
            ContentValues values = new ContentValues();
            values.put(HistoryDatabaseHelper.COLUMN_TITLE, videoItem.getTitle());
            values.put(HistoryDatabaseHelper.COLUMN_THUMBNAIL_URL, videoItem.getThumbnailURL());
            values.put(HistoryDatabaseHelper.COLUMN_VIDEO_ID, videoItem.getId());
            long insertId = database.insert(HistoryDatabaseHelper.TABLE_SONGS_PLAYED_HISTORY, null,
                    values);
            Cursor cursor = database.query(HistoryDatabaseHelper.TABLE_SONGS_PLAYED_HISTORY,
                    allColumns, HistoryDatabaseHelper.COLUMN_ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            VideoItem item = cursorToVideoItem(cursor);
            cursor.close();
            return item;
        }
        return null;


    }

    public void deleteRow(VideoItem videoItem) {
        long id = videoItem.getRowId();
        //System.out.println("Comment deleted with id: " + id);
        database.delete(HistoryDatabaseHelper.TABLE_SONGS_PLAYED_HISTORY, HistoryDatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }
    public void deleteAllRows(){
        for (VideoItem videoItem :getAllRows()){
            deleteRow(videoItem);
        }
    }

    public List<VideoItem> getAllRows() {
        List<VideoItem> videoItems = new ArrayList<VideoItem>();

        Cursor cursor = database.query(HistoryDatabaseHelper.TABLE_SONGS_PLAYED_HISTORY,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            VideoItem videoItem = cursorToVideoItem(cursor);
            videoItems.add(0,videoItem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return videoItems;
    }
    // making VideoIten object from row in the table
    private VideoItem cursorToVideoItem(Cursor cursor) {
        VideoItem videoItem = new VideoItem();
        videoItem.setRowId(cursor.getLong(0));
        videoItem.setTitle(cursor.getString(1));
        videoItem.setThumbnailURL(cursor.getString(2));
        videoItem.setId(cursor.getString(3));
        return videoItem;
    }
}
