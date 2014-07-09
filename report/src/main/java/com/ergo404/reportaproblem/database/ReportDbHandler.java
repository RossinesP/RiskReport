package com.ergo404.reportaproblem.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ergo404.reportaproblem.BuildConfig;
import com.ergo404.reportaproblem.Report;
import com.ergo404.reportaproblem.utils.Constants;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by pierrerossines on 08/06/2014.
 */
public class ReportDbHandler {
    public final static String INTENT_DB_CHANGED = "db_changed";
    private final static String TAG = ReportDbHandler.class.getSimpleName();
    private static ReportDbHandler sInstance;

    private Context mContext;
    private ReportDbHelper mHelper;
    private SQLiteDatabase mDb;

    private LocalBroadcastManager mBroadcastMgr;

    private ReportDbHandler(Context context) {
        mContext = context;
        mHelper = new ReportDbHelper(context);
        mBroadcastMgr = LocalBroadcastManager.getInstance(context);
    }

    public static ReportDbHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ReportDbHandler(context);
        }

        return sInstance;
    }

    public void openDatabase() {
        if (mDb == null) {
            mDb = mHelper.getWritableDatabase();
        }
    }

    public void closeDatabase() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    public Report getReport(long sqlId) {
        openDatabase();

        Report report = new Report();
        Cursor c = mDb.query(ReportDbHelper.TABLE_REPORTS, null,
                ReportDbHelper.KEY_ID + "=?",
                new String[] { String.valueOf(sqlId) }, null, null, null);
        if (c.moveToFirst()) {
            report.sqlId = sqlId;
            report.riskName = c.getString(c.getColumnIndex(ReportDbHelper.KEY_RISK_NAME));
            report.riskDescription = c.getString(c.getColumnIndex(ReportDbHelper.KEY_RISK_DESCRIPTION));
            report.workPlace = c.getString(c.getColumnIndex(ReportDbHelper.KEY_WORK_PLACE));
            report.workUnit = c.getString(c.getColumnIndex(ReportDbHelper.KEY_WORK_UNIT));

            report.riskEmployees = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_RISK_EMPLOYEES));
            report.riskUsers = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_RISK_USERS));
            report.riskThirdParty = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_RISK_THIRD));

            report.woundRisk = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_WOUND));
            report.sicknessRisk = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_SICKNESS));
            report.physicalHardness = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_PHYSICAL_HARDNESS));
            report.mentalHardness = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_MENTAL_HARDNESS));

            report.probability = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_PROBABILITY));
            report.fixCost = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_FIX_COST));
            report.fixEasiness = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_FIX_EASINESS));

            String pictures = c.getString(c.getColumnIndex(ReportDbHelper.KEY_PICTURESLIST));
            for (String picture : pictures.split(";")) {
                if (!picture.isEmpty()) {
                    report.pictures.add(picture);
                }
            }

            report.date = c.getLong(c.getColumnIndex(ReportDbHelper.KEY_RISK_DATE));
        }

        return report;
    }

    public boolean deleteReport(long sqlId) {
        openDatabase();

        Report report = getReport(sqlId);
        for (String picture : report.pictures) {
            if (BuildConfig.DEBUG) Log.v(TAG, "Deleting picture " + picture);
            File f = new File(Uri.parse(picture).getPath());
            if (f.getAbsolutePath().contains(Constants.REPORT_DIR.getAbsolutePath())) {
                if (f.delete()) {
                    if (BuildConfig.DEBUG) Log.v(TAG, "Picture deleted");
                } else {
                    if (BuildConfig.DEBUG) Log.v(TAG, "Error while deleting picture");
                }
            } else {
                if (BuildConfig.DEBUG) Log.v(TAG, "Did not delete that picture because it is not in our folder");
            }
        }
        int affected = mDb.delete(ReportDbHelper.TABLE_REPORTS, ReportDbHelper.KEY_ID + "=?",
                new String[] { String.valueOf(sqlId) });

        boolean reportsDeleted = affected > 0;
        if (reportsDeleted) {
            notifyDbChanged();
        }
        return reportsDeleted;
    }

    public boolean deleteReports(ArrayList<Long> reportsIds) {
        boolean result = true;
        for (Long sqlId : reportsIds) {
            result &= deleteReport(sqlId);
            if (result) Log.i(TAG, "Deleted report " + sqlId);
        }
        return result;
    }


    public ArrayList<Report> getReports() {
        openDatabase();

        ArrayList<Report> reports = new ArrayList<Report>();
        Cursor c = mDb.query(ReportDbHelper.TABLE_REPORTS, null, null, null, null, null,
                ReportDbHelper.KEY_RISK_DATE + " DESC", null);
        if (c.moveToFirst()) {
            do {
                Report report = new Report();
                report.sqlId = c.getLong(c.getColumnIndex(ReportDbHelper.KEY_ID));
                report.riskName = c.getString(c.getColumnIndex(ReportDbHelper.KEY_RISK_NAME));
                report.riskDescription = c.getString(c.getColumnIndex(ReportDbHelper.KEY_RISK_DESCRIPTION));
                report.workPlace = c.getString(c.getColumnIndex(ReportDbHelper.KEY_WORK_PLACE));
                report.workUnit = c.getString(c.getColumnIndex(ReportDbHelper.KEY_WORK_UNIT));

                report.riskEmployees = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_RISK_EMPLOYEES));
                report.riskUsers = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_RISK_USERS));
                report.riskThirdParty = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_RISK_THIRD));

                report.woundRisk = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_WOUND));
                report.sicknessRisk = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_SICKNESS));
                report.physicalHardness = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_PHYSICAL_HARDNESS));
                report.mentalHardness = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_MENTAL_HARDNESS));

                report.probability = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_PROBABILITY));
                report.fixCost = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_FIX_COST));
                report.fixEasiness = c.getInt(c.getColumnIndex(ReportDbHelper.KEY_FIX_EASINESS));

                String pictures = c.getString(c.getColumnIndex(ReportDbHelper.KEY_PICTURESLIST));
                for (String picture : pictures.split(";")) {
                    if (!picture.isEmpty()) {
                        report.pictures.add(picture);
                    }
                }

                report.date = c.getLong(c.getColumnIndex(ReportDbHelper.KEY_RISK_DATE));
                reports.add(report);
            } while (c.moveToNext());
        }

        return reports;
    }

    public long addOrUpdateReport(Report report) {
        openDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ReportDbHelper.KEY_RISK_NAME, report.riskName);
        cv.put(ReportDbHelper.KEY_RISK_DESCRIPTION, report.riskDescription);
        cv.put(ReportDbHelper.KEY_WORK_PLACE, report.workPlace);
        cv.put(ReportDbHelper.KEY_WORK_UNIT, report.workUnit);
        cv.put(ReportDbHelper.KEY_RISK_EMPLOYEES, report.riskEmployees);
        cv.put(ReportDbHelper.KEY_RISK_USERS, report.riskUsers);
        cv.put(ReportDbHelper.KEY_RISK_THIRD, report.riskThirdParty);
        cv.put(ReportDbHelper.KEY_WOUND, report.woundRisk);
        cv.put(ReportDbHelper.KEY_SICKNESS, report.sicknessRisk);
        cv.put(ReportDbHelper.KEY_PHYSICAL_HARDNESS, report.physicalHardness);
        cv.put(ReportDbHelper.KEY_MENTAL_HARDNESS, report.mentalHardness);
        cv.put(ReportDbHelper.KEY_PROBABILITY, report.probability);
        cv.put(ReportDbHelper.KEY_FIX_COST, report.fixCost);
        cv.put(ReportDbHelper.KEY_FIX_EASINESS, report.fixEasiness);

        String picturesList = "";
        for (String picture : report.pictures) {
            picturesList += picture + ";";
        }
        cv.put(ReportDbHelper.KEY_PICTURESLIST, picturesList);

        cv.put(ReportDbHelper.KEY_RISK_DATE, (report.date != -1) ? report.date : System.currentTimeMillis());
        long result = -1;
        if (report.sqlId == -1) {
            result = mDb.insert(ReportDbHelper.TABLE_REPORTS, null, cv);
        } else {
            long affected = mDb.update(ReportDbHelper.TABLE_REPORTS, cv,
                    ReportDbHelper.KEY_ID + "=?",
                    new String[] {
                        String.valueOf(report.sqlId)
                    });
            result = (affected > 0) ? report.sqlId : -1;
        }

        if (result != -1) {
            notifyDbChanged();
        }
        return result;
    }

    private void notifyDbChanged() {
        Intent notif = new Intent(INTENT_DB_CHANGED);
        mBroadcastMgr.sendBroadcast(notif);
    }
}
