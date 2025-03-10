package com.example.sqlassignment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class AccessDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "AccessDBHelper";
    private Context context = null;
    private static final int DATABASE_VERSION = 1;

    public AccessDBHelper(@Nullable Context context) {
        super(context, ConfigAccess.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + ConfigAccess.TABLE_NAME + " ("
                + ConfigAccess.COLUMN_ACCESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // key ensures uniqueness between multiple rows
                + ConfigAccess.COLUMN_STUDENT_ID + " INTEGER,"
                + ConfigAccess.COLUMN_ACCESS_TYPE + " TEXT,"
                + ConfigAccess.COLUMN_TIMESTAMP + " TEXT" +")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ConfigAccess.TABLE_NAME + ";");
            onCreate(db);
    }

    public List<Access> getAccessByStudentId(int studentId) {
        Log.d(TAG, "Fetching access events for student ID: " + studentId);
        List<Access> accessList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + ConfigAccess.TABLE_NAME + " WHERE " + ConfigAccess.COLUMN_STUDENT_ID + " = ? ORDER BY " + ConfigAccess.COLUMN_TIMESTAMP + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(ConfigAccess.COLUMN_ACCESS_ID));
                        String accessType = cursor.getString(cursor.getColumnIndexOrThrow(ConfigAccess.COLUMN_ACCESS_TYPE));
                        String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(ConfigAccess.COLUMN_TIMESTAMP));
                        accessList.add(new Access(id, studentId, accessType, timestamp));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while fetching access events", e);
            } finally {
                cursor.close();
            }
        } else {
            Log.e(TAG, "Cursor is null");
        }
        db.close();
        Log.d(TAG, "Access events fetched: " + accessList.size());
        return accessList;
    }

    public boolean addAccess(Access access) {
        Log.d(TAG, "Adding access event: " + access);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigAccess.COLUMN_STUDENT_ID, access.getStudentId());
        values.put(ConfigAccess.COLUMN_ACCESS_TYPE, access.getAccessType());
        values.put(ConfigAccess.COLUMN_TIMESTAMP, access.getTimestamp());

        long result = -1;
        try {
            result = db.insert(ConfigAccess.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error while adding access event", e);
        } finally {
            db.close();
        }
        boolean isSuccess = result != -1;
        Log.d(TAG, "Access event added: " + isSuccess);
        return isSuccess;
    }

}
