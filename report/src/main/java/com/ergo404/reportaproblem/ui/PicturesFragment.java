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

import java.io.File;
import java.util.ArrayList;

/**
 * Created by pierrerossines on 14/06/2014.
 */
public class PicturesFragment extends Fragment {
    private final static String TAG = PicturesFragment.class.getSimpleName();
    private GridView mGridView;
    private PictureListAdapter mPicturesAdapter;
    private OnPictureUpdatedListener mPicturesUpdatedListener;

    private class DeletePictureTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {
            ArrayList<String> deletedPictures = new ArrayList<String>();
            for (String picture : params[0]) {
                Log.i(TAG, "Deleting picture " + Uri.parse(picture).getPath());
                File pictureF = new File(Uri.parse(picture).getPath());
                if (pictureF.delete()) {
                    deletedPictures.add(picture);
                } else {
                    Log.e(TAG, "Could not delete picture " + Uri.parse(picture).getPath());
                }
            }

            return deletedPictures;
        }

        @Override
        protected void onPostExecute(ArrayList<String> deleted) {
            super.onPostExecute(deleted);
            if (deleted.size() > 0) {
                Toast.makeText(getActivity(), getString(R.string.toast_image_deleted), Toast.LENGTH_SHORT).show();
            }
        }
    }

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
            mPicturesUpdatedListener = (OnPictureUpdatedListener) activity;
        } catch (ClassCastException cce) {
            Log.v(TAG, "Parent activity must implement OnPictureUpdatedListener");
            cce.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated");
        mPicturesAdapter = new PictureListAdapter(getActivity());
        mGridView.setAdapter(mPicturesAdapter);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mGridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                updateCABTitle(mode);
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

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mPicturesUpdatedListener.updateData(mPicturesAdapter.getItems(), false);
    }

    private void deleteSelectedItems() {
        SparseBooleanArray checkedItems = mGridView.getCheckedItemPositions();
        ArrayList<String> deletePictureList = new ArrayList<String>();
        for (int i = 0; i < mPicturesAdapter.getCount(); i++) {
            if (checkedItems.get(i, false)) {
                deletePictureList.add((String) mPicturesAdapter.getItem(i));
            }
        }
        new DeletePictureTask().execute(deletePictureList);

        ArrayList<String> newPictureList = new ArrayList<String>();
        newPictureList.addAll(mPicturesAdapter.getItems());
        newPictureList.removeAll(deletePictureList);

        mPicturesUpdatedListener.updateData(newPictureList, true);
    }

    private void selectAll() {
        for (int i = 0; i < mGridView.getCount(); i++) {
            mGridView.setItemChecked(i, true);
        }
    }

    private void updateCABTitle(ActionMode mode) {
        int checkedItemCount = mGridView.getCheckedItemCount();
        String selectedItems = getString(R.string.items_selected);
        selectedItems = selectedItems.replace("%nb", String.valueOf(checkedItemCount));
        mode.setTitle(selectedItems);

        MenuItem item = mode.getMenu().findItem(R.id.action_selectall);
        if (checkedItemCount == mGridView.getCount()) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }
    }

    public void setReport(Report report) {
        mPicturesAdapter.updatePictures(report.pictures);
        Log.v(TAG, "setReport : " + report.pictures.size() + " items in the new report, " + mPicturesAdapter.getCount()
                + " in the adapter, " + mGridView.getCount() + " in the gridview");
    }

    public interface OnPictureUpdatedListener {
        public void updateData(ArrayList<String> picturesList, boolean updateReport);
    }
}