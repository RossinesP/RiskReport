package com.ergo404.reportaproblem.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ergo404.reportaproblem.R;
import com.ergo404.reportaproblem.Report;

/**
 * Created by pierrerossines on 09/06/2014.
 */
public class DescriptionFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private final static String TAG = DescriptionFragment.class.getSimpleName();
    private LinearLayout mDescriptionLayout;
    private LinearLayout mTargetsLayout;
    private LinearLayout mRisksLayout;
    private LinearLayout mFixLayout;

    private FrameLayout mDescriptionLayoutFrame;
    private FrameLayout mTargetsLayoutFrame;
    private FrameLayout mRisksLayoutFrame;
    private FrameLayout mFixLayoutFrame;

    private EditText mWorkUnit;
    private EditText mWorkPlace;
    private EditText mProblemName;
    private EditText mProblemDescription;

    private TextView mRiskEmployeesTitle;
    private SeekBar mRiskEmployeesBar;
    private TextView mRiskUsersTitle;
    private SeekBar mRiskUsersBar;
    private TextView mRiskThirdTitle;
    private SeekBar mRiskThirdBar;
    private TextView mWoundRiskTitle;
    private SeekBar mWoundRiskBar;
    private TextView mSicknessTitle;
    private SeekBar mSicknessRiskBar;
    private TextView mPhysHardTitle;
    private SeekBar mRiskPhysHardnessBar;
    private TextView mMentHardTitle;
    private SeekBar mRiskMentalHardnessBar;
    private TextView mProbabilityTitle;
    private SeekBar mProbabilityBar;
    private TextView mSolutionEasinessTitle;
    private SeekBar mSolutionEasiness;
    private TextView mSolutionCostTitle;
    private SeekBar mSolutionCost;

    private ImageView mExpandDescription;
    private ImageView mExpandTargets;
    private ImageView mExpandRisks;
    private ImageView mExpandFix;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            OnUpdateReportListener listener = (OnUpdateReportListener) activity;
        } catch (ClassCastException exception) {
            Log.v(TAG, "The parent activity must implement OnUpdateReportListener");
        }
        try {
            ReportProvider provider = (ReportProvider) activity;
        } catch (ClassCastException exception) {
            Log.v(TAG, "The parent activity must implement ReportProvider");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout descriptionHeaderLayout = (LinearLayout) view.findViewById(R.id.descriptionHeaderLayout);
        mDescriptionLayoutFrame = (FrameLayout) view.findViewById(R.id.descriptionLayoutFrame);
        mDescriptionLayout = (LinearLayout) view.findViewById(R.id.descriptionLayout);
        descriptionHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDescription();
            }
        });

        LinearLayout targetsHeaderLayout = (LinearLayout) view.findViewById(R.id.targetsHeaderLayout);
        mTargetsLayoutFrame = (FrameLayout) view.findViewById(R.id.targetsLayoutFrame);
        mTargetsLayout = (LinearLayout) view.findViewById(R.id.targetsLayout);
        targetsHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTargets();
            }
        });

        LinearLayout risksHeaderLayout = (LinearLayout) view.findViewById(R.id.risksHeaderLayout);
        mRisksLayoutFrame = (FrameLayout) view.findViewById(R.id.risksLayoutFrame);
        mRisksLayout = (LinearLayout) view.findViewById(R.id.risksLayout);
        risksHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRisks();
            }
        });

        LinearLayout fixHeaderLayout = (LinearLayout) view.findViewById(R.id.fixHeaderLayout);
        mFixLayoutFrame = (FrameLayout) view.findViewById(R.id.fixLayoutFrame);
        mFixLayout = (LinearLayout) view.findViewById(R.id.fixLayout);
        fixHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFix();
            }
        });

        mProblemName = (EditText) view.findViewById(R.id.risk_name);
        mProblemDescription = (EditText) view.findViewById(R.id.problem_description);
        mWorkPlace = (EditText) view.findViewById(R.id.work_place);
        mWorkUnit = (EditText) view.findViewById(R.id.work_unit);

        mRiskEmployeesTitle = (TextView) view.findViewById(R.id.employees_risk);
        mRiskEmployeesBar = (SeekBar) view.findViewById(R.id.employees_risk_bar);
        mRiskEmployeesBar.setOnSeekBarChangeListener(this);
        mRiskUsersTitle = (TextView) view.findViewById(R.id.users_risk);
        mRiskUsersBar = (SeekBar) view.findViewById(R.id.users_risk_bar);
        mRiskUsersBar.setOnSeekBarChangeListener(this);
        mRiskThirdTitle = (TextView) view.findViewById(R.id.third_party_risk);
        mRiskThirdBar = (SeekBar) view.findViewById(R.id.third_party_risk_bar);
        mRiskThirdBar.setOnSeekBarChangeListener(this);
        mWoundRiskTitle = (TextView) view.findViewById(R.id.wound_risk);
        mWoundRiskBar = (SeekBar) view.findViewById(R.id.wound_risk_bar);
        mWoundRiskBar.setOnSeekBarChangeListener(this);
        mSicknessTitle = (TextView) view.findViewById(R.id.sickness_risk);
        mSicknessRiskBar = (SeekBar) view.findViewById(R.id.sickness_risk_bar);
        mSicknessRiskBar.setOnSeekBarChangeListener(this);
        mPhysHardTitle = (TextView) view.findViewById(R.id.physical_hardness_risk);
        mRiskPhysHardnessBar = (SeekBar) view.findViewById(R.id.physical_hardness_risk_bar);
        mRiskPhysHardnessBar.setOnSeekBarChangeListener(this);
        mMentHardTitle = (TextView) view.findViewById(R.id.mental_hardness_risk);
        mRiskMentalHardnessBar = (SeekBar) view.findViewById(R.id.mental_hardness_risk_bar);
        mRiskMentalHardnessBar.setOnSeekBarChangeListener(this);
        mProbabilityTitle = (TextView) view.findViewById(R.id.probability);
        mProbabilityBar = (SeekBar) view.findViewById(R.id.probability_bar);
        mProbabilityBar.setOnSeekBarChangeListener(this);
        mSolutionEasinessTitle = (TextView) view.findViewById(R.id.repair_easiness);
        mSolutionEasiness = (SeekBar) view.findViewById(R.id.repair_easiness_bar);
        mSolutionEasiness.setOnSeekBarChangeListener(this);
        mSolutionCostTitle = (TextView) view.findViewById(R.id.repair_cost);
        mSolutionCost = (SeekBar) view.findViewById(R.id.repair_cost_bar);
        mSolutionCost.setOnSeekBarChangeListener(this);

        mExpandDescription = (ImageView) view.findViewById(R.id.expand_description);
        mExpandTargets = (ImageView) view.findViewById(R.id.expand_targets);
        mExpandRisks = (ImageView) view.findViewById(R.id.expand_risks);
        mExpandFix = (ImageView) view.findViewById(R.id.expand_fix);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
        setReport(((ReportProvider) getActivity()).getReport());
        expandDescription();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
        updateData();
    }

    public void notifyReportUpdated() {
        Log.v(TAG, "notifyReportUpdated called");
        if (isResumed()) {
            setReport(((ReportProvider) getActivity()).getReport());
        }
    }

    public void updateData() {
        ((OnUpdateReportListener) getActivity()).updateData(mProblemName.getText().toString(),
                mProblemDescription.getText().toString(),
                mWorkUnit.getText().toString(),
                mWorkPlace.getText().toString(),
                mRiskEmployeesBar.getProgress(),
                mRiskUsersBar.getProgress(),
                mRiskThirdBar.getProgress(),
                mWoundRiskBar.getProgress(),
                mSicknessRiskBar.getProgress(),
                mRiskPhysHardnessBar.getProgress(),
                mRiskMentalHardnessBar.getProgress(),
                mProbabilityBar.getProgress(),
                mSolutionCost.getProgress(),
                mSolutionEasiness.getProgress());
    }

    public void setReport(Report report) {
        mProblemName.setText(report.riskName);
        mProblemDescription.setText(report.riskDescription);
        mWorkPlace.setText(report.workPlace);
        mWorkUnit.setText(report.workUnit);

        mRiskEmployeesBar.setProgress(report.riskEmployees);
        mRiskUsersBar.setProgress(report.riskUsers);
        mRiskThirdBar.setProgress(report.riskThirdParty);
        mWoundRiskBar.setProgress(report.woundRisk);
        mSicknessRiskBar.setProgress(report.sicknessRisk);
        mRiskPhysHardnessBar.setProgress(report.physicalHardness);
        mRiskMentalHardnessBar.setProgress(report.mentalHardness);
        mProbabilityBar.setProgress(report.probability);
        mSolutionCost.setProgress(report.fixCost);
        mSolutionEasiness.setProgress(report.fixEasiness);
        updateTitles();
    }

    private void updateTitles() {
        mRiskEmployeesTitle.setText(Report.getEmployeesString(getActivity(), mRiskEmployeesBar.getProgress()));
        mRiskUsersTitle.setText(Report.getUsersString(getActivity(), mRiskUsersBar.getProgress()));
        mRiskThirdTitle.setText(Report.getThirdString(getActivity(), mRiskThirdBar.getProgress()));
        mWoundRiskTitle.setText(Report.getWoundString(getActivity(), mWoundRiskBar.getProgress()));
        mSicknessTitle.setText(Report.getSicknessString(getActivity(), mSicknessRiskBar.getProgress()));
        mPhysHardTitle.setText(Report.getPhysicalHardnessString(getActivity(), mRiskPhysHardnessBar.getProgress()));
        mMentHardTitle.setText(Report.getMentalHardnessString(getActivity(), mRiskMentalHardnessBar.getProgress()));
        mProbabilityTitle.setText(Report.getProbabilityString(getActivity(), mProbabilityBar.getProgress()));
        mSolutionCostTitle.setText(Report.getFixCostString(getActivity(), mSolutionCost.getProgress()));
        mSolutionEasinessTitle.setText(Report.getFixeasinessString(getActivity(), mSolutionEasiness.getProgress()));
    }

    public void expand(final View frameView, final View layout) {
        frameView.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int measuredHeight = frameView.getMeasuredHeight();

        frameView.getLayoutParams().height = 0;
        frameView.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                frameView.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(measuredHeight * interpolatedTime);
                frameView.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        Animation alpha = AnimationUtils.loadAnimation(layout.getContext(), android.R.anim.fade_in);
        int duration = (int)(measuredHeight / frameView.getContext().getResources().getDisplayMetrics().density);
        alpha.setDuration(duration);
        a.setDuration(duration);
        layout.startAnimation(alpha);
        frameView.startAnimation(a);
    }

    public void collapse(final View frameView, final View layout) {
        if (frameView.getVisibility() == View.GONE) return;
        final int initialHeight = frameView.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    frameView.setVisibility(View.GONE);
                }else{
                    frameView.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    frameView.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        Animation alpha = AnimationUtils.loadAnimation(layout.getContext(), android.R.anim.fade_out);
        int duration = (int)(initialHeight / frameView.getContext().getResources().getDisplayMetrics().density);
        alpha.setDuration(duration);
        a.setDuration(duration);
        layout.startAnimation(alpha);
        frameView.startAnimation(a);
    }

    private void expandDescription() {
        mExpandDescription.setImageResource(R.drawable.ic_action_collapse);
        expand(mDescriptionLayoutFrame, mDescriptionLayout);

        collapseFix();
        collapseTargets();
        collapseRisks();
    }

    private void collapseDescription() {
        mExpandDescription.setImageResource(R.drawable.ic_action_expand);
        collapse(mDescriptionLayoutFrame, mDescriptionLayout);
    }

    private void toggleDescription() {
        if (mDescriptionLayoutFrame.getVisibility() == FrameLayout.GONE) {
            expandDescription();
        } else {
            collapseDescription();
        }
    }

    private void expandTargets() {
        mExpandTargets.setImageResource(R.drawable.ic_action_collapse);
        expand(mTargetsLayoutFrame, mTargetsLayout);

        collapseDescription();
        collapseRisks();
        collapseFix();
    }

    private void collapseTargets() {
        mExpandTargets.setImageResource(R.drawable.ic_action_expand);
        collapse(mTargetsLayoutFrame, mTargetsLayout);
    }

    private void toggleTargets() {
        if (mTargetsLayoutFrame.getVisibility() == LinearLayout.GONE) {
            expandTargets();
        } else {
            collapseTargets();
        }
    }

    private void expandRisks() {
        mExpandRisks.setImageResource(R.drawable.ic_action_collapse);
        expand(mRisksLayoutFrame, mRisksLayout);

        collapseDescription();
        collapseTargets();
        collapseFix();
    }

    private void collapseRisks() {
        mExpandRisks.setImageResource(R.drawable.ic_action_expand);
        collapse(mRisksLayoutFrame, mRisksLayout);
    }

    private void toggleRisks() {
        if (mRisksLayoutFrame.getVisibility() == LinearLayout.GONE) {
            expandRisks();
        } else {
            collapseRisks();
        }
    }

    private void expandFix() {
        mExpandFix.setImageResource(R.drawable.ic_action_collapse);
        expand(mFixLayoutFrame, mFixLayout);

        collapseDescription();
        collapseTargets();
        collapseRisks();
    }

    private void collapseFix() {
        mExpandFix.setImageResource(R.drawable.ic_action_expand);
        collapse(mFixLayoutFrame, mFixLayout);
    }

    private void toggleFix() {
        if (mFixLayoutFrame.getVisibility() == LinearLayout.GONE) {
            expandFix();
        } else {
            collapseFix();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateTitles();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface OnUpdateReportListener {
        public void updateData(String riskName, String riskDescription,
                               String workUnit, String workPlace,
                               int riskEmployees, int riskUsers,
                               int riskThird, int woundRisk,
                               int sicknessRisk, int physicalHardness,
                               int mentalHardness, int probability,
                               int fixCost, int fixEasiness);
    }
}
