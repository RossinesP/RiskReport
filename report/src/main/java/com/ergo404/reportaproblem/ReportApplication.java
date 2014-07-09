package com.ergo404.reportaproblem;

import android.app.Application;
import android.content.Intent;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by pierrerossines on 15/06/2014.
 */
public class ReportApplication extends Application {
    private final static String TAG = ReportApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .build();
        ImageLoader.getInstance().init(config);

        Intent cleanPicturesService = new Intent(this, PictureCleanerService.class);
        startService(cleanPicturesService);
    }
}
