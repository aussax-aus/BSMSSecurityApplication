package com.bsms.securityapp.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bsms.securityapp.AlarmResponseActivity;
import com.bsms.securityapp.R;
import com.bsms.securityapp.data.ResponseDatabaseHelper;
import com.bsms.securityapp.model.Responses;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final List<Responses> reportList;
    private final Context context;
    private final ResponseDatabaseHelper dbHelper;

    public ReportAdapter(Context context, List<Responses> reportList, ResponseDatabaseHelper dbHelper) {
        this.context = context;
        this.reportList = reportList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_respones, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Responses report = reportList.get(position);
        String details = report.getDetails();

        holder.tvTypeDept.setText("Alarm: " + extract(details, "Alarm Type:") +
                " | Department: " + extract(details, "Department:"));
        holder.tvRoleName.setText("Role: " + extract(details, "Role:") +
                " | Name: " + extract(details, "Name:"));
        holder.tvContactLeave.setText("Contact: " + extract(details, "Contact:") +
                " | Leaving: " + extract(details, "Time Leaving:"));
        holder.tvIncident.setText("Incident: " + extract(details, "Incident:"));
        holder.tvAction.setText("Action: " + extract(details, "Action:"));
        holder.tvDateTime.setText(report.getDate());

        // Set photo count with fallback logic
        int photoCount = report.getPhotoCount();
        if (photoCount == 0) {
            // Fallback: parse photoPaths if photoCount is not set but paths exist
            String photoPaths = report.getPhotoPaths();
            if (photoPaths != null && !photoPaths.trim().isEmpty()) {
                String[] paths = photoPaths.split(",");
                for (String path : paths) {
                    if (path != null && !path.trim().isEmpty()) {
                        photoCount++;
                    }
                }
            }
        }
        
        // Show/hide photo count based on whether there are photos
        if (photoCount > 0) {
            holder.tvPhotoCount.setText("Photos: " + photoCount);
            holder.tvPhotoCount.setVisibility(View.VISIBLE);
        } else {
            holder.tvPhotoCount.setVisibility(View.GONE);
        }

        // Copy button
        holder.btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            String copyText = "Date: " + report.getDate() + "\nLocation: " + report.getLocation() + "\n" + report.getDetails();
            ClipData clip = ClipData.newPlainText("Report", copyText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AlarmResponseActivity.class);
            intent.putExtra("edit_report_id", report.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.getWritableDatabase().delete("alarm_responses", "id=?", new String[]{String.valueOf(report.getId())});
            reportList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Report deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    private String extract(String text, String key) {
        int start = text.indexOf(key);
        if (start == -1) return "";
        int end = text.indexOf("|", start + key.length());
        if (end == -1) end = text.length();
        return text.substring(start + key.length(), end).trim();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeDept, tvRoleName, tvContactLeave, tvIncident, tvAction, tvDateTime, tvPhotoCount;
        Button btnCopy, btnEdit, btnDelete;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeDept = itemView.findViewById(R.id.tvTypeDept);
            tvRoleName = itemView.findViewById(R.id.tvRoleName);
            tvContactLeave = itemView.findViewById(R.id.tvContactLeave);
            tvIncident = itemView.findViewById(R.id.tvIncident);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPhotoCount = itemView.findViewById(R.id.tvPhotoCount);
            btnCopy = itemView.findViewById(R.id.btnCopy);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
