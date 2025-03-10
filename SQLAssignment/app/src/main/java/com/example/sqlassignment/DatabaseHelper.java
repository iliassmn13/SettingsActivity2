package com.example.sqlassignment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context = null;
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, Config.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + Config.TABLE_NAME + " ("
                + Config.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // key ensures uniqueness between multiple rows
                + Config.COLUMN_SURNAME + " TEXT,"
                + Config.COLUMN_NAME + " TEXT,"
                + Config.COLUMN_STUDENT_ID + " INTEGER,"
                + Config.COLUMN_GPA + " REAL, "+
                Config.COLUMN_DELETED + " BOOLEAN DEFAULT 0)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_NAME + ";");
            onCreate(db);
        }
    }

    public long addProfile(Profiles profiles) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Config.COLUMN_NAME, profiles.getName());
        values.put(Config.COLUMN_SURNAME, profiles.getSurname());
        values.put(Config.COLUMN_GPA, profiles.getGpa());
        values.put(Config.COLUMN_STUDENT_ID, profiles.getStudentId());

        try {
            id = db.insertOrThrow(Config.TABLE_NAME, null, values);
        } catch (SQLException e) {
            Toast.makeText(context, "Insert Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
        return id;
    }
    public List<Profiles> getAllProfiles(String query) {
        List<Profiles> profilesList = new ArrayList<Profiles>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ID));
                        @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NAME));
                        @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SURNAME));
                        @SuppressLint("Range") double gpa = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_GPA));
                        @SuppressLint("Range") int studentId = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STUDENT_ID));
                        @SuppressLint("Range") boolean deleted = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_DELETED)) > 0;
                        Profiles profiles = new Profiles(id, surname, name, studentId, gpa,deleted);

                        profilesList.add(profiles);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Get Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return profilesList;
    }

    // Check if the profile id exists already to avoid duplicate id
    public boolean isProfileIdExists(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT 1 FROM " + Config.TABLE_NAME + " WHERE " + Config.COLUMN_STUDENT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        boolean exists = cursor.moveToFirst();

        cursor.close();
        db.close();
        return exists;
    }

    // Sorting functions for profiles
    public List<Profiles> getProfilesSortedByID() {
        return getAllProfiles("SELECT * FROM " + Config.TABLE_NAME + " ORDER BY " + Config.COLUMN_STUDENT_ID + " ASC");
    }

    public List<Profiles> getProfilesSortedBySurname() {
        return getAllProfiles("SELECT * FROM " + Config.TABLE_NAME + " ORDER BY " + Config.COLUMN_SURNAME + " ASC");
    }

    // Update the profile deleted status in the database
    public void updateProfileDeletedStatus(int studentId, boolean deleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Config.COLUMN_DELETED, deleted ? 1 : 0);
        db.update(Config.TABLE_NAME, values, Config.COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
        db.close();
    }

}


