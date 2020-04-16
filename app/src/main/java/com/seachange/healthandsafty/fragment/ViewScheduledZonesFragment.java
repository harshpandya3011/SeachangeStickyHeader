package com.seachange.healthandsafty.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.adapter.ScheduledZoneListAdapter;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.ScheduleSetUpHelper;
import com.seachange.healthandsafty.helper.View.AnimatedExpandableListView;
import com.seachange.healthandsafty.model.Schedule;
import com.seachange.healthandsafty.model.ScheduleGroup;
import com.seachange.healthandsafty.model.ScheduleSetting;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.model.ZoneCheckTimeRange;
import com.seachange.healthandsafty.utils.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ViewScheduledZonesFragment extends BaseFragment {

    private List<ScheduleGroup> expandableListTitle = new ArrayList<>();
    private AnimatedExpandableListView expandableListView;
    private HashMap<String, List<ZoneCheckTimeRange>> expandableListDetail = new HashMap<>();
    private SiteZone mZone;
    private int mCurrentTab;
    private boolean mFromTab;
    private ScheduledZoneListAdapter mAdapter;
    private Button mSaveBtn;
    private ScheduleSetUpHelper mScheduleHelper;
    private Schedule currentSchedule;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processData();
        mScheduleHelper = new ScheduleSetUpHelper(mCtx);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        processTitle();
        updateTitleByCurrentSchedule();
        processCurrentScheduleData();
        initButtons();
        updateSaveButton();
    }

    private void processTitle() {
        String week = mCtx.getResources().getStringArray(R.array.pref_week)[mCurrentTab];
        if (mFromTab) {
            if (getActivity() != null) {
                 ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Copy " + week + " From");
            }
        } else {
            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Copy " + week + " To");
            }
        }
    }

    public void initData(SiteZone zone, int tab, boolean fromTab) {
        mZone = zone;
        mCurrentTab = tab;
        mFromTab = fromTab;
    }

    private void processData() {
        expandableListTitle.add(new ScheduleGroup("Monday", false, 1, true));
        expandableListTitle.add(new ScheduleGroup("Tuesday", false, 2, true));
        expandableListTitle.add(new ScheduleGroup("Wednesday", false, 3, true));
        expandableListTitle.add(new ScheduleGroup("Thursday", false, 4, true));
        expandableListTitle.add(new ScheduleGroup("Friday", false, 5, true));
        expandableListTitle.add(new ScheduleGroup("Saturday", false, 6, true));
        expandableListTitle.add(new ScheduleGroup("Sunday", false, 7, true));
        expandableListTitle.remove(mCurrentTab);
    }

    private void updateTitleByCurrentSchedule() {
        ScheduleSetting savedScheduleSetting = mScheduleHelper.getCurrentScheduleSettingByZoneId(mZone.getZone_id(), true);
        if (savedScheduleSetting == null) {
            for (int i = 0; i < expandableListTitle.size(); i++) {
                 expandableListTitle.get(i).setWithSchedule(false);
            }
        } else {
            for (int i = 0; i < expandableListTitle.size(); i++) {
                boolean isEmpty = true;
                for (int j = 0; j < savedScheduleSetting.getSchedules().size(); j++) {
                    Schedule schedule = savedScheduleSetting.getSchedules().get(j);
                    if (schedule.getDayOfWeek() !=null && schedule.getDayOfWeek() == expandableListTitle.get(i).getTabDay() && schedule.getZoneCheckTimeRanges().size() > 0) {
                        Logger.info("this is falese: " + schedule.getDayOfWeek() + "title: " + expandableListTitle.get(i).getTabDay());
                        isEmpty = false;
                    }
                }
                if (isEmpty) expandableListTitle.get(i).setWithSchedule(false);
            }
        }
    }

    private void processCurrentScheduleData() {
        expandableListDetail = new LinkedHashMap<>();
        ScheduleSetting savedScheduleSetting = mScheduleHelper.getCurrentScheduleSettingByZoneId(mZone.getZone_id(), true);
        if (savedScheduleSetting == null) {
            for (int i = 0; i < expandableListTitle.size(); i++) {
                List<ZoneCheckTimeRange> list = new ArrayList<>();
                list.add(new ZoneCheckTimeRange());
                expandableListDetail.put(expandableListTitle.get(i).getTitle(), list);
            }

        } else {
            for (int i = 0; i < expandableListTitle.size(); i++) {
                List<ZoneCheckTimeRange> list = new ArrayList<>();
                for (int j = 0; j < savedScheduleSetting.getSchedules().size(); j++) {
                    Schedule schedule = savedScheduleSetting.getSchedules().get(j);
                    if (schedule.getDayOfWeek() !=null && schedule.getDayOfWeek() == expandableListTitle.get(i).getTabDay()) {
                        list = schedule.getZoneCheckTimeRanges();
                    }
                }
                if (list.size() == 0) list.add(new ZoneCheckTimeRange());
                expandableListDetail.put(expandableListTitle.get(i).getTitle(), list);
            }
        }
        updateAdapter();
    }


    private void updateAdapter() {
        mAdapter = new ScheduledZoneListAdapter(mCtx, mFromTab, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_scheduled_zones, container, false);
        expandableListView = view.findViewById(R.id.expandableListViewScheduledZones);
        mSaveBtn = view.findViewById(R.id.schedule_copy_save_button);
        return view;
    }

    private void initButtons() {
        if (expandableListDetail.size() == 0) return;
        for (int i = 0; i < expandableListTitle.size(); i++) {
            expandableListView.expandGroup(i);
        }

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {

            if (mFromTab) {
                for (int i = 0; i < expandableListTitle.size(); i++) {
                    if (groupPosition == i) {
                        if (expandableListTitle.get(i).getWithSchedule()) {
                            expandableListTitle.get(i).setSelected(true);
                        }
                    } else {
                        expandableListTitle.get(i).setSelected(false);
                    }
                }
                mAdapter.refreshContent(expandableListTitle);
                mAdapter.notifyDataSetChanged();
            } else {
                for (int i = 0; i < expandableListTitle.size(); i++) {
                    if (groupPosition == i) {
                        if (expandableListTitle.get(i).getSelected()) {
                            expandableListTitle.get(i).setSelected(false);
                        } else {
                            expandableListTitle.get(i).setSelected(true);
                        }
                    }
                }
                mAdapter.refreshContent(expandableListTitle);
                mAdapter.notifyDataSetChanged();
            }

            updateSaveButton();
            return true;
        });

        mSaveBtn.setOnClickListener(v -> {
            if (mFromTab) {
                performCopyFrom();
            } else {
                performCopyTo();
            }
            closePage();
        });
    }

    private void performCopyTo() {
        ScheduleSetting savedScheduleSetting = mScheduleHelper.getCurrentScheduleSettingByZoneId(mZone.getZone_id(), true);
        if (savedScheduleSetting != null) {
            currentSchedule = savedScheduleSetting.getScheduleByDay(mCurrentTab + 1);
            if (currentSchedule != null && currentSchedule.getZoneCheckTimeRanges().size() > 0) {
                for (int i = 0; i < expandableListTitle.size(); i++) {
                    ScheduleGroup scheduleGroup = expandableListTitle.get(i);
                    if (scheduleGroup.getSelected()) {
                        mScheduleHelper.AddZoneTimeRangesToExistingZoneSettingRegular(mZone, new DateUtil().getDayPosition(scheduleGroup.getTitle()), currentSchedule.getZoneCheckTimeRanges());
                    }
                }
            }
        }
    }

    private void performCopyFrom() {
        for (int i = 0; i < expandableListTitle.size(); i++) {
            ScheduleGroup scheduleGroup = expandableListTitle.get(i);
            if (scheduleGroup.getSelected()) {
                ArrayList<ZoneCheckTimeRange> tmp = new ArrayList<>(expandableListDetail.get(expandableListTitle.get(i).getTitle()));
                if (tmp.size()>0) {
                    mScheduleHelper.AddZoneTimeRangesToExistingZoneSettingRegular(mZone, mCurrentTab + 1, tmp);
                }
            }
        }
    }

    private void updateSaveButton() {
        mSaveBtn.setEnabled(checkIfAnyDateSelected());
    }

    private boolean checkIfAnyDateSelected() {
        for (ScheduleGroup scheduleGroup : expandableListTitle) {
            if (scheduleGroup.getSelected()) {
                return true;
            }
        }
        return false;
    }

    private void closePage() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
