package com.ergo404.reportaproblem.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;
import com.ergo404.reportaproblem.ui.adapters.PictureListAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.AnimateDismissAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by pierrerossines on 14/06/2014.
 */
public class PicturesFragment extends Fragment {
    private final static String TAG = PicturesFragment.class.getSimpleName();
    private GridView mGridView;
    private PictureListAdapter mPicturesAdapter;
    private AnimateDismissAdapter mAnimateDismissAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pictures, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGridView = (GridView) view.findViewById(R.id.picturesGrid);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            OnPictureUpdatedListener listener = (OnPictureUpdatedListener) activity;
        } catch (ClassCastException cce) {
            Log.v(TAG, "Parent activity must implement OnPictureUpdatedListener");
            cce.printStackTrace();
        }
        try {
            ReportProvider provider = (ReportProvider) activity;
        } catch (ClassCastException cce) {
            Log.v(TAG, "Parent activity must implement ReportProvider");
            cce.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPicturesAdapter = new PictureListAdapter(getActivity());
        OnDismissCallback mDismissCallback = new OnDismissCallback() {
            @Override
            public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
                for (int position: reverseSortedPositions) {
                    final String picturePath = (String) mPicturesAdapter.getItem(position);
                    listView.getChildAt(position).setMinimumHeight(300);
                    mPicturesAdapter.remove(position);
                    new DeletePictureTask().execute(picturePath);
                }

                ((OnPictureUpdatedListener) getActivity()).updateData(mPicturesAdapter.getItems(), true);
            }
        };
        mAnimateDismissAdapter = new AnimateDismissAdapter(mPicturesAdapter, mDismissCallback);
        mAnimateDismissAdapter.setAbsListView(mGridView);

        mGridView.setAdapter(mAnimateDismissAdapter);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mGridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                updateCABTitle(mode);
                if (checked) {
                    mPicturesAdapter.select(position);
                } else {
                    mPicturesAdapter.unselect(position);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.pictures_list_contextual, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deleteSelectedItems();
                        mode.finish();
                        return true;
                    case R.id.action_selectall:
                        selectAll();
                        updateCABTitle(mode);
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mPicturesAdapter.clearSelection();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setReport(((ReportProvider) getActivity()).getReport());
    }

    @Override
    public void onPause() {
        super.onPause();
        ((OnPictureUpdatedListener) getActivity()).updateData(mPicturesAdapter.getItems(), false);
    }

    public void notifyReportUpdated() {
        if (isResumed()) {
            setReport(((ReportProvider) getActivity()).getReport());
        }
    }

    private void deleteSelectedItems() {
        SparseBooleanArray checkedItems = mGridView.getCheckedItemPositions();
        ArrayList<Integer> deletePictureList = new ArrayList<Integer>();
        for (int i = 0; i < mPicturesAdapter.getCount(); i++) {
            if (checkedItems.get(i, false)) {
                deletePictureList.add(i);
            }
        }
        mAnimateDismissAdapter.animateDismiss(deletePictureList);
    }

    private void selectAll() {
        for (int i = 0; i < mGridView.getCount(); i++) {
            mGridView.setItemChecked(i, true);
        }
    }

    private void updateCABTitle(ActionMode mode) {
        int checkedItemCount = mGridView.getCheckedItemCount();
        String selectedItems = getString(R.string.pictures_selected);
        selectedItems = selectedItems.replace("%nb", String.valueOf(checkedItemCount));
        mode.setTitle(selectedItems);

        MenuItem item = mode.getMenu().findItem(R.id.action_selectall);
        if (checkedItemCount == mGridView.getCount()) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }
    }

    private void setReport(Report report) {
        mPicturesAdapter.updatePictures(report.pictures);
    }

    private class DeletePictureTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String path = params[0];
            Log.i(TAG, "Deleting picture " + Uri.parse(path).getPath());
            File pictureF = new File(Uri.parse(path).getPath());
            boolean result = pictureF.delete();
            if (!result) {
                Log.e(TAG, "Could not delete picture " + Uri.parse(path).getPath());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean deleted) {
            super.onPostExecute(deleted);
            if (deleted) {
                Toast.makeText(getActivity(), getString(R.string.toast_image_deleted), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnPictureUpdatedListener {
        public void updateData(ArrayList<String> picturesList, boolean updateReport);
    }
}