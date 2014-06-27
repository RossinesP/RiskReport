package com.ergo404.reportaproblem.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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
public class ReportActivity extends FragmentActivity implements DescriptionFragment.OnUpdateReportListener, PicturesFragment.OnPictureUpdatedListener, ReportProvider {
    public final static String EXTRA_REPORTID = "reportid";

    private final static String EXTRA_LASTPICTURE = "lastPicture";
    private final static String TAG = ReportActivity.class.getSimpleName();
    private final static int REQUEST_IMAGE_CAPTURE = 101;

    private Report mReport;
    private Uri mLastAskedPicture;
    private Uri mPictureToAdd;

    private final File mReportFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "RiskReport");
    private final static int DESCRIPTION_FRAGMENT_POS = 0;
    private final static int PICTURES_FRAGMENT_POS = 1;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate called");
        mReport = new Report();
        setContentView(R.layout.activity_report);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(false, new ZoomOutPageTransformer());
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        actionBar.addTab(actionBar.newTab()
                .setText(R.string.label_description)
                .setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.label_pictures)
                .setTabListener(tabListener));

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

            Report savedReport = Report.getReport(savedInstanceState);
            if (!savedReport.isEmpty()) {
                Log.v(TAG, "Loading report from the saved instance state, id = " + savedReport.sqlId);
                setReport(savedReport);
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
    protected void onStop() {
        super.onStop();
        if (mReport.isEmpty()) {
            Log.v(TAG, "Deleting the empty report");
            new DeleteReportTask().execute(mReport);
        } else {
            Log.v(TAG, "Saving the report");
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
            case R.id.action_sendreport:
                new GenerateAndSendReport().execute(mReport);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState()");
        mReport.writeReport(outState);

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
                if (mReport != null) {
                    mReport.pictures.add(mLastAskedPicture.toString());
                    new UpdateReportTask().execute(mReport);
                    mPager.setCurrentItem(PICTURES_FRAGMENT_POS, true);
                } else {
                    mPictureToAdd = mLastAskedPicture;
                }
                mLastAskedPicture = null;
            }
        }
    }

    private void setReport(final Report report) {
        Log.v(TAG, "setReport called with id " + report.sqlId + ", report title is " + report.riskName);
        mReport = report;
        getActionBar().setTitle(mReport.riskName);

        if (mPictureToAdd != null) {
            Log.v(TAG, "Added a picture !");
            mReport.pictures.add(mPictureToAdd.toString());
            mPictureToAdd = null;
            mPager.setCurrentItem(PICTURES_FRAGMENT_POS, true);

            new UpdateReportTask().execute(mReport);
        }

        ((DescriptionFragment) mPagerAdapter.getItem(DESCRIPTION_FRAGMENT_POS)).notifyReportUpdated();
        ((PicturesFragment) mPagerAdapter.getItem(PICTURES_FRAGMENT_POS)).notifyReportUpdated();
    }

    @Override
    public Report getReport() {
        return mReport;
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

    public class PagerAdapter extends FragmentPagerAdapter {

        private PicturesFragment mPicturesFragment;
        private DescriptionFragment mDescriptionFragment;

        public PagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case DESCRIPTION_FRAGMENT_POS:
                    if (mDescriptionFragment == null) {
                        mDescriptionFragment = new DescriptionFragment();
                    }
                    return mDescriptionFragment;
                case PICTURES_FRAGMENT_POS:
                    if (mPicturesFragment == null) {
                        mPicturesFragment = new PicturesFragment();
                    }
                    return mPicturesFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case DESCRIPTION_FRAGMENT_POS:
                    return getString(R.string.label_description);
                case PICTURES_FRAGMENT_POS:
                    return getString(R.string.label_pictures);
                default:
                    return "";
            }
        }
    }

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private class GenerateAndSendReport extends AsyncTask<Report, Void, Void> {

        @Override
        protected Void doInBackground(Report... reports) {
            createFolder();
            Report report = reports[0];
            String filePath = report.writePDFReport(mReportFolder.getAbsolutePath());

            Report.email(ReportActivity.this, "", "", getString(R.string.report_subject), "", filePath);
            return null;
        }
    }

    private class LoadReportTask extends AsyncTask<Long, Void, Report> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "Loading the report from the DB");
        }

        @Override
        protected Report doInBackground(Long... params) {
            Log.v(TAG, "Loading report with id " + params[0]);
            ReportDbHandler dbHandler = ReportDbHandler.getInstance(ReportActivity.this);
            Report result = dbHandler.getReport(params[0]);
            dbHandler.closeDatabase();
            return result;
        }

        @Override
        protected void onPostExecute(Report report) {
            super.onPostExecute(report);
            Log.i(TAG, "Loaded report with id " + report.sqlId);
            setReport(report);
        }
    }

    private class UpdateReportTask extends AsyncTask<Report, Void, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG , "Saving the report to the DB");
        }

        @Override
        protected Long doInBackground(final Report... params) {
            Report report = params[0];
            Log.v(TAG, "Saving report with id " + report.sqlId + " and name " + report.riskName);
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
            Log.v(TAG , "Deleting report with id " + params[0].sqlId + " and name " + params[0].riskName);
            ReportDbHandler handler = ReportDbHandler.getInstance(ReportActivity.this);
            boolean result = handler.deleteReport(params[0].sqlId);
            handler.closeDatabase();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean deleted) {
            super.onPostExecute(deleted);
            Log.v(TAG , "Report deleted");
        }
    }
}
