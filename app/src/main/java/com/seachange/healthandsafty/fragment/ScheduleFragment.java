package com.seachange.healthandsafty.fragment;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.microsoft.appcenter.analytics.Analytics;
import com.seachange.healthandsafty.activity.BaseActivity;
import com.seachange.healthandsafty.activity.LoginActivity;
import com.seachange.healthandsafty.db.DBCheck;
import com.seachange.healthandsafty.db.DBCheckViewModel;
import com.seachange.healthandsafty.helper.DBZoneCheckHelper;
import com.seachange.healthandsafty.helper.RequestErrorHandler;
import com.seachange.healthandsafty.helper.View.RequestErrorHandlerView;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManager;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManagerView;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.nfc.ui.NFCMainActivity;
import com.seachange.healthandsafty.presenter.SchedulePresenter;
import com.seachange.healthandsafty.View.ScheduleView;
import com.seachange.healthandsafty.activity.CaygoHomeActivity;
import com.seachange.healthandsafty.activity.CaygoZoneCheckActivity;
import com.seachange.healthandsafty.activity.ManagerTourActivity;
import com.seachange.healthandsafty.activity.QRCodeCaptureActivity;
import com.seachange.healthandsafty.activity.ZoneCheckActivity;
import com.seachange.healthandsafty.adapter.CustomExpandableListAdapter;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.HomeData;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.ManagerTour;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.helper.SiteCheckUtil;
import com.seachange.healthandsafty.helper.View.AnimatedExpandableListView;
import com.seachange.healthandsafty.helper.interfacelistener.DateDialogView;
import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.utils.Util;
import com.seachange.healthandsafty.utils.UtilStrings;

public class ScheduleFragment extends BaseFragment implements Observer, DateDialogView, ScheduleView, RequestErrorHandlerView, DBTourCheckManagerView {

