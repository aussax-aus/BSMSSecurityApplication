package com.bsms.securityapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bsms.securityapp.ReportFormActivity;
import com.bsms.securityapp.ViewReportsActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_alarm).setOnClickListener(v -> startActivity(new Intent(this, AlarmResponseActivity.class)));
        findViewById(R.id.btn_reports).setOnClickListener(v -> startActivity(new Intent(this, ViewResponsesActivity.class)));
        findViewById(R.id.btn_report_form).setOnClickListener(v -> startActivity(new Intent(this, ReportFormActivity.class)));
        findViewById(R.id.btn_view_reports).setOnClickListener(v -> startActivity(new Intent(this, ViewReportsActivity.class)));
        findViewById(R.id.btn_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.btn_about).setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
    }
}