package com.ergo404.reportaproblem.utils;

import android.os.Environment;

import com.ergo404.reportaproblem.BuildConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created by Pierre on 05/07/2014.
 */
public class Constants {
    public final static File REPORT_DIR = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "RiskReport" + (BuildConfig.DEBUG ? ".debug" : ""));

    public static boolean createReportDir() {
        boolean folderAvailable = (Constants.REPORT_DIR.mkdirs() || Constants.REPORT_DIR.isDirectory());
        if (folderAvailable) {
            File nomediaFile = new File(REPORT_DIR, ".nomedia");
            try {
                nomediaFile.createNewFile();
            } catch (IOException ioe){
                // Ignore this error
            }
        }
        return folderAvailable;
    }

}
