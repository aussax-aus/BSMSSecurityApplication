package com.bsms.securityapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bsms.securityapp.adapter.ReportAdapter;
import com.bsms.securityapp.data.ResponseDatabaseHelper;
import com.bsms.securityapp.model.Responses;
import java.util.List;
import android.widget.Button;



public class ViewResponsesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResponseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_responses);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Go back to the previous screen
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        recyclerView = findViewById(R.id.recyclerReports);
        dbHelper = new ResponseDatabaseHelper(this);

        loadReports();
    }


    private void loadReports() {
        List<Responses> reports = dbHelper.getAllResponses();
        if (reports.isEmpty()) {
            Toast.makeText(this, "No alarm responses found.", Toast.LENGTH_SHORT).show();
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ReportAdapter(this, reports, dbHelper));
    }
}
