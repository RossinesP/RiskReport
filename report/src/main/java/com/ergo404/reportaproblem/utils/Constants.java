package com.ergo404.reportaproblem.utils;

import android.os.Environment;

import com.ergo404.reportaproblem.BuildConfig;

import java.io.File;

/**
 * Created by Pierre on 05/07/2014.
 */
public class Constants {
    public final static File REPORT_DIR = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "RiskReport" + (BuildConfig.DEBUG ? ".debug" : ""));

    public static boolean createReportDir() {
        return (Constants.REPORT_DIR.mkdirs() || Constants.REPORT_DIR.isDirectory());
    }

}
