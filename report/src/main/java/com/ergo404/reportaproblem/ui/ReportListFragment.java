package com.ergo404.reportaproblem.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;
import com.ergo404.reportaproblem.database.ReportDbHandler;
import com.ergo404.reportaproblem.ui.adapters.ReportListAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.AnimateDismissAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;

import java.util.ArrayList;

/**
 * Created by pierrerossines on 07/06/2014.
 */
public class ReportListFragment extends ListFragment {

    private final static String TAG = ReportListFragment.class.getSimpleName();
    private class UpdateListTask extends AsyncTask<Void, Void, ArrayList<Report>> {

        @Override
        protected ArrayList<Report> doInBackground(Void... params) {
            ReportDbHandler handler = ReportDbHandler.getInstance(getActivity());
            ArrayList<Report> reportList = handler.getReports();
            return reportList;
        }

        @Override
        protected void onPostExecute(ArrayList<Report> list) {
            super.onPostExecute(list);
            mAdapter.updateReportsList(list);
        }
    }

    private class DeleteReportTask extends AsyncTask<ArrayList<Long>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<Long>... params) {
            ReportDbHandler handler = ReportDbHandler.getInstance(getActivity());
            boolean result = handler.deleteReports(params[0]);
            handler.closeDatabase();
            return result;
        }

        @Override
        protected void onPostExecute(Boolean deleted) {
            super.onPostExecute(deleted);
            if (deleted) {
                Toast.makeText(getActivity(), getString(R.string.toast_report_deleted), Toast.LENGTH_SHORT).show();
            }

            new UpdateListTask().execute();
        }
    }

    private ReportListAdapter mAdapter;
    private AnimateDismissAdapter mDismissAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportlist, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView emptyText = (TextView) view.findViewById(R.id.empty_text);
        String emptyTextS = getString(R.string.reportlist_empty);
        int imgPos = emptyTextS.indexOf("image");
        SpannableString spannable = new SpannableString(emptyTextS);
        ImageSpan imgSpan = new ImageSpan(getActivity(), R.drawable.ic_action_new_event);
        spannable.setSpan(imgSpan, imgPos, imgPos + 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        emptyText.setText(spannable);
        emptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportI = new Intent(getActivity(), ReportActivity.class);
                startActivity(reportI);
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent reportI = new Intent(getActivity(), ReportActivity.class);
                Report report = (Report) mAdapter.getItem(position);
                reportI.putExtra(ReportActivity.EXTRA_REPORTID, report.sqlId);
                startActivity(reportI);
            }
        });
        if (mAdapter == null) {
            mAdapter = new ReportListAdapter(getActivity());
        }
        mDismissAdapter = new AnimateDismissAdapter(mAdapter, new OnDismissCallback() {
            @Override
            public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
                ArrayList<Long> deleteSqlIds = new ArrayList<Long>();
                for (int position : reverseSortedPositions) {
                    deleteSqlIds.add(((Report) mAdapter.getItem(position)).sqlId);
                    mAdapter.remove(position);
                }
                new DeleteReportTask().execute(deleteSqlIds);
            }
        });
        mDismissAdapter.setAbsListView(getListView());
        setListAdapter(mDismissAdapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setSelector(android.R.color.darker_gray);
        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                updateCABTitle(mode);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.action_selectall:
                        selectAll();
                        updateCABTitle(mode);
                        return true;
                    case R.id.action_delete:
                        deleteSelectedItems();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            private void selectAll() {
                for (int i = 0; i < getListView().getCount(); i++) {
                    getListView().setItemChecked(i, true);
                }
            }

            private void updateCABTitle(ActionMode mode) {
                int checkedItemCount = getListView().getCheckedItemCount();
                String selectedItems = getString(R.string.reports_selected);
                selectedItems = selectedItems.replace("%nb", String.valueOf(checkedItemCount));
                mode.setTitle(selectedItems);

                MenuItem item = mode.getMenu().findItem(R.id.action_selectall);
                if (checkedItemCount == getListView().getCount()) {
                    item.setVisible(false);
                } else {
                    item.setVisible(true);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.report_list_contextual, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
    }

    private void deleteSelectedItems() {
        SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
        ArrayList<Integer> deleteList = new ArrayList<Integer>();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (checkedItems.get(i, false)) {
                deleteList.add(i);
            }
        }

        mDismissAdapter.animateDismiss(deleteList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setListAdapter(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        new UpdateListTask().execute();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(ContextMenu.NONE, ContextMenu.NONE, ContextMenu.NONE, R.string.delete_report);
    }

}
