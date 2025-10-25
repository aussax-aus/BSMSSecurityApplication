package com.bsms.securityapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bsms.securityapp.data.ReportsDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportFormActivity extends AppCompatActivity {

    private EditText etSiteName, etSiteAddress, etDate, etOfficerName, etStartTime, etFinishTime, etNotes;
    private Button btnSubmitReport;
    private ReportsDatabaseHelper dbHelper;

    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // return to previous screen
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        // Initialize database helper
        dbHelper = new ReportsDatabaseHelper(this);

        // Link UI elements
        etSiteName = findViewById(R.id.etSiteName);
        etSiteAddress = findViewById(R.id.etSiteAddress);
        etDate = findViewById(R.id.etDate);
        etOfficerName = findViewById(R.id.etOfficerName);
        etStartTime = findViewById(R.id.etStartTime);
        etFinishTime = findViewById(R.id.etFinishTime);
        etNotes = findViewById(R.id.etNotes);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        // Date picker
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(
                    ReportFormActivity.this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        // Start time picker
        etStartTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(
                    ReportFormActivity.this,
                    (TimePicker view, int hourOfDay, int minute) ->
                            etStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            timePicker.show();
        });

        // Finish time picker
        etFinishTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(
                    ReportFormActivity.this,
                    (TimePicker view, int hourOfDay, int minute) ->
                            etFinishTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            timePicker.show();
        });

        // Save report
        btnSubmitReport.setOnClickListener(v -> saveReport());
    }

    private void saveReport() {
        String siteName = etSiteName.getText().toString().trim();
        String siteAddress = etSiteAddress.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String officerName = etOfficerName.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String finishTime = etFinishTime.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Validate required fields
        if (siteName.isEmpty() || date.isEmpty() || officerName.isEmpty()) {
            Toast.makeText(this, "Please complete all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = dbHelper.insertReport(siteName, siteAddress, date, officerName, startTime, finishTime, notes);

        if (inserted) {
            Toast.makeText(this, "Report saved successfully!", Toast.LENGTH_LONG).show();

            // 🧭 Automatically go back after saving
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else {
            Toast.makeText(this, "Error saving report.", Toast.LENGTH_LONG).show();
        }
    }


    private void clearForm() {
        etSiteName.setText("");
        etSiteAddress.setText("");
        etDate.setText("");
        etOfficerName.setText("");
        etStartTime.setText("");
        etFinishTime.setText("");
        etNotes.setText("");
    }
}
