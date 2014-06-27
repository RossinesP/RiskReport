package com.ergo404.reportaproblem.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by pierrerossines on 08/06/2014.
 */
public class ReportListAdapter extends BaseAdapter {
    private final static String TAG = ReportListAdapter.class.getSimpleName();
    private ArrayList<Report> mReportsList;
    private Context mContext;
    private java.text.DateFormat mDateFormatter;
    private ImageLoader mImageLoader;
    private HashMap<String, Bitmap> mBitmapCache;
    private ArrayList<Integer> mSelected;

    public ReportListAdapter(Context context) {
        mContext = context;
        mReportsList = new ArrayList<Report>();
        mDateFormatter = DateFormat.getDateFormat(mContext);

        mImageLoader = ImageLoader.getInstance();
        BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
        decodingOptions.inSampleSize = 6;
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .decodingOptions(decodingOptions)
                .bitmapConfig(Bitmap.Config.ARGB_4444)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        mImageLoader.init(config);

        mBitmapCache = new HashMap<String, Bitmap>();
        mSelected = new ArrayList<Integer>();
    }

    public void updateReportsList(ArrayList<Report> newReportsList) {
        mReportsList.clear();
        mReportsList.addAll(newReportsList);
        mSelected.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        Report report = mReportsList.get(position);
        if (report.pictures.size() > 0) {
            mBitmapCache.remove(report.pictures.get(0));
        }
        mReportsList.remove(position);
        notifyDataSetChanged();
    }

    public void select(int position) {
        mSelected.add(position);
        notifyDataSetChanged();
    }

    public void unselect(int position) {
        mSelected.remove((Integer) position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelected.clear();
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
            viewHolder.riskPicture = (ImageView) convertView.findViewById(R.id.report_picture);
            viewHolder.root = (ViewGroup) convertView.findViewById(R.id.item_root);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Report report = (Report) getItem(position);

        viewHolder.riskName.setText(report.riskName.isEmpty() ? mContext.getString(R.string.no_title) : report.riskName);
        viewHolder.workPlace.setText(report.workPlace);

        String date = mDateFormatter.format(new Date(report.date));
        viewHolder.riskDate.setText(date);

        Log.v(TAG, "Report name : " + report.riskName + ", pictures : " +report.pictures.size());
        if (report.pictures.size() > 0) {
            viewHolder.riskPicture.setVisibility(View.VISIBLE);
            final String path = report.pictures.get(0);
            Bitmap bitmap = mBitmapCache.get(path);
            if (bitmap != null) {
                viewHolder.riskPicture.setImageBitmap(bitmap);
            } else {
                mImageLoader.displayImage(path, viewHolder.riskPicture, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        mBitmapCache.put(path, bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        } else {
            viewHolder.riskPicture.setImageBitmap(null);
            viewHolder.riskPicture.setVisibility(View.GONE);
        }

        viewHolder.root.setBackgroundColor(mSelected.contains(position) ?
                mContext.getResources().getColor(android.R.color.darker_gray) :
                mContext.getResources().getColor(android.R.color.transparent));
        return convertView;
    }

    private class ViewHolder {
        public TextView riskName;
        public TextView workPlace;
        public TextView riskDate;
        public ImageView riskPicture;
        public ViewGroup root;
    }
}
