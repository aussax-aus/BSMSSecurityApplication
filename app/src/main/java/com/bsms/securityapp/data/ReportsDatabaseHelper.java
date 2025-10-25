package com.bsms.securityapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.util.List;
import java.util.ArrayList;

import com.bsms.securityapp.model.Report; // ✅ Make sure you have this model class

public class ReportsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reports.db";
    private static final int DATABASE_VERSION = 4;

    public ReportsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE reports (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "site_name TEXT, " +
                "site_address TEXT, " +
                "date TEXT, " +
                "officer_name TEXT, " +
                "start_time TEXT, " +
                "finish_time TEXT, " +
                "notes TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reports");
        onCreate(db);
    }

    // Optional safety net for version rollback
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reports");
        onCreate(db);
    }

    // ✅ Insert a new report
    public boolean insertReport(String siteName, String siteAddress, String date,
                                String officerName, String startTime, String finishTime, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("site_name", siteName);
        values.put("site_address", siteAddress);
        values.put("date", date);
        values.put("officer_name", officerName);
        values.put("start_time", startTime);
        values.put("finish_time", finishTime);
        values.put("notes", notes);

        long result = db.insert("reports", null, values);
        return result != -1;
    }

    // ✅ Return all reports as readable strings
    public List<String> getAllReports() {
        List<String> reportsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM reports ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String report =
                        "Site: " + cursor.getString(cursor.getColumnIndexOrThrow("site_name")) + "\n" +
                                "Address: " + cursor.getString(cursor.getColumnIndexOrThrow("site_address")) + "\n" +
                                "Date: " + cursor.getString(cursor.getColumnIndexOrThrow("date")) + "\n" +
                                "Officer: " + cursor.getString(cursor.getColumnIndexOrThrow("officer_name")) + "\n" +
                                "Start: " + cursor.getString(cursor.getColumnIndexOrThrow("start_time")) + "\n" +
                                "Finish: " + cursor.getString(cursor.getColumnIndexOrThrow("finish_time")) + "\n" +
                                "Notes: " + cursor.getString(cursor.getColumnIndexOrThrow("notes"));
                reportsList.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reportsList;
    }

    // ✅ Return all reports as Report objects (for custom adapter)
    public List<Report> getAllReportsObjects() {
        List<Report> reportsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM reports ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Report report = new Report();
                report.setSiteName(cursor.getString(cursor.getColumnIndexOrThrow("site_name")));
                report.setSiteAddress(cursor.getString(cursor.getColumnIndexOrThrow("site_address")));
                report.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                report.setOfficerName(cursor.getString(cursor.getColumnIndexOrThrow("officer_name")));
                report.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow("start_time")));
                report.setFinishTime(cursor.getString(cursor.getColumnIndexOrThrow("finish_time")));
                report.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
                reportsList.add(report);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reportsList;
    }
}
