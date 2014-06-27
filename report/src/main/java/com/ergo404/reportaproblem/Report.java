package com.ergo404.reportaproblem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                ? context.getString(R.string.undefined) : (value - 1)*25 + "%");
    }

    private static String getRatio(Context context, int textRes, int value) {
        return context.getString(textRes).replace("%num", (value == 0)
                ? context.getString(R.string.undefined) : (value - 1) + "/4");
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
        return getRatio(context, R.string.number_employees, riskEmployees);
    }

    public static String getThirdString(Context context, int riskThirdParty) {
        return getRatio(context, R.string.number_third, riskThirdParty);
    }

    public static String getUsersString(Context context, int riskUsers) {
        return getRatio(context, R.string.number_users, riskUsers);
    }

    public static String getWoundString(Context context, int woundRisk) {
        return getRatio(context, R.string.possible_wound, woundRisk);
    }

    public static String getSicknessString(Context context, int sicknessRisk) {
        return getRatio(context, R.string.possible_sickness, sicknessRisk);
    }

    public static String getPhysicalHardnessString(Context context, int physicalHardness) {
        return getRatio(context, R.string.physical_hardness, physicalHardness);
    }

    public static String getMentalHardnessString(Context context, int mentalHardness) {
        return getRatio(context, R.string.mental_hardness, mentalHardness);
    }

    public static String getProbabilityString(Context context, int probability) {
        return getRatio(context, R.string.risk_probability, probability);
    }

    public static String getFixCostString(Context context, int fixCost) {
        return getRatio(context, R.string.solution_cost, fixCost);
    }

    public static String getFixeasinessString(Context context, int fixEasiness) {
        return getRatio(context, R.string.solution_simplicity, fixEasiness);
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
            Log.v(TAG, picture);
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

    public static void email(Context context, String emailTo, String emailCC,
                             String subject, String emailText, String filePath) {
        ArrayList<String> files = new ArrayList<String>();
        files.add(filePath);
        Report.email(context, emailTo, emailCC, subject, emailText, files);
    }

    public static void email(Context context, String emailTo, String emailCC,
                             String subject, String emailText, List<String> filePaths) {
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{emailTo});
        emailIntent.putExtra(android.content.Intent.EXTRA_CC,
                new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        for (String file : filePaths)
        {
            File fileIn = new File(file);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.report_sending_chooser)));
    }
}
