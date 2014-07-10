package com.ergo404.reportaproblem.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ergo404.reportaproblem.R;

import java.lang.ref.WeakReference;

/**
 * Created by Pierre on 09/07/2014.
 */
public class ExportDialogFragment extends DialogFragment {

    private static ExportDialogFragment sInstance;

    public static ExportDialogFragment getInstance() {
        if (sInstance == null) sInstance = new ExportDialogFragment();
        return sInstance;
    }

    public ExportDialogFragment() {

    }

    private int mNumReports;

    private WeakReference<OnDismissListener> mListener;

    public void setNumReports(int numReports) {
        mNumReports = numReports;
    }

    public void setProgress(int progress) {
        ((ProgressDialog) getDialog()).setProgress(progress);
    }

    public void setDismissListener (OnDismissListener listener) {
         mListener = new WeakReference<OnDismissListener>(listener);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        if (mNumReports == 1) {
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
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null && mListener.get() != null) mListener.get().onDismiss();
    }

    public interface OnDismissListener {
        public void onDismiss();
    }
}
