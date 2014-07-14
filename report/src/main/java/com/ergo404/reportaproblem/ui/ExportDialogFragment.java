package com.ergo404.reportaproblem.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.ergo404.reportaproblem.BuildConfig;
import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;
import com.ergo404.reportaproblem.utils.Constants;
import com.ergo404.reportaproblem.utils.EmailUtils;
import com.ergo404.reportaproblem.utils.ReportUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Pierre on 09/07/2014.
 */
public class ExportDialogFragment extends DialogFragment {
    private final static String TAG = ExportDialogFragment.class.getSimpleName();
    private ArrayList<Report> mReports;
    private ExportAsyncTask mExportTask;

    public ExportDialogFragment() {
        mReports = new ArrayList<Report>();
    }

    public static void exportReport(Report report, FragmentManager fragmentManager) {
        ExportDialogFragment fragment = new ExportDialogFragment();
        ArrayList<Report> reports = new ArrayList<Report>();
        reports.add(report);
        exportReports(reports, fragmentManager);
    }
    public static void exportReports(ArrayList<Report> reports, FragmentManager fragmentManager) {
        ExportDialogFragment fragment = new ExportDialogFragment();
        fragment.setReports(reports);
        fragment.show(fragmentManager, "exportdialog");
    }

    public void setReports(ArrayList<Report> reports) {
        mReports.clear();
        mReports.addAll(reports);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        if (mReports.size()  == 1) {
            dialog.setTitle(R.string.creating_report);
            dialog.setMessage(getString(R.string.creating_report));
        } else {
            dialog.setTitle(R.string.creating_reports);
            dialog.setMessage(getString(R.string.creating_reports));
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminate(true);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExportTask == null) {
            mExportTask = new ExportAsyncTask(getActivity());
            mExportTask.execute(mReports);
        }
    }

    private class ExportAsyncTask extends AsyncTask<ArrayList<Report>, Integer, ArrayList<String>> {
        private WeakReference<Context> mContext;
        public ExportAsyncTask (Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<Report>... reportsArgs) {
            Constants.createReportDir();
            ArrayList<Report> reports = reportsArgs[0];
            ArrayList<String> filePaths = new ArrayList<String>();
            for (Report report : reports) {
                String filePath = ReportUtils.writeHTMLReport(mContext.get(), report, Constants.REPORT_DIR.getAbsolutePath());
                filePaths.add(filePath);
            }
            return filePaths;
        }

        @Override
        protected void onPostExecute(ArrayList<String> filePaths) {
            super.onPostExecute(filePaths);
            for (String path : filePaths) {
                if (BuildConfig.DEBUG) Log.v(TAG, "onPostExecute path : " + path);
            }

            if (getActivity() != null) {
                EmailUtils.email(getActivity(), "", "", getString(R.string.report_subject), "", filePaths);
            }
            dismiss();
        }
    };

}
