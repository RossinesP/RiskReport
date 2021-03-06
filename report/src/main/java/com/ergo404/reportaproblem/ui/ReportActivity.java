package com.ergo404.reportaproblem.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
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

import com.ergo404.reportaproblem.BuildConfig;
import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;
import com.ergo404.reportaproblem.database.ReportDbHandler;
import com.ergo404.reportaproblem.utils.Constants;
import com.ergo404.reportaproblem.utils.EmailUtils;
import com.ergo404.reportaproblem.utils.ReportUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

/**
 * Created by pierrerossines on 09/06/2014.
 */
public class ReportActivity extends FragmentActivity implements DescriptionFragment.OnUpdateReportListener, PicturesFragment.OnPictureUpdatedListener, ReportProvider, PictureTaker {
    public final static String EXTRA_REPORTID = "reportid";

    private final static String EXTRA_LASTPICTURE = "lastPicture";
    private final static String TAG = ReportActivity.class.getSimpleName();
    private final static int REQUEST_IMAGE_CAPTURE = 101;

    private Report mReport;
    private Uri mLastAskedPicture;
    private Uri mPictureToAdd;


    private final static int DESCRIPTION_FRAGMENT_POS = 0;
    private final static int PICTURES_FRAGMENT_POS = 1;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (savedInstanceState != null) {
            String lastPicture = savedInstanceState.getString(EXTRA_LASTPICTURE);
            if (lastPicture != null) {
                mLastAskedPicture = Uri.parse(lastPicture);
            }

            Report savedReport = Report.getReport(savedInstanceState);
            if (!savedReport.isEmpty()) {
                setReport(savedReport);
            }
        }

        Intent callingIntent = getIntent();
        if (callingIntent != null) {
            long sqliteId = callingIntent.getLongExtra(EXTRA_REPORTID, -1);
            if (sqliteId != -1) {
                new LoadReportTask().execute(sqliteId);
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            case R.id.action_sendreport:
                sendReport();
                //new GenerateAndSendReport().execute(mReport);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mReport.writeReport(outState);

        if (mLastAskedPicture != null) {
            outState.putString(EXTRA_LASTPICTURE, mLastAskedPicture.toString());
        }
    }

    @Override
    public void takePicture() {
        dispatchTakePictureIntent();
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
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_IMAGE_CAPTURE) {
                final boolean isCamera;
                if(data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if(action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri = null;
                if(isCamera) {
                    selectedImageUri = mLastAskedPicture;
                    mLastAskedPicture = null;
                    if (BuildConfig.DEBUG) Log.v(TAG, "Got a file from the camera : " + selectedImageUri.toString());
                } else {
                    if (data != null) {
                        File file = new File(getPath(data.getData()));
                        selectedImageUri = Uri.fromFile(file);
                        if (BuildConfig.DEBUG) Log.v(TAG, "Got a file from the filemanager : " + selectedImageUri.toString());
                    }
                }

                if (mReport != null && selectedImageUri != null) {
                    Log.v(TAG, "Adding a last Asked picture in onActivityResult");
                    if (mReport != null) {
                        mReport.pictures.add(selectedImageUri.toString());
                        new UpdateReportTask().execute(mReport);
                        mPager.setCurrentItem(PICTURES_FRAGMENT_POS, true);
                    } else {
                        mPictureToAdd = selectedImageUri;
                    }

                }
            }
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param uri The Uri to query.
     * @author paulburke
     */
    public String getPath(final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat) {
            return getPathKitkat(uri);
        } else {
            return getPathLegacy(uri);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPathKitkat(final Uri uri) {
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(this, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        } else {
            return getPathLegacy(uri);
        }

        return null;
    }

    public String getPathLegacy(final Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(this, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void sendReport() {
        ExportDialogFragment.exportReport(new Report(mReport), getSupportFragmentManager());
    }

    private void setReport(final Report report) {
        mReport = report;
        getActionBar().setTitle(mReport.riskName);

        if (mPictureToAdd != null) {
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
        if (Constants.createReportDir()) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // TODO
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mLastAskedPicture = Uri.fromFile(photoFile);

                // Camera.
                final List<Intent> cameraIntents = new ArrayList<Intent>();
                final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                final PackageManager packageManager = getPackageManager();
                final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
                for(ResolveInfo res : listCam) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(captureIntent);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mLastAskedPicture);
                    cameraIntents.add(intent);
                }

                // Filesystem.
                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

                startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                Constants.REPORT_DIR      /* directory */
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

    private class LoadReportTask extends AsyncTask<Long, Void, Report> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        }
    }

    private class UpdateReportTask extends AsyncTask<Report, Void, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            mReport.sqlId = sqlId;
            setReport(mReport);
        }
    }

    private class DeleteReportTask extends AsyncTask<Report, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Report... params) {
            ReportDbHandler handler = ReportDbHandler.getInstance(ReportActivity.this);
            boolean result = handler.deleteReport(params[0].sqlId);
            handler.closeDatabase();
            return result;
        }
    }
}
