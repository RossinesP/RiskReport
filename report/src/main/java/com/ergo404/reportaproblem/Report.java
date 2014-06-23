package com.ergo404.reportaproblem;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by pierrerossines on 07/06/2014.
 */
public class Report {
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
