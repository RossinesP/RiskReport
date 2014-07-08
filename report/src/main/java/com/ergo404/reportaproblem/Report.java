package com.ergo404.reportaproblem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.ergo404.reportaproblem.utils.ZipUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import crl.android.pdfwriter.PDFWriter;
import crl.android.pdfwriter.PaperSize;
import crl.android.pdfwriter.StandardFonts;

/**
 * Created by pierrerossines on 07/06/2014.
 */
public class Report {
    private final static String TAG = Report.class.getSimpleName();
    public static final String SQLID = "sqlid";
    public static final String DATE = "date";
    public static final String RISKNAME = "riskname";
    public static final String RISKDESCRIPTION = "riskdescription";
    public static final String WORKPLACE = "workplace";
    public static final String WORKUNIT = "workunit";
    public static final String RISKEMPLOYEES = "riskemployees";
    public static final String RISKUSERS = "riskusers";
    public static final String RISKTHIRD = "riskthird";
    public static final String WOUNDRISK = "woundrisk";
    public static final String SICKNESSRISK = "sicknessrisk";
    public static final String PHYSICALHARDNESS = "physicalhardness";
    public static final String MENTALHARDNESS = "mentalhardness";
    public static final String PROBABILITY = "probability";
    public static final String FIXCOST = "fixcost";
    public static final String FIXEASINESS = "fixeasiness";
    public static final String PICTURES = "pictures";


    public int riskEmployees;
    public int riskUsers;
    public int riskThirdParty;
    public int woundRisk;
    public int sicknessRisk;
    public int physicalHardness;
    public int mentalHardness;
    public int probability;
    public int fixCost;
    public int fixEasiness;

    public long date;
    public long sqlId;

    public String workUnit;
    public String workPlace;
    public String riskName;
    public String riskDescription;
    public ArrayList<String> pictures;

    public Report() {
        workUnit = "";
        workPlace = "";
        riskName = "";
        riskDescription = "";
        pictures = new ArrayList<String>();
        sqlId = -1;
        date = -1;
        /*
        fixCost = -1;
        fixEasiness = -1;
        probability = -1;
        mentalHardness = -1;
        physicalHardness = -1;
        sicknessRisk = -1;
        woundRisk = -1;
        riskEmployees = -1;
        riskThirdParty = -1;
        riskUsers = -1;
        */
    }

    public void writeReport (Bundle outState) {
        outState.putLong(SQLID, sqlId);
        outState.putLong(DATE, date);

        outState.putString(RISKNAME, riskName);
        outState.putString(RISKDESCRIPTION, riskDescription);
        outState.putString(WORKPLACE, workPlace);
        outState.putString(WORKUNIT, workUnit);

        outState.putInt(RISKEMPLOYEES, riskEmployees);
        outState.putInt(RISKUSERS, riskUsers);
        outState.putInt(RISKTHIRD, riskThirdParty);
        outState.putInt(WOUNDRISK, woundRisk);
        outState.putInt(SICKNESSRISK, sicknessRisk);
        outState.putInt(PHYSICALHARDNESS, physicalHardness);
        outState.putInt(MENTALHARDNESS, mentalHardness);
        outState.putInt(PROBABILITY, probability);
        outState.putInt(FIXCOST, fixCost);
        outState.putInt(FIXEASINESS, fixEasiness);

        outState.putStringArrayList(PICTURES, pictures);
    }

