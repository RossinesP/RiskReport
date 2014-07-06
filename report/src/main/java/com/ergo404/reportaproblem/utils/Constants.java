package com.ergo404.reportaproblem.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Pierre on 05/07/2014.
 */
public class Constants {
    public final static File REPORT_DIR = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "RiskReport");

    public static boolean createReportDir() {
        return (Constants.REPORT_DIR.mkdirs() || Constants.REPORT_DIR.isDirectory());
    }

}
