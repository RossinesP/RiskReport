package com.ergo404.reportaproblem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ergo404.reportaproblem.utils.FileUtils;
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

/*
import crl.android.pdfwriter.PDFWriter;
import crl.android.pdfwriter.PaperSize;
import crl.android.pdfwriter.StandardFonts;
*/

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
    }

    public Report(Report source) {
        workUnit = source.workUnit;
        workPlace = source.workPlace;
        riskName = source.riskName;
        riskDescription = source.riskDescription;
        pictures = new ArrayList<String>();
        pictures.addAll(source.pictures);
        sqlId = source.sqlId;
        date = source.date;

        riskEmployees = source.riskEmployees;
        riskThirdParty = source.riskThirdParty;
        riskUsers = source.riskUsers;
        woundRisk = source.woundRisk;
        sicknessRisk = source.sicknessRisk;
        physicalHardness = source.physicalHardness;
        mentalHardness = source.mentalHardness;
        probability = source.probability;
        fixCost = source.fixCost;
        fixEasiness = source.fixEasiness;
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
