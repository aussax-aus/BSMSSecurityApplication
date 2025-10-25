package com.bsms.securityapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsms.securityapp.R;
import com.bsms.securityapp.model.Report;

import java.util.List;

public class ReportListAdapter extends BaseAdapter {
    private final Context context;
    private final List<Report> reports;

    public ReportListAdapter(Context context, List<Report> reports) {
        this.context = context;
        this.reports = reports;
    }

    @Override
    public int getCount() {
        return reports.size();
    }

    @Override
    public Object getItem(int position) {
        return reports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_report_compact, parent, false);
        }

        Report report = reports.get(position);

        ((TextView) convertView.findViewById(R.id.tvSiteName)).setText(report.getSiteName());
        ((TextView) convertView.findViewById(R.id.tvSiteAddress)).setText(report.getSiteAddress());
        ((TextView) convertView.findViewById(R.id.tvDate)).setText(report.getDate());
        ((TextView) convertView.findViewById(R.id.tvOfficerName)).setText(report.getOfficerName());
        ((TextView) convertView.findViewById(R.id.tvStartTime)).setText("Start: " + report.getStartTime());
        ((TextView) convertView.findViewById(R.id.tvFinishTime)).setText("Finish: " + report.getFinishTime());
        ((TextView) convertView.findViewById(R.id.tvNotes)).setText(report.getNotes());

        return convertView;
    }
}
