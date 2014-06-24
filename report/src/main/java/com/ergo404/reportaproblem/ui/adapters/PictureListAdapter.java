package com.ergo404.reportaproblem.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pierrerossines on 27/04/2014.
 */
public class PictureListAdapter extends BaseAdapter {
    private final static String TAG = PictureListAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String> mPictures;

    private ImageLoader mImageLoader;
    private HashMap<String, Bitmap> mBitmapCache;

    public PictureListAdapter(Context context) {
        mContext = context;
        mPictures = new ArrayList<String>();
        mImageLoader = ImageLoader.getInstance();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.ARGB_4444)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        mImageLoader.init(config);

        mBitmapCache = new HashMap<String, Bitmap>();
    }

    public void updatePictures(ArrayList<String> newPictures) {
        mPictures.clear();
        mPictures.addAll(newPictures);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mBitmapCache.remove(mPictures.get(position));
        mPictures.remove(position);
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
        ViewHolder holder;
        if (convertView == null || convertView.getLayoutParams().height == 0) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.picture_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String path = mPictures.get(position);
        Bitmap bitmap = mBitmapCache.get(path);
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap);
        } else {
            mImageLoader.displayImage(mPictures.get(position), holder.imageView, new ImageLoadingListener() {
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
        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
    }
}
