package com.ergo404.reportaproblem;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ergo404.reportaproblem.database.ReportDbHandler;
import com.ergo404.reportaproblem.utils.Constants;
import com.ergo404.reportaproblem.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Pierre on 05/07/2014.
 */
public class PictureCleanerService extends IntentService {
    public final static String TAG = PictureCleanerService.class.getSimpleName();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PictureCleanerService(String name) {
        super(name);
    }

    public PictureCleanerService() {
        super("PictureCleanerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (BuildConfig.DEBUG) Log.v(TAG, "onHandleIntent called");
        cleanPictures();
    }

    private void cleanPictures() {
        ReportDbHandler dbHandler = ReportDbHandler.getInstance(this);
        ArrayList<Report> reports = dbHandler.getReports();
        if (reports == null) {
            if (BuildConfig.DEBUG) Log.v(TAG, "Reports List is null");
            return;
        }

        ArrayList<String> pictureFiles = new ArrayList<String>();

        if (!Constants.createReportDir()) {
            if (BuildConfig.DEBUG) Log.v(TAG, "Could not create report dir");
            return;
        }
        for (File f : Constants.REPORT_DIR.listFiles()) {
            if (BuildConfig.DEBUG) Log.v(TAG, "Found file : " + f.getName());
            if (f.isFile() && f.getAbsolutePath().toLowerCase().matches(".*[.]{1}(jpeg|jpg)")) {
                if (BuildConfig.DEBUG) Log.v(TAG, "Is an image file");
                pictureFiles.add(Uri.fromFile(f).toString());
            } else {
                if (BuildConfig.DEBUG) Log.v(TAG, "Deleting file " + f);
                FileUtils.delete(f);
            }
        }

        ArrayList<String> reportPictures = new ArrayList<String>();
        for (Report report : reports) {
            ArrayList<String> newPictureList = new ArrayList<String>();
            for (String picturePath : report.pictures) {
                File picture = new File(Uri.parse(picturePath).getPath());
                if (!picture.exists() || !picture.isFile()) {
                    if (BuildConfig.DEBUG) Log.v(TAG, "Found a picture in the DB which does not exist");
                } else {
                    newPictureList.add(picturePath);
                }
            }

            if (report.pictures.size() != newPictureList.size()) {
                if (BuildConfig.DEBUG) Log.v(TAG, "Updating the picture list for report " + report.sqlId);
                report.pictures = newPictureList;
                dbHandler.addOrUpdateReport(report);
            }

            reportPictures.addAll(newPictureList);
        }

        for (String picture : pictureFiles) {
            if (!reportPictures.contains(picture)) {
                if (BuildConfig.DEBUG) Log.v(TAG, "Found picture " + picture + " which is NOT in the DB");
                File pictureF = new File(Uri.parse(picture).getPath());
                if (pictureF.delete()) {
                    if (BuildConfig.DEBUG) Log.v(TAG, "Deleted picture " + picture);
                } else {
                    if (BuildConfig.DEBUG) Log.v(TAG, "Error while deleting picture " + picture);
                }
            } else {
                if (BuildConfig.DEBUG) Log.v(TAG, "Found picture " + picture + " which is in the DB");
            }
        }
    }
}
