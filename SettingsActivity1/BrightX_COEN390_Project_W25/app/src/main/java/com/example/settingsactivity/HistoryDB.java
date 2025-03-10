package com.example.settingsactivity;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryDB {

    private static HistoryDB instance;
    private SQLiteDatabase db;
    private static final String DB_NAME = "DB_HISTORY";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "HISTORY";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_RECOMMENDATION = "recommendation";
    private static final String COLUMN_DATA1 = "data1";
    private static final String COLUMN_DATA2 = "data2";


    // Singleton pattern for database instance
    public static synchronized HistoryDB getInstance(Context context) {
        if (instance == null) {
            instance = new HistoryDB(context.getApplicationContext());
        }
        return instance;
    }

    HistoryDB(Context context) {
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();
    }

    public void addRow(String recommendation, String data1, String data2) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, getCurrentTimestamp());
        values.put(COLUMN_RECOMMENDATION, recommendation);
        values.put(COLUMN_DATA1, data1); // Inserting data1
        values.put(COLUMN_DATA2, data2); // Inserting data2

        try {
            db.insert(TABLE_NAME, null, values);
        } catch (SQLiteException e) {
            // Handle exception or notify user
        }
    }


    public Cursor retrieveHistory() {
        return db.query(TABLE_NAME, new String[] { COLUMN_TIMESTAMP, COLUMN_RECOMMENDATION },
                null, null, null, null, COLUMN_TIMESTAMP + " DESC");
    }

    public void deleteHistory(String timestamp) {
        String whereClause = COLUMN_TIMESTAMP + "=?";
        String[] whereArgs = { timestamp };
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }


    public void clearHistory() {
        db.delete(TABLE_NAME, null, null);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void close() {
        db.close();
    }

    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        public CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_TIMESTAMP + " TEXT PRIMARY KEY, " +
                    COLUMN_RECOMMENDATION + " TEXT, " +
                    COLUMN_DATA1 + " TEXT, " + // Added new column
                    COLUMN_DATA2 + " TEXT);"; // Added new column
            db.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}