    private AnimatedExpandableListView expandableListView;
    private CustomExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private boolean isManager, isLive, offlineTourComplete;
    private SiteCheck siteCheck;
    private String nextCheckTime, dateString;
    private ManagerTour managerTour;
    private static String tourName = "Home Tour";
    private Integer currentCheckPosition;
    private HomeData homeData;
    private TextView noScheduleTV;
    private RelativeLayout dateView;
    private SchedulePresenter mPresenter;
    private int mCurrentDay;
    private Button datePickerButton;
    private HashMap<String, List<SiteZone>> expandableListDetail;
    private RequestErrorHandler mErrorHandler;
    private ArrayList<DBZoneCheckModel> mDBList;
    private DBCheckViewModel mDBViewModel;
    private DBZoneCheckHelper mDBChecker;
    private DBTourCheckManager mDBTourCheckManager;
    private DBTourCheckModel mCurrentTourCheckModel = new DBTourCheckModel();
    private SiteCheck siteTour;
    private ArrayList<SiteZone> mZones;
    private CaygoSite mCaygoSite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            if (getActivity().getIntent().getExtras() != null)
                isManager = getActivity().getIntent().getExtras().getBoolean(UtilStrings.MANAGER_HOME);
        }
        mPresenter = new SchedulePresenter(this, mCtx);
        mCurrentDay = 0;
        mErrorHandler = new RequestErrorHandler(this, (CaygoHomeActivity)getActivity(), true, false);
        mDBList = new ArrayList<>();
        mDBChecker = new DBZoneCheckHelper(mCtx);

        //init site tour offline work
        mDBTourCheckManager = new DBTourCheckManager(this, (AppCompatActivity)getActivity());
        mDBTourCheckManager.retrieveAllTourChecks();
        ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleData();
        if (siteChecks!=null && siteChecks.size() > 0) {
            siteTour = siteChecks.get(0);
        }
        mCaygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
    }

    public void getDataFromParent() {
        if (getActivity() == null) return;
        homeData = ((CaygoHomeActivity) getActivity()).getmHomeData();
        tourName = homeData.tourName;
        managerTour = homeData.managerTour;
        siteCheck = homeData.siteCheck;
        nextCheckTime = homeData.nextCheckTime;
        currentCheckPosition = null;
        if (mCurrentDay == 0) loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentDay !=0) return;
        if (expandableListAdapter != null) {
            expandableListAdapter.notifyDataSetChanged();
        }
        HazardObserver.getInstance().addObserver(this);
        isLive = true;

        if (managerTour != null) {
            if (homeData.afterLastCheck) {
                if (managerTour.allZonesChecked()) {
                    currentCheckPosition = null;
                    PreferenceHelper.getInstance(mCtx).fetchScheduledDataNotify();
                }
            }
        }
        ((CaygoHomeActivity)getActivity()).getmHomeData().reloadDataFromCache();
        getDataFromParent();
        loadUnsyncedCheck();
        //retrieve data from, need to test this part.
        if (siteCheck!=null)
        mDBTourCheckManager.getCurrentTourCheck(siteTour.getScheduledSiteTourId());
    }

    private void loadUnsyncedCheck() {
        mDBViewModel = null;
        mDBViewModel = ViewModelProviders.of(getActivity()).get(DBCheckViewModel.class);
        mDBViewModel.getAllChecks().observe(getActivity(), checks -> {
            // Update the cached copy of the words in the adapter.
            Logger.info("DB - > load db message");
            if (checks==null) return;

            ArrayList<DBZoneCheckModel> arrayList = new ArrayList<>();
            Gson gson = new Gson();
            for (DBCheck check : checks) {
                Logger.info("DB - > " + check.getZoneCheck());
                DBZoneCheckModel dbZoneCheckModel = gson.fromJson(check.getZoneCheck(), DBZoneCheckModel.class);
                dbZoneCheckModel.setSync(check.isSync());
                arrayList.add(dbZoneCheckModel);
            }

            if (!arrayList.isEmpty()) {
                mDBList = arrayList;
                if (expandableListAdapter !=null) {
                    expandableListAdapter.reloadDBChecks(mDBList);
                    expandableListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        isLive = false;
        if (expandableListAdapter !=null) {
            expandableListAdapter.stopTimer();
        }
    }

    private void loadData() {
        noScheduleTV.setVisibility(View.GONE);

        if (siteCheck != null) {
            expandableListDetail = SiteCheckUtil.getData(siteCheck);
            expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
            Integer current = currentCheckPosition;
            getCurrentCheckPosition();

            if (isLive && expandableListAdapter != null) {
                if (current != null && current != currentCheckPosition) {
                    expandableListView.collapseGroupWithAnimation(current);
                }

                if (currentCheckPosition == null) {
                    expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail, siteCheck
                            , nextCheckTime, isManager, homeData, mCurrentDay, dateString);
                    expandableListView.setAdapter(expandableListAdapter);
                    if (mCurrentDay == 0) {
                        expandableListView.setSelection(expandableListAdapter.getGroupCount() - 1);
                    }

                } else {
                    expandableListAdapter.refresh(getActivity(), expandableListTitle, expandableListDetail, siteCheck
                            , nextCheckTime, isManager, homeData, mCurrentDay, dateString);
                    expandableListAdapter.notifyDataSetChanged();
                }
            } else {
                expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail, siteCheck,
                        nextCheckTime, isManager, homeData, mCurrentDay, dateString);
                expandableListView.setAdapter(expandableListAdapter);
            }

            if (mCurrentDay == 1 || mCurrentDay == 2){
                expandableListView.setSelection(0);
            }

            if (currentCheckPosition != null && mCurrentDay == 0 && expandableListView !=null && expandableListView.getExpandableListAdapter() !=null) {
                expandCurrentPositions();
            }
            hideLoadingSpinner();
        } else {
            hideLoadingSpinner();
            mProgressView.setVisibility(View.VISIBLE);
            if (mCurrentDay == 0) {

                if(PreferenceHelper.getInstance(mCtx).getScheduleStatus() == 2) {
                    if (!noScheduleTV.getText().equals(mCtx.getResources().getString(R.string.caygo_home_no_data_returned))) {
                        noScheduleTV.setText(R.string.caygo_home_no_data_found);
                    }
                } else if (PreferenceHelper.getInstance(mCtx).getScheduleStatus() == 1) {
                    noScheduleTV.setText(R.string.caygo_home_no_data_returned);
                } else {
                    noScheduleTV.setText(R.string.noscheduletoday);
                }
            } else {
                noScheduleTV.setText(R.string.noscheduleyet);
            }
            noScheduleTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoadingSpinner();
        getDataFromParent();
        expandableListView.setOnGroupExpandListener(groupPosition -> {
            if (groupPosition == 0) {
                if (isManager  && mCurrentDay == 0) {
                    if (!siteCheck.isComplete() && !offlineTourComplete) {
                        viewManagerTour();
                    }
                }
            }
        });

        expandableListView.setOnGroupCollapseListener(groupPosition -> {
            if (groupPosition == 0) {
//                if (isManager && !siteCheck.isComplete() && mCurrentDay == 0) {
//                    viewManagerTour();
//                }
                if (isManager  && mCurrentDay == 0) {
                    if (!siteCheck.isComplete() && !offlineTourComplete) {
                        viewManagerTour();
                    }
                }
            }
        });

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            SiteZone siteZone = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
            if (groupPosition>0) {
                final CheckTime checkTime = siteCheck.getCheckTimes().get(groupPosition - 1);
                final DBZoneCheckModel tmp = checkIfZonecheckInDB(checkTime, Integer.toString(siteZone.getZone_id()));

                if (checkTime.getStatus() < 2) {
                    if (siteZone.isChecked()) {
                        Intent intent = new Intent(mCtx, ZoneCheckActivity.class);
                        intent.putExtra(UtilStrings.ZONE_CHECK_ID, siteZone.getZoneCheckId());
                        intent.putExtra(UtilStrings.DB_SITE_ZONE, Parcels.wrap(tmp));
                        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                        intent.putExtra(UtilStrings.CHECK_TIME, checkTime.getCheck_date());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity(intent);
                        currentCheckPosition = null;
                        Logger.info("You have already checked this zone! click to view checks");
                    }
                } else if (checkTime.getStatus() == 2) {
                    if (siteZone.isChecked()) {
                        Intent intent = new Intent(mCtx, ZoneCheckActivity.class);
                        intent.putExtra(UtilStrings.ZONE_CHECK_ID, siteZone.getZoneCheckId());
                        intent.putExtra(UtilStrings.DB_SITE_ZONE, Parcels.wrap(tmp));
                        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                        intent.putExtra(UtilStrings.CHECK_TIME, checkTime.getCheck_date());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity(intent);
                        currentCheckPosition = null;
                        Logger.info("You have already checked this zone!, click to view checks");

                    } else {

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        String time = df.format(Calendar.getInstance().getTime());
                        Analytics.trackEvent(UtilStrings.LOG_ZONE_CHECK_TIME + checkTime.getCheck_date() + " -> current time: " + time);
                        if (siteZone.getStatus() == 3) {
                            Intent intent = new Intent(mCtx, CaygoZoneCheckActivity.class);
                            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                            intent.putExtra(UtilStrings.SCHEDULED_CHECK_TIME, Parcels.wrap(checkTime));
                            intent.putExtra(UtilStrings.TOUR_NAME, tourName);
                            intent.putExtra(UtilStrings.CHECK_TIME_ID, checkTime.getCheck_id());
                            intent.putExtra(UtilStrings.CHECK_TIME, checkTime.getCheck_date());
                            intent.putExtra(UtilStrings.SITE_TOUR_ID, "");
                            intent.putExtra(UtilStrings.TOUR_CHECK, false);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mCtx.startActivity(intent);
                        } else {
                            if (mCaygoSite.siteTypeNFC(mCtx)) {
                                scanNFCToStart(siteZone, checkTime);
                            } else {
                                showScanPopUp(siteZone, checkTime);
                            }
                        }
                    }
                } else {

                    //view check only available to checked zone
                    if (siteZone.isChecked()) {
                        Intent intent = new Intent(mCtx, ZoneCheckActivity.class);
                        intent.putExtra(UtilStrings.ZONE_CHECK_ID, siteZone.getZoneCheckId());
                        intent.putExtra(UtilStrings.DB_SITE_ZONE, Parcels.wrap(tmp));
                        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                        intent.putExtra(UtilStrings.CHECK_TIME, checkTime.getCheck_date());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity(intent);
                        currentCheckPosition = null;
                    }
                }
            } else {
                //site tour checks...
                if (siteZone.isChecked()) {
                    DBZoneCheckModel tmpDB =  getDBModel(siteZone.getZone_id());
                    Intent intent = new Intent(mCtx, ZoneCheckActivity.class);
                    intent.putExtra(UtilStrings.ZONE_CHECK_ID, siteZone.getZoneCheckId());
                    intent.putExtra(UtilStrings.DB_SITE_ZONE, Parcels.wrap(tmpDB));
                    intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(intent);
                    currentCheckPosition = null;
                }
            }
            return true;
        });

        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (expandableListView.isGroupExpanded(groupPosition)) {
                expandableListView.collapseGroupWithAnimation(groupPosition);
            } else {
                expandableListView.expandGroupWithAnimation(groupPosition);
            }
            return true;
        });

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (expandableListView.getChildAt(0) != null) {
                    if (getActivity() !=null) {
                        ((CaygoHomeActivity) getActivity()).swipeLayout.setEnabled(expandableListView.getFirstVisiblePosition() == 0 && expandableListView.getChildAt(0).getTop() == 0);
                    }
                }
            }
        });

        datePickerButton.setText(String.format("Today, %s", new Util().todayDateWithMonth()));
        dateView.setOnClickListener(v -> showDatePicker());
        datePickerButton.setOnClickListener(v -> showDatePicker());
    }

    private DBZoneCheckModel checkIfZonecheckInDB(CheckTime checkTime, String zoneId) {
        for (int i= 0;i<mDBList.size();i++) {
            if(mDBList.get(i).isCurrentZoneCheck(zoneId, checkTime.getCheck_id())) {
                return mDBList.get(i);
            }
        }
        return null;
    }

    private void showDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String tmp = datePickerButton.getText().toString();
        if (tmp.contains("Today, ")) {
            tmp = tmp.replace("Today, ", "");
        }
        try {
            Date displayDate = sdf.parse(tmp);
            Calendar c = Calendar.getInstance();
            c.setTime(displayDate);
            MyDatePickerFragment dateFragment = MyDatePickerFragment.newInstance(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dateFragment.setmListener(this);
            if (getActivity() !=null) dateFragment.show(getActivity().getSupportFragmentManager(), "date picker");
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_item_list, container, false);
        expandableListView = view.findViewById(R.id.expandableListViewlayout);
        mIndicator = view.findViewById(R.id.schedule_avi);
        mProgressView = view.findViewById(R.id.schedule_progress_rel);
        noScheduleTV = view.findViewById(R.id.schedule_no_content);
        dateView = view.findViewById(R.id.schedule_date_rel);
        datePickerButton = view.findViewById(R.id.schedule_header_date);
        return view;
    }

    private void viewManagerTour() {
        if (!siteCheck.isComplete()) {
            Intent intent = new Intent(getActivity(), ManagerTourActivity.class);
            intent.putExtra(UtilStrings.TOUR_NAME, tourName);
            startActivity(intent);
        } else {
            Logger.info("You have already did CAYGO tour today.");
        }
    }

    private void getCurrentCheckPosition() {
        for (int i= 0; i< siteCheck.getCheckTimes().size();i++) {
            CheckTime checkTime = siteCheck.getCheckTimes().get(i);
            checkTime.processCheckTime();
            if (checkTime.getStatus() == 2) {
                if (i< siteCheck.getCheckTimes().size()) {
                    currentCheckPosition = i + 1;
                    return;
                } else if(i<siteCheck.getCheckTimes().size()) {
                    currentCheckPosition = i ;
                    return;
                }
            }
        }
    }

    public void scrollToCurrent() {
        if (currentCheckPosition != null && mCurrentDay !=0) {
            expandableListView.setSelection(currentCheckPosition);
        }
    }

    private void expandCurrentPositions() {
        for (int i= 0; i< siteCheck.getCheckTimes().size();i++) {
            CheckTime checkTime = siteCheck.getCheckTimes().get(i);
            checkTime.processCheckTime();
            if (checkTime.getStatus() == 2) {
                expandableListView.expandGroup(i+1);
            }
        }
    }

    private void showScanPopUp(final SiteZone siteZone, final CheckTime checkTime) {

        if (getActivity() == null) return;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("Scan to start")
                .content("Scan QR Code to Start Zone Check")
                .positiveText("OK")
                .negativeText("Cancel")
                .onPositive((dialog, which) -> {
                    Intent intent = new Intent(mCtx, QRCodeCaptureActivity.class);
                    intent.putExtra(UtilStrings.SCAN_LEVEL, 1);
                    intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                    intent.putExtra(UtilStrings.SCHEDULED_CHECK_TIME, Parcels.wrap(checkTime));
                    intent.putExtra(UtilStrings.CHECK_TIME_ID, checkTime.getCheck_id());
                    intent.putExtra(UtilStrings.CHECK_TIME, checkTime.getCheck_date());
                    intent.putExtra(UtilStrings.TOUR_NAME, tourName);
                    intent.putExtra(UtilStrings.SITE_TOUR_ID, "");
                    intent.putExtra(UtilStrings.TOUR_CHECK, false);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(intent);
                });
        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    private void scanNFCToStart(SiteZone siteZone, CheckTime checkTime) {
        Intent intent = new Intent(mCtx, NFCMainActivity.class);
        intent.putExtra(UtilStrings.NFC_TYPE, 3);
        intent.putExtra(UtilStrings.SCAN_LEVEL, 1);
        intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
        intent.putExtra(UtilStrings.SCHEDULED_CHECK_TIME, Parcels.wrap(checkTime));
        intent.putExtra(UtilStrings.CHECK_TIME_ID, checkTime.getCheck_id());
        intent.putExtra(UtilStrings.CHECK_TIME, checkTime.getCheck_date());
        intent.putExtra(UtilStrings.TOUR_NAME, tourName);
        intent.putExtra(UtilStrings.SITE_TOUR_ID, "");
        intent.putExtra(UtilStrings.TOUR_CHECK, false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (!isLive) return;
        HazardObserver observer = (HazardObserver) observable;
        if (observer.isScheduledReceived()) {
            getDataFromParent();
            Logger.info("Home screen schedule data have received.");
            if(isManager) {
                //retrieve data from, need to test this part.
                mDBTourCheckManager.getCurrentTourCheck(siteTour.getScheduledSiteTourId());
            }
        }

        if (observer.isQrCodeFailed()) {
            if (getActivity()!=null) {

                String title = "Incorrect QR Code";
                String message = "Please scan a QR Code for the Zone you select.";
                if (mCaygoSite.siteTypeNFC(mCtx)) {
                    title = "Incorrect NFC Tag";
                    message = "Please tap an NFC tag for the Zone you selected.";
                }

                MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                        .title(title)
                        .content(message)
                        .positiveText("Try Again")
                        .onPositive((dialog, which) -> dialog.dismiss());

                MaterialDialog dialog = builder.build();
                dialog.show();
            }
        }
    }

    @Override
    public void onDialogPositiveClicked(String date, boolean today, int dayNumber) {
        showLoadingSpinner();
        noScheduleTV.setVisibility(View.GONE);
        mCurrentDay = dayNumber;
        dateString = date;
        if (today) {
            datePickerButton.setText(String.format("Today, %s", date));
            getDataFromParent();
        }else {
            datePickerButton.setText(date);
            mPresenter.getSchedulesByDate(date);
        }
    }

    @Override
    public void onDialogNegativeClicked() {

    }

    @Override
    public void getSchedules(ArrayList<SiteCheck> arrayList) {
        hideLoadingSpinner();
        if (arrayList.size()>0) {
            siteCheck = arrayList.get(0);
        } else {
            siteCheck = null;
        }
        nextCheckTime = null;
        loadData();
    }

    @Override
    public void requestError(VolleyError error) {
        siteCheck = null;
        nextCheckTime = null;
        if (((BaseActivity) getActivity()).connected) {

            if (error !=null) {
                if(error.networkResponse !=null) {
                    if(error.networkResponse.statusCode == 401 && PreferenceHelper.getInstance(mCtx).getRefreshToken() == null) {
                        goToLogInScreen();
                    } else mErrorHandler.onErrorResponse(error);
                } else mErrorHandler.onErrorResponse(error);
            } else mErrorHandler.onErrorResponse(error);
        }
        loadData();
    }

    private void goToLogInScreen() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void errorDialogPositiveClicked() {

    }

    @Override
    public void errorDialogNegativeClicked() {

    }

    @Override
    public void allTourChecksInDB(@Nullable ArrayList<DBTourCheckModel> mList) {

    }

    @Override
    public void getTourCheckByIdInDB(@Nullable DBTourCheckModel mTour) {
        mCurrentTourCheckModel = mTour;
        updateTourOffineModel();
    }

    private void updateTourOffineModel() {
        if (mCurrentTourCheckModel == null){
            return;
        }

        ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleDataWithOutRefresh();
        if (siteChecks !=null && siteChecks.size() > 0) {
            siteTour = siteChecks.get(0);
            mZones = siteTour.getTourChecks();
        }
        int hazards = getCurrentHazards(mZones);

        if (allChecked()) {
            offlineTourComplete = true;
        }

        ArrayList<DBZoneCheckModel> list = mCurrentTourCheckModel.getZoneChecks();
        expandableListAdapter.reloadOfflineCheck(offlineTourComplete, hazards, mCurrentTourCheckModel);
        expandableListAdapter.notifyDataSetChanged();
    }

    public int getCurrentHazards(ArrayList<SiteZone> zones) {
        int hazards = 0;
        if (mZones == null) return hazards;
        for (SiteZone zone : mZones) {
            if (!zone.isChecked()) {
                if (getDBModel(zone.getZone_id()) != null) {

                    DBZoneCheckModel tmpModel = getDBModel(zone.getZone_id());
                    if (tmpModel.isComplete())
                        hazards = hazards + tmpModel.getAddressHazardCommandModelsV2().size();
                }
            } else {
                hazards = hazards + zone.getHazards();
            }
        }
        return hazards;
    }

    private boolean allChecked() {
        if (mZones == null) return false;
        for (SiteZone zone : mZones) {
            if (!zone.isChecked()) {
                if (getDBModel(zone.getZone_id()) != null) {
                    if (!getDBModel(zone.getZone_id()).isComplete())
                        return false;
                }else {
                    return false;
                }
            }
        }
        return true;
    }

    private DBZoneCheckModel getDBModel(int mZoneId) {
        if (mCurrentTourCheckModel == null) return null;
        for (DBZoneCheckModel tmp : mCurrentTourCheckModel.getZoneChecks()) {
            if (tmp.getZoneId().intValue() == mZoneId) {
                return tmp;
            }
        }
        return null;
    }
}
