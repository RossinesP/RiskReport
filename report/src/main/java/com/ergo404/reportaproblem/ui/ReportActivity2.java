package com.ergo404.reportaproblem.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.ergo404.reportaproblem.R;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ReportActivity2 extends FragmentActivity  {
    private final static String TAG = "ReportActivity2";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_SEND_REPORT = 2;
    private final static int MAX_BAR_VALUES = 5;
    private final static String mBaseDir = Environment.getExternalStorageDirectory() + File.separator + "RapportIncident";
    private String mDirName;

    private static final int NUM_PAGES = 2;

    private ImageView mSelector0;
    private ImageView mSelector1;

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    private ArrayList<Uri> mImagePathList;
    private Uri mLastAskedPicture;


    public ArrayList<Uri> getPictureList() {
        return new ArrayList<Uri>(mImagePathList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDirName = createFolder();
        mImagePathList = new ArrayList<Uri>();


        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mSelector0 = (ImageView) findViewById(R.id.pager_position0);
        mSelector1 = (ImageView) findViewById(R.id.pager_position1);

        mSelector0.setSelected(true);
        mSelector1.setSelected(false);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mSelector0.setSelected(true);
                    mSelector1.setSelected(false);
                } else {
                    mSelector0.setSelected(false);
                    mSelector1.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("imageurilist", mImagePathList);
        if (mLastAskedPicture != null) outState.putParcelable("askeduri", mLastAskedPicture);
        outState.putString("dir", mDirName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Parcelable> savedImageList = savedInstanceState.getParcelableArrayList("imageurilist");
        if (savedImageList != null) {
            mImagePathList.clear();
            for (Parcelable parcelable : savedImageList) {
                mImagePathList.add((Uri) parcelable);
            }
        }
        Uri lastAskedImage = savedInstanceState.getParcelable("askeduri");
        if (lastAskedImage != null) {
            mLastAskedPicture = lastAskedImage;
        }
        String dirName = savedInstanceState.getString("dir");
        if (dirName != null) {
            mDirName = dirName;
        }
    }

    private boolean writeReport(String report) {
        Log.v(TAG, "Writing the report");
        if (createFolder() == null) {
            Log.v(TAG, "Error while creating the folder");
            return false;
        }

        boolean result = false;
        try {
            PrintWriter out = new PrintWriter(mDirName + File.separator + "report.json");
            out.print(report);
            out.close();
            result = true;
        } catch (Exception e) {
            Log.e(TAG, "Exception while writing the report");
            e.printStackTrace();
        }
        Log.v(TAG, "Report written : " + result);
        return result;
    }

    private void sendFile(String report) {
        Log.v(TAG, "Sending the report");
        final Intent ei = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ei.setType("plain/text");
        ei.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_subject));
        ei.putExtra(Intent.EXTRA_TEXT, report);
        ArrayList<Uri> uris = new ArrayList<Uri>();
        File dir = new File(mDirName);
        for (File f : dir.listFiles()) {
            if (f.getAbsolutePath().equals(dir.getAbsolutePath())) continue;
            uris.add(Uri.fromFile(f));
        }

        ei.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivityForResult(Intent.createChooser(ei, getString(R.string.report_sending_chooser)), REQUEST_SEND_REPORT);
    }

    private String createFolder() {
        if (mDirName != null) {
            return mDirName;
        }

        int i = 0;
        while (new File(mBaseDir + File.separator + i).exists()) {
            i++;
        }

        File dir = new File(mBaseDir + File.separator + i);
        if (dir.mkdirs()) {
            mDirName = mBaseDir + File.separator + i;
        }

        return mDirName;
    }

    private void resetEntries() {
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_addpicture) {
            dispatchTakePictureIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = new File(mDirName);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.picture_taken), Toast.LENGTH_LONG).show();
                mImagePathList.add(mLastAskedPicture);
            } else {
                mLastAskedPicture = null;
            }
        } else if (requestCode == REQUEST_SEND_REPORT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.report_sent), Toast.LENGTH_LONG).show();
                resetEntries();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fManager) {
            super(fManager);
        }


        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
