package com.bsms.securityapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bsms.securityapp.data.ResponseDatabaseHelper;
import com.bsms.securityapp.model.Responses;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class AlarmResponseActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MAX_PHOTOS = 10;

    private ResponseDatabaseHelper dbHelper;
    private int editId = -1;
    private List<String> photoPaths = new ArrayList<>();
    private LinearLayout layoutThumbnails;
    private File currentPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        }

        setContentView(R.layout.activity_alarm_response);

        dbHelper = new ResponseDatabaseHelper(this);



        EditText inputDateTime = findViewById(R.id.input_datetime);
        AutoCompleteTextView alarmTypeDropdown = findViewById(R.id.input_alarm_type);
        AutoCompleteTextView departmentDropdown = findViewById(R.id.input_department_company);
        AutoCompleteTextView roleDropdown = findViewById(R.id.input_role);
        EditText inputLocation = findViewById(R.id.input_location);
        EditText inputName = findViewById(R.id.input_name);
        EditText inputContact = findViewById(R.id.input_contact);
        EditText inputTimeLeaving = findViewById(R.id.input_time_leaving);
        EditText inputIncident = findViewById(R.id.input_incident);
        EditText inputAction = findViewById(R.id.input_action);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnBack = findViewById(R.id.btnBack);

        // 📸 New photo UI elements
        Button btnAddPhoto = findViewById(R.id.btn_add_photo);
        layoutThumbnails = findViewById(R.id.layout_thumbnails);

        btnBack.setOnClickListener(v -> finish());

        // Set current date/time
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        inputDateTime.setText(sdf.format(new Date()));

        // Dropdowns
        alarmTypeDropdown.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Alarm", "Intruder", "Panic", "Fire", "Power Failure", "Medical", "Other"}));

        departmentDropdown.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Department of Education", "Hunter Water", "Ausgroup", "Ben's Security", "Other (type below)"}));

        roleDropdown.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"GA (General Assistant)", "Principal", "Teacher", "Manager", "Supervisor",
                        "Administrator", "Contractor", "Visitor", "Other (type below)"}));

        // 📷 Add photo capture button logic
        btnAddPhoto.setOnClickListener(v -> {
            if (photoPaths.size() >= MAX_PHOTOS) {
                Toast.makeText(this, "You can only add up to 10 photos", Toast.LENGTH_SHORT).show();
                return;
            }
            dispatchTakePictureIntent();
        });

        // 🧩 Check if editing existing record
        Intent intent = getIntent();
        if (intent.hasExtra("edit_report_id")) {
            editId = intent.getIntExtra("edit_report_id", -1);
            if (editId > -1) {
                List<Responses> allResponses = dbHelper.getAllResponses();
                for (Responses r : allResponses) {
                    if (r.getId() == editId) {
                        inputDateTime.setText(r.getDate());
                        inputLocation.setText(r.getLocation());
                        alarmTypeDropdown.setText(extract(r.getDetails(), "Alarm Type:"), false);
                        departmentDropdown.setText(extract(r.getDetails(), "Department:"), false);
                        roleDropdown.setText(extract(r.getDetails(), "Role:"), false);
                        inputName.setText(extract(r.getDetails(), "Name:"));
                        inputContact.setText(extract(r.getDetails(), "Contact:"));
                        inputTimeLeaving.setText(extract(r.getDetails(), "Time Leaving:"));
                        inputIncident.setText(extract(r.getDetails(), "Incident:"));
                        inputAction.setText(extract(r.getDetails(), "Action:"));

                        // 🖼️ Load existing photo thumbnails
                        String photoStr = r.getPhotoPaths();
                        if (photoStr != null && !photoStr.isEmpty()) {
                            String[] paths = photoStr.split(",");
                            for (String path : paths) {
                                photoPaths.add(path.trim());
                                addThumbnail(path.trim());
                            }
                        }
                        break;
                    }
                }
            }
        }

        // 💾 Save / update
        btnSave.setOnClickListener(v -> {
            String dateTime = inputDateTime.getText().toString();
            String alarmType = alarmTypeDropdown.getText().toString();
            String department = departmentDropdown.getText().toString();
            String role = roleDropdown.getText().toString();
            String location = inputLocation.getText().toString();
            String name = inputName.getText().toString();
            String contact = inputContact.getText().toString();
            String timeLeaving = inputTimeLeaving.getText().toString();
            String incident = inputIncident.getText().toString();
            String action = inputAction.getText().toString();

            String photoCSV = TextUtils.join(",", photoPaths);

            if (editId > -1) {
                dbHelper.getWritableDatabase().execSQL(
                        "UPDATE alarm_responses SET datetime=?, alarm_type=?, department=?, role=?, " +
                                "location=?, name=?, contact=?, time_leaving=?, incident=?, action=?, photo_paths=? WHERE id=?",
                        new Object[]{dateTime, alarmType, department, role, location, name, contact,
                                timeLeaving, incident, action, photoCSV, editId});
                Toast.makeText(this, "Report updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.insertResponse(
                        dateTime, alarmType, department, role, location, name, contact,
                        timeLeaving, incident, action, photoCSV);
                Toast.makeText(this, success ? "Response saved successfully" : "Error saving response",
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        });
    }

    // Helper: Capture photo intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                currentPhotoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        currentPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "BSMS_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && currentPhotoFile != null) {
            String photoPath = currentPhotoFile.getAbsolutePath();
            photoPaths.add(photoPath);
            addThumbnail(photoPath);
        }
    }

    private void addThumbnail(String path) {
        ImageView thumbnail = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
        params.setMargins(8, 8, 8, 8);
        thumbnail.setLayoutParams(params);
        thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        thumbnail.setImageBitmap(bitmap);
        layoutThumbnails.addView(thumbnail);
    }

    private String extract(String text, String key) {
        int start = text.indexOf(key);
        if (start == -1) return "";
        int end = text.indexOf("|", start + key.length());
        if (end == -1) end = text.length();
        return text.substring(start + key.length(), end).trim();
    }
}
