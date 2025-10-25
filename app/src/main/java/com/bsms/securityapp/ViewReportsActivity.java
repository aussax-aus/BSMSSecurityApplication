package com.bsms.securityapp;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.bsms.securityapp.adapter.ReportListAdapter;
import com.bsms.securityapp.data.ReportsDatabaseHelper;
import com.bsms.securityapp.model.Responses;
import java.util.List;
import com.bsms.securityapp.model.Report;
import android.widget.Button;


public class ViewReportsActivity extends AppCompatActivity {
    private ListView listViewReports;
    private ReportsDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        listViewReports = findViewById(R.id.listViewReports);
        dbHelper = new ReportsDatabaseHelper(this);

        List<Report> reports = dbHelper.getAllReportsObjects();
        ReportListAdapter adapter = new ReportListAdapter(this, reports);
        listViewReports.setAdapter(adapter);
    }
}
