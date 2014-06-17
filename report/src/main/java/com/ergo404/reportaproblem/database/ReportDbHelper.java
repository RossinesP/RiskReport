package com.ergo404.reportaproblem.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pierrerossines on 08/06/2014.
 */
public class ReportDbHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "ReportDb.db";
    private final static int VERSION = 1;

    public static final String TABLE_REPORTS = "reports";

    public static final String KEY_ID = "id";
    public static final String KEY_RISK_NAME = "riskName";
    public static final String KEY_RISK_DESCRIPTION = "riskDescription";
    public static final String KEY_WORK_PLACE = "workPlace";
    public static final String KEY_WORK_UNIT = "workUnit";
    public static final String KEY_RISK_DATE = "riskDate";

    public static final String KEY_RISK_EMPLOYEES = "riskEmployees";
    public static final String KEY_RISK_THIRD = "riskThirdParties";
    public static final String KEY_RISK_USERS = "riskUsers";
    public static final String KEY_WOUND = "woundRisk";
    public static final String KEY_SICKNESS = "sicknessRisk";
    public static final String KEY_PHYSICAL_HARDNESS = "physicalHardness";
    public static final String KEY_MENTAL_HARDNESS = "mentalHardness";
    public static final String KEY_PROBABILITY = "probability";
    public static final String KEY_FIX_COST = "fixCost";
    public static final String KEY_FIX_EASINESS = "fixEasiness";
    public static final String KEY_PICTURESLIST = "picturesList";

    public ReportDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_REPORTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_RISK_NAME + " TEXT,"
                + KEY_RISK_DESCRIPTION + " TEXT,"
                + KEY_WORK_PLACE + " TEXT,"
                + KEY_WORK_UNIT + " TEXT,"
                + KEY_RISK_DATE + " TEXT,"
                + KEY_RISK_EMPLOYEES + " INTEGER,"
                + KEY_RISK_THIRD + " INTEGER,"
                + KEY_RISK_USERS + " INTEGER,"
                + KEY_WOUND + " INTEGER,"
                + KEY_SICKNESS + " INTEGER,"
                + KEY_PHYSICAL_HARDNESS + " INTEGER,"
                + KEY_MENTAL_HARDNESS + " INTEGER,"
                + KEY_PROBABILITY + " INTEGER,"
                + KEY_FIX_COST + " INTEGER,"
                + KEY_FIX_EASINESS + " INTEGER,"
                + KEY_PICTURESLIST + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
        onCreate(db);
    }
}
