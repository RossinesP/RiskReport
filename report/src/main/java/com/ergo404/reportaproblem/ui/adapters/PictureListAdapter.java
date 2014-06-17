package com.ergo404.reportaproblem.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ergo404.reportaproblem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * Created by pierrerossines on 27/04/2014.
 */
public class PictureListAdapter extends BaseAdapter {
    private final static String TAG = PictureListAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String> mPictures;

    private ImageLoader mImageLoader;

    public PictureListAdapter(Context context) {
        mContext = context;
        mPictures = new ArrayList<String>();
        mImageLoader = ImageLoader.getInstance();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        mImageLoader.init(config);
    }

    public void updatePictures(ArrayList<String> newPictures) {
        mPictures.clear();
        mPictures.addAll(newPictures);
        notifyDataSetChanged();
    }

    public ArrayList<String> getItems() {
        return mPictures;
    }

    @Override
    public int getCount() {
        return mPictures.size();
    }

    @Override
    public Object getItem(int position) {
        return mPictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(TAG, "getView called");
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.picture_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mImageLoader.displayImage(mPictures.get(position), holder.imageView);
        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
    }
}
