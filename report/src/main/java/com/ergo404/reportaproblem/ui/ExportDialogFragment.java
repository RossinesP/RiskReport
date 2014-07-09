package com.ergo404.reportaproblem.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.ergo404.reportaproblem.R;

/**
 * Created by Pierre on 09/07/2014.
 */
public class ExportDialogFragment extends DialogFragment {

    private int mNumReports;

    public void setNumReports(int numReports) {
        mNumReports = numReports;
    }

    public void setProgress(int progress) {
        ((ProgressDialog) getDialog()).setProgress(progress);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog.Builder builder = new ProgressDialog.Builder(getActivity());
        if (mNumReports == 1) {
            builder.setTitle(R.string.creating_report);
        } else {
            builder.setTitle(R.string.creating_reports);
        }

        return builder.create();
    }
}
