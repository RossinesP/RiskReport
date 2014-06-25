package com.ergo404.reportaproblem.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;
import com.ergo404.reportaproblem.database.ReportDbHandler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by pierrerossines on 07/06/2014.
 */
public class ReportListActivity extends FragmentActivity implements ReportCreator{
    private final static String TAG = ReportListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportlist);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_createreport) {
            new CreateReportTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void createReport() {
        new CreateReportTask().execute();
    }

    private class CreateReportTask extends AsyncTask<Void, Void, Long> {
        @Override
        protected Long doInBackground(Void... params) {
            Report report = new Report();
            ReportDbHandler handler = ReportDbHandler.getInstance(ReportListActivity.this);
            long sqlid = handler.addOrUpdateReport(report);
            return sqlid;
        }

        @Override
        protected void onPostExecute(Long sqlid) {
            super.onPostExecute(sqlid);
            Log.v(TAG, "Starting the Report activity with attached sqlid " + sqlid);
            Intent reportI = new Intent(ReportListActivity.this, ReportActivity.class);
            reportI.putExtra(ReportActivity.EXTRA_REPORTID, sqlid);
            startActivity(reportI);
        }
    }
}
