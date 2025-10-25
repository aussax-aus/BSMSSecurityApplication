package com.bsms.securityapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bsms.securityapp.model.Responses;

import java.util.ArrayList;
import java.util.List;

public class ResponseDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bsms.db";
    private static final int DATABASE_VERSION = 3; // ✅ bumped version for new column
    private static final String TABLE_NAME = "alarm_responses";

    public ResponseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ✅ If upgrading from an old version, add column instead of dropping all data
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN photo_paths TEXT");
            } catch (Exception e) {
                // fallback if table doesn't exist yet
                createTable(db);
            }
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            createTable(db);
        }
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "datetime TEXT, " +
                "alarm_type TEXT, " +
                "department TEXT, " +
                "role TEXT, " +
                "location TEXT, " +
                "name TEXT, " +
                "contact TEXT, " +
                "time_leaving TEXT, " +
                "incident TEXT, " +
                "action TEXT, " +
                "photo_paths TEXT)"); // ✅ new column added
    }

    // ✅ Save all fields + optional photo paths
    public boolean insertResponse(String datetime, String alarm_type, String department, String role,
                                  String location, String name, String contact, String time_leaving,
                                  String incident, String action, String photoPaths) {

        SQLiteDatabase db = this.getWritableDatabase();
        createTable(db);

        ContentValues values = new ContentValues();
        values.put("datetime", datetime);
        values.put("alarm_type", alarm_type);
        values.put("department", department);
        values.put("role", role);
        values.put("location", location);
        values.put("name", name);
        values.put("contact", contact);
        values.put("time_leaving", time_leaving);
        values.put("incident", incident);
        values.put("action", action);
        values.put("photo_paths", photoPaths); // ✅ Save photo CSV string

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    // ✅ Fetch all responses including photo paths
    public List<Responses> getAllResponses() {
        List<Responses> reports = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        createTable(db);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Responses report = new Responses();
                report.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                report.setDate(cursor.getString(cursor.getColumnIndexOrThrow("datetime")));
                report.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                report.setDetails("Alarm Type: " + cursor.getString(cursor.getColumnIndexOrThrow("alarm_type")) +
                        " | Department: " + cursor.getString(cursor.getColumnIndexOrThrow("department")) +
                        " | Role: " + cursor.getString(cursor.getColumnIndexOrThrow("role")) +
                        " | Name: " + cursor.getString(cursor.getColumnIndexOrThrow("name")) +
                        " | Contact: " + cursor.getString(cursor.getColumnIndexOrThrow("contact")) +
                        " | Time Leaving: " + cursor.getString(cursor.getColumnIndexOrThrow("time_leaving")) +
                        " | Incident: " + cursor.getString(cursor.getColumnIndexOrThrow("incident")) +
                        " | Action: " + cursor.getString(cursor.getColumnIndexOrThrow("action")));

                // ✅ Include photo paths and compute photo count
                int colIndex = cursor.getColumnIndex("photo_paths");
                if (colIndex != -1) {
                    String photos = cursor.getString(colIndex);
                    report.setPhotoPaths(photos);
                    
                    // Count non-empty photo paths
                    int count = 0;
                    if (photos != null && !photos.trim().isEmpty()) {
                        String[] paths = photos.split(",");
                        for (String path : paths) {
                            if (path != null && !path.trim().isEmpty()) {
                                count++;
                            }
                        }
                    }
                    report.setPhotoCount(count);
                } else {
                    report.setPhotoPaths("");
                    report.setPhotoCount(0);
                }

                reports.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reports;
    }

    // ✅ Export all as text (including photo paths)
    public List<String> getAllReportsAsText() {
        List<String> reports = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        createTable(db);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    sb.append(cursor.getColumnName(i))
                            .append(": ")
                            .append(cursor.getString(i))
                            .append("\n");
                }
                reports.add(sb.toString());
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reports;
    }

    public void deleteAllResponses() {
        SQLiteDatabase db = this.getWritableDatabase();
        createTable(db);
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}
