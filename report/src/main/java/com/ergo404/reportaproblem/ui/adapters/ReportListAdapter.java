package com.ergo404.reportaproblem.ui.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pierrerossines on 08/06/2014.
 */
public class ReportListAdapter extends BaseAdapter {
    private ArrayList<Report> mReportsList;
    private Context mContext;
    private java.text.DateFormat mDateFormatter;

    public ReportListAdapter(Context context) {
        mContext = context;
        mReportsList = new ArrayList<Report>();
        mDateFormatter = DateFormat.getDateFormat(mContext);
    }

    public void updateReportsList(ArrayList<Report> newReportsList) {
        mReportsList.clear();
        mReportsList.addAll(newReportsList);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mReportsList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mReportsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReportsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_report, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.riskName = (TextView) convertView.findViewById(R.id.risk_name);
            viewHolder.workPlace = (TextView) convertView.findViewById(R.id.work_place);
            viewHolder.riskDate = (TextView) convertView.findViewById(R.id.report_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Report report = (Report) getItem(position);
        viewHolder.riskName.setText(report.riskName);
        viewHolder.workPlace.setText(report.workPlace);

        String date = mDateFormatter.format(new Date(report.date));
        viewHolder.riskDate.setText(date);
        return convertView;
    }

    private class ViewHolder {
        public TextView riskName;
        public TextView workPlace;
        public TextView riskDate;
    }
}
