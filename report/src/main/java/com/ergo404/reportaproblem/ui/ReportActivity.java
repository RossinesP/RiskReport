package com.ergo404.reportaproblem.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;
import com.ergo404.reportaproblem.database.ReportDbHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pierrerossines on 09/06/2014.
 */
public class ReportActivity extends FragmentActivity implements DescriptionFragment.OnUpdateReportListener, PicturesFragment.OnPictureUpdatedListener {
    public final static String EXTRA_REPORTID = "reportid";

    private final static String EXTRA_LASTPICTURE = "lastPicture";
    private final static String TAG = ReportActivity.class.getSimpleName();
    private final static int REQUEST_IMAGE_CAPTURE = 101;

    private Report mReport;
    private Uri mLastAskedPicture;
    private Uri mPictureToAdd;

    private final File mReportFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "RiskReport");
    private DescriptionFragment mDescriptionFragment;
    private PicturesFragment mPicturesFragment;

    private class LoadReportTask extends AsyncTask<Long, Void, Report> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "Loading the report from the DB");
        }

        @Override
        protected Report doInBackground(Long... params) {
            ReportDbHandler dbHandler = ReportDbHandler.getInstance(ReportActivity.this);
            Report result = dbHandler.getReport(params[0]);
            dbHandler.closeDatabase();
            return result;
        }

        @Override
        protected void onPostExecute(Report report) {
            super.onPostExecute(report);
            setReport(report);
            Log.i(TAG, "Loaded report with id " + report.sqlId);
        }
    };

    private class UpdateReportTask extends AsyncTask<Report, Void, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG , "Saving the report to the DB");
        }

        @Override
        protected Long doInBackground(final Report... params) {
            Report report = params[0];
            ReportDbHandler dbHandler = ReportDbHandler.getInstance(ReportActivity.this);
            long result = dbHandler.addOrUpdateReport(report);
            dbHandler.closeDatabase();

            if (report.sqlId != -1) {
                return report.sqlId;
            } else {
                return result;
            }
        }

        @Override
        protected void onPostExecute(Long sqlId) {
            super.onPostExecute(sqlId);
            Log.v(TAG, "Report saved, id = " + sqlId);
            mReport.sqlId = sqlId;
            setReport(mReport);
        }
    }

    private class DeleteReportTask extends AsyncTask<Report, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG , "Deleting the report from the DB");
        }

        @Override
        protected Boolean doInBackground(Report... params) {
            ReportDbHandler handler = ReportDbHandler.getInstance(ReportActivity.this);
            boolean result = handler.deleteReport(params[0].sqlId);
            handler.closeDatabase();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean deleted) {
            super.onPostExecute(deleted);
            setReport(new Report());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mReport = new Report();

        mDescriptionFragment = (DescriptionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_description);
        mPicturesFragment = (PicturesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_pictures);
        loadReport(savedInstanceState);
    }

    private void loadReport(Bundle savedInstanceState) {
        Log.v(TAG, "loadReport called");
        if (savedInstanceState != null) {
            Log.v(TAG, "Save instance state present !");
            String lastPicture = savedInstanceState.getString(EXTRA_LASTPICTURE);
            if (lastPicture != null) {
                Log.v(TAG, "Last picture present");
                mLastAskedPicture = Uri.parse(lastPicture);
            }

            long sqliteId = savedInstanceState.getLong(EXTRA_REPORTID, -1);
            if (sqliteId != -1) {
                Log.v(TAG, "SqlId present : " + sqliteId);
                new LoadReportTask().execute(sqliteId);
                return;
            }
        }

        Intent callingIntent = getIntent();
        if (callingIntent != null) {
            long sqliteId = callingIntent.getLongExtra(EXTRA_REPORTID, -1);
            if (sqliteId != -1) {
                Log.v(TAG, "Intent with id: " + sqliteId);
                new LoadReportTask().execute(sqliteId);
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReport.isEmpty()) {
            new DeleteReportTask().execute(mReport);
        } else {
            new UpdateReportTask().execute(mReport);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addpicture:
                dispatchTakePictureIntent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState()");
        if (mReport.sqlId != -1) {
            outState.putLong(EXTRA_REPORTID, mReport.sqlId);
        }
        if (mLastAskedPicture != null) {
            outState.putString(EXTRA_LASTPICTURE, mLastAskedPicture.toString());
        }
    }

    @Override
    public void updateData(String riskName, String riskDescription, String workUnit, String workPlace, int riskEmployees, int riskUsers, int riskThird, int woundRisk, int sicknessRisk, int physicalHardness, int mentalHardness, int probability, int fixCost, int fixEasiness) {
        mReport.riskName = riskName;
        mReport.riskDescription = riskDescription;
        mReport.workUnit = workUnit;
        mReport.workPlace = workPlace;
        mReport.riskEmployees = riskEmployees;
        mReport.riskUsers = riskUsers;
        mReport.riskThirdParty = riskThird;
        mReport.woundRisk = woundRisk;
        mReport.sicknessRisk = sicknessRisk;
        mReport.physicalHardness = physicalHardness;
        mReport.mentalHardness = mentalHardness;
        mReport.probability = probability;
        mReport.fixCost = fixCost;
        mReport.fixEasiness = fixEasiness;
    }

    @Override
    public void updateData(ArrayList<String> picturesList, boolean updateReport) {
        mReport.pictures.clear();
        mReport.pictures.addAll(picturesList);
        if (updateReport) setReport(mReport);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mReport != null && mLastAskedPicture != null) {
                Log.v(TAG, "Adding a last Asked picture in onActivityResult");
                if (mReport.sqlId != -1) {
                    mReport.pictures.add(mLastAskedPicture.toString());
                    new UpdateReportTask().execute(mReport);
                } else {
                    mPictureToAdd = mLastAskedPicture;
                }
                mLastAskedPicture = null;
            }
        }
    }

    private void setReport(final Report report) {
        Log.v(TAG, "setReport called with id " + report.sqlId);
        mReport = report;
        getActionBar().setTitle(mReport.riskName);
        mDescriptionFragment.setReport(mReport);
        mPicturesFragment.setReport(mReport);

        if (mPictureToAdd != null) {
            mReport.pictures.add(mPictureToAdd.toString());
            mPictureToAdd = null;

            new UpdateReportTask().execute(mReport);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (createFolder() != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // TODO
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mLastAskedPicture = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            mLastAskedPicture);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private File createFolder() {
        boolean result = (mReportFolder.mkdirs() || mReportFolder.isDirectory());
        return result ? mReportFolder : null;
    }

    private File createImageFile() throws IOException {
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                mReportFolder      /* directory */
        );

        return image;
    }
}