    public static Report getReport(Bundle savedState) {
        Report report = new Report();
        report.sqlId = savedState.getLong(SQLID, -1);
        report.date = savedState.getLong(DATE, -1);

        report.riskName = savedState.getString(RISKNAME, "");
        report.riskDescription = savedState.getString(RISKDESCRIPTION, "");
        report.workPlace = savedState.getString(WORKPLACE, "");
        report.workUnit = savedState.getString(WORKUNIT, "");

        report.riskEmployees = savedState.getInt(RISKEMPLOYEES);
        report.riskUsers = savedState.getInt(RISKUSERS);
        report.riskThirdParty = savedState.getInt(RISKTHIRD);
        report.woundRisk = savedState.getInt(WOUNDRISK);
        report.sicknessRisk = savedState.getInt(SICKNESSRISK);
        report.physicalHardness = savedState.getInt(PHYSICALHARDNESS);
        report.mentalHardness = savedState.getInt(MENTALHARDNESS);
        report.probability = savedState.getInt(PROBABILITY);
        report.fixCost = savedState.getInt(FIXCOST);
        report.fixEasiness = savedState.getInt(FIXEASINESS);

        report.pictures = savedState.getStringArrayList(PICTURES);
        return report;
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

    private String generateHTML(Context context) {
        StringBuilder htmlDoc = new StringBuilder();
        htmlDoc.append("<!DOCTYPE html>");
        htmlDoc.append("<html>");
        htmlDoc.append("<head>");
        htmlDoc.append("<meta charset=\"UTF-8\" />");
        htmlDoc.append("<title>" + getTitleString(context, riskName) + "</title>");
        htmlDoc.append("</head>");
        htmlDoc.append("<body>");
        htmlDoc.append("<h1>" + getTitleString(context, riskName) + "</h1>");
        htmlDoc.append("<p>" + getDescriptionString(context, riskDescription) + "</p>");
        htmlDoc.append("<p>" + getWorkPlaceString(context, workPlace) + "</p>");
        htmlDoc.append("<p>" + getWorkUnitString(context, workUnit) + "</p>");
        htmlDoc.append("<p>" + new SimpleDateFormat().format(new Date(date)) + "</p>");
        htmlDoc.append("<p>" + getEmployeesString(context, riskEmployees) + "</p>");
        htmlDoc.append("<p>" + getUsersString(context, riskUsers) + "</p>");
        htmlDoc.append("<p>" + getThirdString(context, riskThirdParty) + "</p>");
        htmlDoc.append("<p>" + getWoundString(context, woundRisk)+ "</p>");
        htmlDoc.append("<p>" + getSicknessString(context, sicknessRisk) + "</p>");
        htmlDoc.append("<p>" + getPhysicalHardnessString(context, physicalHardness) + "</p>");
        htmlDoc.append("<p>" + getMentalHardnessString(context, mentalHardness) + "</p>");
        htmlDoc.append("<p>" + getProbabilityString(context, probability) + "</p>");
        htmlDoc.append("<p>" + getFixCostString(context, fixCost) + "</p>");
        htmlDoc.append("<p>" + getFixeasinessString(context, fixEasiness) + "</p>");
        for (String picture : pictures) {
            htmlDoc.append("<p><img src=\"pictures/" + new File(picture).getName() + "\" /></p>");
        }
        htmlDoc.append("</body></html>");
        return htmlDoc.toString();
    }
    private String generatePDF(Context context) {
        PDFWriter writer = new PDFWriter(PaperSize.A4_WIDTH, PaperSize.A4_HEIGHT);

        final int fontSize = 16;
        final int availableWidth = PaperSize.A4_WIDTH - (2 * PaperSize.A4_MARGIN);
        final int margin = PaperSize.A4_MARGIN;
        final int pageHeight = PaperSize.A4_HEIGHT;
        writer.setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_ROMAN);

        int i = 1;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getTitleString(context, riskName));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getDescriptionString(context, riskDescription));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getWorkPlaceString(context, workPlace));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getWorkUnitString(context, workUnit));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, new SimpleDateFormat().format(new Date(date)));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getEmployeesString(context, riskEmployees));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getUsersString(context, riskUsers));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getThirdString(context, riskThirdParty));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getWoundString(context, woundRisk));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getSicknessString(context, sicknessRisk));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getPhysicalHardnessString(context, physicalHardness));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getMentalHardnessString(context, mentalHardness));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getProbabilityString(context, probability));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getFixCostString(context, fixCost));
        i++;
        writer.addText(margin, pageHeight - (margin + i * fontSize), fontSize, getFixeasinessString(context, fixEasiness));
        i++;

        for (String picture : pictures) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bmp = BitmapFactory.decodeFile(Uri.parse(picture).getPath(), options);
            writer.newPage();
            writer.addImage(PaperSize.A4_MARGIN, PaperSize.A4_HEIGHT - (PaperSize.A4_MARGIN + bmp.getHeight()), bmp);
        }

        return writer.asString();
    }

    public String writePDFReport(String folder, Context context) {
        String filePath = folder + File.separator
                + riskName + "-"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(date)) + ".pdf";
        File newFile = new File(filePath);
        try {
            newFile.createNewFile();
            try {
                FileOutputStream pdfFile = new FileOutputStream(newFile);
                //pdfFile.write(generatePDF(context).getBytes("ISO-8859-1"));
                pdfFile.write(generatePDF(context).getBytes("UTF-8"));
                pdfFile.close();
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    /**
     * Creates a subfolder of folderPath with
     * the html report and a subfolder with a copy of all the pictures
     * from the report, then zips that subfolder and returns the path to the zip file
     *
     * @param folderPath the folder in which we want to create the zipfile
     * @param context the Context
     * @return the path to the generated zip file
     */
    public String writeHTMLReport(String folderPath, Context context) {
        String subFolder = folderPath + File.separator + riskName + "-"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(date));
        if (BuildConfig.DEBUG) Log.i(TAG, "writeHTMLReport to " + subFolder);
        File folder = new File(subFolder);
        if ((folder.exists() && !folder.isDirectory())
                || (!folder.exists() && !folder.mkdirs())) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Folder could not be created");
            return null;
        }

        File picturesFolder = new File(folder, "pictures");
        if ((picturesFolder.exists() && !picturesFolder.isDirectory())
                || (!picturesFolder.exists() && !picturesFolder.mkdirs())) return null;
        for (String picture : pictures) {
            File src = new File(Uri.parse(picture).getPath());
            File dest = new File (picturesFolder, src.getName());
            if (BuildConfig.DEBUG) Log.i(TAG, "Copying " + src + " to " + dest);
            try {
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

        File report = new File(folder, "report.html");
        try {
            report.createNewFile();
            try {
                FileOutputStream reportFile = new FileOutputStream(report);
                reportFile.write(generateHTML(context).getBytes("UTF-8"));
                reportFile.close();
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Could not create the " + report.getAbsolutePath() + " file");
            e.printStackTrace();
            return null;
        }

        String zipFile = new File(folderPath, folder.getName() + ".zip").getAbsolutePath();
        if (BuildConfig.DEBUG) Log.i(TAG, "Ziping folder " + folder.getAbsolutePath() +  " to file " + zipFile);
        ZipUtils.zipFileAtPath(folder, zipFile);
        return zipFile;
    }

    public boolean isEmpty() {
        return riskName.isEmpty()
                && riskDescription.isEmpty()
                && workPlace.isEmpty()
                && workUnit.isEmpty()
                && pictures.isEmpty()
                && riskEmployees == 0
                && riskUsers == 0
                && riskThirdParty == 0
                && woundRisk == 0
                && sicknessRisk == 0
                && physicalHardness == 0
                && mentalHardness == 0
                && probability == 0
                && fixCost == 0
                && fixEasiness == 0;
    }
}
