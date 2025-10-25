package com.bsms.securityapp;

import com.bsms.securityapp.data.ResponseDatabaseHelper;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private ResponseDatabaseHelper dbHelper;
    private Button btnExportAll, btnDeleteAll, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new ResponseDatabaseHelper(this);

        // Buttons
        btnExportAll = findViewById(R.id.btnExportAll);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnBack = findViewById(R.id.btnBack);

        // 🧾 EXPORT & SHARE BUTTON
        btnExportAll.setOnClickListener(v -> exportAndShareReports());

        // 🗑️ SAFE DELETE BUTTON
        btnDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete all responses?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        try {
                            dbHelper.deleteAllResponses();  // Deletes only the rows
                            Toast.makeText(SettingsActivity.this, "All responses deleted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(SettingsActivity.this, "Error deleting records: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 🔙 BACK BUTTON
        btnBack.setOnClickListener(v -> finish());
    }

    // 📦 EXPORT AND SHARE FUNCTION
    private void exportAndShareReports() {
        try {
            // 1️⃣ Get all report data from database
            List<String> reports = dbHelper.getAllReportsAsText(); // Make sure this method exists in your DatabaseHelper

            if (reports == null || reports.isEmpty()) {
                Toast.makeText(this, "No reports to export", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2️⃣ Create files in cache directory
            File pdfFile = new File(getCacheDir(), "reports.pdf");
            File txtFile = new File(getCacheDir(), "reports.txt");
            File csvFile = new File(getCacheDir(), "reports.csv");

            // 3️⃣ Write to text file
            FileWriter txtWriter = new FileWriter(txtFile);
            for (String r : reports) txtWriter.write(r + "\n\n");
            txtWriter.close();

            // 4️⃣ Write to CSV
            FileWriter csvWriter = new FileWriter(csvFile);
            csvWriter.write("Report Data\n");
            for (String r : reports) csvWriter.write("\"" + r.replace("\"", "'") + "\"\n");
            csvWriter.close();

            // 5️⃣ Write to PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            for (String r : reports) document.add(new Paragraph(r + "\n\n"));
            document.close();

            // 6️⃣ Prepare share intent
            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile));
            uris.add(FileProvider.getUriForFile(this, getPackageName() + ".provider", txtFile));
            uris.add(FileProvider.getUriForFile(this, getPackageName() + ".provider", csvFile));

            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType("*/*");
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Reports"));

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
