package com.ergo404.reportaproblem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ergo404.reportaproblem.R;

/**
 * Created by pierrerossines on 07/06/2014.
 */
public class ReportListActivity extends FragmentActivity {
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
            Intent reportI = new Intent(this, ReportActivity.class);
            startActivity(reportI);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
