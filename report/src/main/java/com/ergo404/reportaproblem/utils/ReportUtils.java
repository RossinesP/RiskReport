package com.ergo404.reportaproblem.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ergo404.reportaproblem.BuildConfig;
import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pierrerossines on 12/07/2014.
 */
public class ReportUtils {
    private final static String TAG = ReportUtils.class.getSimpleName();
    /**
     * Creates a subfolder of folderPath with
     * the html report and a subfolder with a copy of all the pictures
     * from the report, then zips that subfolder and returns the path to the zip file
     *
     * @param context the Context
     * @param folderPath the folder in which we want to create the zipfile
     * @param sourceReport the Report to write
     * @return the path to the generated zip file
     */
    public static String writeHTMLReport(Context context, Report sourceReport, String folderPath) {
        Report report = new Report(sourceReport);

        String subFolder = folderPath + File.separator + report.riskName + "-"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(report.date));
        if (BuildConfig.DEBUG) Log.i(TAG, "writeHTMLReport to " + subFolder);
        File folder = new File(subFolder);
        if ((folder.exists() && !folder.isDirectory())
                || (!folder.exists() && !folder.mkdirs())) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Folder could not be created");
            return null;
        }

        File picturesFolder = new File(folder, "pictures");
        picturesFolder.mkdirs();
        for (String picture : report.pictures) {
            File src = new File(Uri.parse(picture).getPath());
            File dest = new File (picturesFolder, src.getName());
            if (BuildConfig.DEBUG) Log.i(TAG, "Copying " + src + " (" + src.getName() + ") to " + dest);
            try {
                dest.createNewFile();
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Did not work");
                e.printStackTrace();
                return null;
            }
        }

        File reportF = new File(folder, "report.html");
        try {
            reportF.createNewFile();
            try {
                FileOutputStream reportFile = new FileOutputStream(reportF);
                reportFile.write(generateHTML(context, report).getBytes("UTF-8"));
                reportFile.close();
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Could not create the " + reportF.getAbsolutePath() + " file");
            e.printStackTrace();
            return null;
        }

        String zipFile = new File(folderPath, folder.getName() + ".zip").getAbsolutePath();
        if (BuildConfig.DEBUG) Log.i(TAG, "Ziping folder " + folder.getAbsolutePath() +  " to file " + zipFile);
        ZipUtils.zipFileAtPath(folder, zipFile);
        FileUtils.delete(folder);
        return zipFile;
    }

    private static String getPercentage(Context context, int textRes, int value) {
        return context.getString(textRes).replace("%num", (value == 0)
                ? context.getString(R.string.undefined) : "~" + (value - 1)*25 + "%");
    }

    private static String getRatio(Context context, int textRes, int value) {
        return context.getString(textRes).replace("%num", (value == 0)
                ? context.getString(R.string.undefined) : (value - 1) + "/4");
    }

    private static String getStrength(Context context, int textRes, int value) {
        String replaceString = "";
        switch (value) {
            case 0:
                replaceString = context.getString(R.string.undefined);
                break;
            case 1:
                replaceString = context.getString(R.string.nullrisk);
                break;
            case 2:
                replaceString = context.getString(R.string.low);
                break;
            case 3:
                replaceString = context.getString(R.string.medium);
                break;
            case 4:
                replaceString = context.getString(R.string.high);
                break;
            case 5:
                replaceString = context.getString(R.string.veryhigh);
                break;
        }
        return context.getString(textRes).replace("%num", replaceString);
    }

    public static String getTitleString(Context context, String title) {
        if (title.isEmpty()) {
            return context.getString(R.string.no_title);
        } else {
            return context.getString(R.string.display_risk_name).replace("%value", title);
        }
    }

    public static String getDescriptionString(Context context, String description) {
        return context.getString(R.string.display_risk_description).replace("%value",
                description.isEmpty()
                        ? context.getString(R.string.undefined) : description);
    }

    public static String getWorkUnitString(Context context, String workUnit) {
        return context.getString(R.string.display_work_unit).replace("%value",
                workUnit.isEmpty()
                        ? context.getString(R.string.undefined) : workUnit);
    }

    public static String getWorkPlaceString(Context context, String workPlace) {
        return context.getString(R.string.display_work_place).replace("%value",
                workPlace.isEmpty()
                        ? context.getString(R.string.undefined) : workPlace);
    }

    public static String getEmployeesString(Context context, int riskEmployees) {
        return getPercentage(context, R.string.number_employees, riskEmployees);
    }

    public static String getThirdString(Context context, int riskThirdParty) {
        return getPercentage(context, R.string.number_third, riskThirdParty);
    }

    public static String getUsersString(Context context, int riskUsers) {
        return getPercentage(context, R.string.number_users, riskUsers);
    }

    public static String getWoundString(Context context, int woundRisk) {
        return getStrength(context, R.string.possible_wound, woundRisk);
    }

    public static String getSicknessString(Context context, int sicknessRisk) {
        return getStrength(context, R.string.possible_sickness, sicknessRisk);
    }

    public static String getPhysicalHardnessString(Context context, int physicalHardness) {
        return getStrength(context, R.string.physical_hardness, physicalHardness);
    }

    public static String getMentalHardnessString(Context context, int mentalHardness) {
        return getStrength(context, R.string.mental_hardness, mentalHardness);
    }

    public static String getProbabilityString(Context context, int probability) {
        return getStrength(context, R.string.risk_probability, probability);
    }

    public static String getFixCostString(Context context, int fixCost) {
        return getStrength(context, R.string.solution_cost, fixCost);
    }

    public static String getFixeasinessString(Context context, int fixEasiness) {
        return getStrength(context, R.string.solution_simplicity, fixEasiness);
    }

    private static String generateHTML(Context context, Report report) {
        StringBuilder htmlDoc = new StringBuilder();
        htmlDoc.append("<!DOCTYPE html>");
        htmlDoc.append("<html>");
        htmlDoc.append("<head>");
        htmlDoc.append("<meta charset=\"UTF-8\" />");
        htmlDoc.append("<title>" + getTitleString(context, report.riskName) + "</title>");
        htmlDoc.append("</head>");
        htmlDoc.append("<body>");
        htmlDoc.append("<h1>" + getTitleString(context, report.riskName) + "</h1>");
        htmlDoc.append("<p>" + getDescriptionString(context, report.riskDescription) + "</p>");
        htmlDoc.append("<p>" + getWorkPlaceString(context, report.workPlace) + "</p>");
        htmlDoc.append("<p>" + getWorkUnitString(context, report.workUnit) + "</p>");
        htmlDoc.append("<p>" + new SimpleDateFormat().format(new Date(report.date)) + "</p>");
        htmlDoc.append("<p>" + getEmployeesString(context, report.riskEmployees) + "</p>");
        htmlDoc.append("<p>" + getUsersString(context, report.riskUsers) + "</p>");
        htmlDoc.append("<p>" + getThirdString(context, report.riskThirdParty) + "</p>");
        htmlDoc.append("<p>" + getWoundString(context, report.woundRisk)+ "</p>");
        htmlDoc.append("<p>" + getSicknessString(context, report.sicknessRisk) + "</p>");
        htmlDoc.append("<p>" + getPhysicalHardnessString(context, report.physicalHardness) + "</p>");
        htmlDoc.append("<p>" + getMentalHardnessString(context, report.mentalHardness) + "</p>");
        htmlDoc.append("<p>" + getProbabilityString(context, report.probability) + "</p>");
        htmlDoc.append("<p>" + getFixCostString(context, report.fixCost) + "</p>");
        htmlDoc.append("<p>" + getFixeasinessString(context, report.fixEasiness) + "</p>");
        for (String picture : report.pictures) {
            htmlDoc.append("<p><img src=\"pictures/" + new File(picture).getName() + "\" width=\"600px\"/></p>");
        }
        htmlDoc.append("</body></html>");
        return htmlDoc.toString();
    }
}
