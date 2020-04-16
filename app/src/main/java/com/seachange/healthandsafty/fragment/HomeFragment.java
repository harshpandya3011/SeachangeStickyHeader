package com.seachange.healthandsafty.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.microsoft.appcenter.analytics.Analytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import com.seachange.healthandsafty.View.RequestErrorView;
import com.seachange.healthandsafty.activity.BaseActivity;
import com.seachange.healthandsafty.activity.CaygoHomeActivity;
import com.seachange.healthandsafty.activity.CaygoZoneCheckActivity;
import com.seachange.healthandsafty.activity.LoginActivity;
import com.seachange.healthandsafty.activity.ManagerTourActivity;
import com.seachange.healthandsafty.activity.QRCodeCaptureActivity;
import com.seachange.healthandsafty.activity.ZoneCheckActivity;
import com.seachange.healthandsafty.adapter.HomeExpandListViewAdapter;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.db.DBCheck;
import com.seachange.healthandsafty.db.DBCheckViewModel;
import com.seachange.healthandsafty.helper.CustomExpListView;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.HomeData;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.ManagerTour;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.helper.SiteCheckUtil;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManager;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManagerView;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.model.Stats;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.nfc.ui.NFCMainActivity;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.jetbrains.annotations.Nullable;
import org.parceler.Parcels;


public class HomeFragment extends BaseFragment implements Observer, RequestErrorView, DBTourCheckManagerView {

    private ArrayList<SiteZone> mZones;
    private ManagerTour managerTour;
    private ProgressBar mComplianceProgress;
    private TextView mComplianceView, mTotalHazards, mStreak, mSiteTitle, mGroupTitle, mWeekTitle, mScheduled;
    private TextView mManagerTourHazards, mManagerTourDate, mMangerSelectIcon;
    private ImageView mIcon;
    private CaygoSite mCaygoSite;
    private String nextCheckTime;
    private RelativeLayout managerView, managerViewButton;
    private SiteCheck siteCheck;
    private static String tourName = "Home Tour";
    private Stats mStats;
    private boolean firstLoad, qrCodeDialogShowing, managerSelect, isManager;
    private TextView noScheduleTV, mCountDownTimerView, mCheckStartTimeTV;
    private HomeData mHomeData;
    private HomeExpandListViewAdapter expandableListAdapter;
    private HashMap<String, List<SiteZone>> expandableListDetail;
    private List<String> expandableListTitle;
    private CustomExpListView expandableListView;
    private ArrayList<CheckTime> liveCheckTimes;
    private ArrayList<DBZoneCheckModel> mDBList;
    private DBCheckViewModel mDBViewModel;
    private DBTourCheckManager mDBTourCheckManager;
    private DBTourCheckModel mCurrentTourCheckModel = new DBTourCheckModel();
    private SiteCheck siteTour;
    private CountDownTimer mCountDownTimer;
    private LinearLayout mBeforeStartLin;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() !=null && getActivity().getIntent().getExtras() !=null)
        isManager = getActivity().getIntent().getExtras().getBoolean(UtilStrings.MANAGER_HOME);
        HazardObserver.getInstance().setQrCodeFailed(false);
        liveCheckTimes = new ArrayList<>();
        mDBList = new ArrayList<>();

        ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleData();
        if (siteChecks !=null &&siteChecks.size() > 0) {
            siteTour = siteChecks.get(0);
            mZones = siteTour.getTourChecks();
        }

        //init site tour offline work
        mDBTourCheckManager = new DBTourCheckManager(this, (BaseActivity)getActivity());
        mDBTourCheckManager.retrieveAllTourChecks();
//        mDBTourCheckManager.getCurrentTourCheck(siteTour.getScheduledSiteTourId());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_home_list, container, false);

        expandableListView = mView.findViewById(R.id.expandableListViewlayoutHome);
        mComplianceProgress = mView.findViewById(R.id.home_compliance_progressbar);
        mComplianceView = mView.findViewById(R.id.home_check_compliance);
        mIcon = mView.findViewById(R.id.home_site_icon);
        mTotalHazards = mView.findViewById(R.id.home_total_hazards);
        mStreak = mView.findViewById(R.id.home_streak);
        mScheduled = mView.findViewById(R.id.home_scheduled);
        mSiteTitle = mView.findViewById(R.id.home_site_name);
        mGroupTitle = mView.findViewById(R.id.home_group_name);
        mWeekTitle = mView.findViewById(R.id.home_sub_title);
        managerView = mView.findViewById(R.id.manager_home_tour_view);
        managerViewButton = mView.findViewById(R.id.manager_home_tour_view_click);
        mManagerTourHazards = mView.findViewById(R.id.home_manager_hazards);
        mManagerTourDate = mView.findViewById(R.id.manager_tour_home_tour_date);
        mMangerSelectIcon = mView.findViewById(R.id.home_manager_check_icon);
        noScheduleTV = mView.findViewById(R.id.home_screen_no_content);
        mCountDownTimerView = mView.findViewById(R.id.home_count_down_timer);
        mCheckStartTimeTV = mView.findViewById(R.id.home_check_start_time);
        mBeforeStartLin = mView.findViewById(R.id.before_start_lin);

        return mView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDataFromParent();
        initCheckClick();
    }

    private void loadPage(){
        mCaygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        initPageWithData();
        initStatsData();
        loadCheckListData(true);
        mBeforeStartLin.setVisibility(View.GONE);
        noScheduleTV.setVisibility(View.GONE);

        if (liveCheckTimes.size() == 0) {
            noScheduleTV.setVisibility(View.VISIBLE);

            //check hasn't started yet for the day.
            if (mHomeData.beforeFirstCheck){
                noScheduleTV.setVisibility(View.GONE);
                mBeforeStartLin.setVisibility(View.VISIBLE);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date mDate = sdf.parse(mHomeData.siteCheck.getCheckTimes().get(0).getCheck_date());
                    DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    mCheckStartTimeTV.setText(String.format(df.format(mDate)));

                    //set timer
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();

                    long diffInMs = mDate.getTime() - currentDate.getTime();
                    long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

                    int seconds = (int) diffInSec;
                    int hours = seconds / (60 * 60);
                    int tempMint = (seconds - (hours * 60 * 60));
                    int minutes = tempMint / 60;
                    seconds = tempMint - (minutes * 60);

                    if (hours > 0) {
                        mCountDownTimerView.setText(String.format("%sh %sm %ss", String.format("%02d", hours), String.format("%02d", minutes), String.format("%02d", seconds)));
                    } else {
                        mCountDownTimerView.setText(String.format("%sm%ss", String.format("%02d", minutes), String.format("%02d", seconds)));
                    }

                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }

                    mCountDownTimer = new CountDownTimer(diffInMs, 1000) { //Sets 10 second remaining

                        public void onTick(long millisUntilFinished) {
                            int seconds = (int) (millisUntilFinished / 1000);
                            int hours = seconds / (60 * 60);
                            int tempMint = (seconds - (hours * 60 * 60));
                            int minutes = tempMint / 60;
                            seconds = tempMint - (minutes * 60);
                            if (hours > 0) {
                                mCountDownTimerView.setText(String.format("%sh %sm %ss", String.format("%02d", hours), String.format("%02d", minutes), String.format("%02d", seconds)));
                            } else {
                                mCountDownTimerView.setText(String.format("%sm%ss", String.format("%02d", minutes), String.format("%02d", seconds)));
                            }
                        }
                        public void onFinish() {
                            mCountDownTimerView.setText("00:00");
                            Logger.info("timer reached end");
                        }
                    }.start();


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //else if no data

            else if(PreferenceHelper.getInstance(mCtx).getScheduleStatus() == 2) {

                if (!noScheduleTV.getText().equals(mCtx.getResources().getString(R.string.caygo_home_no_data_returned))) {
                    noScheduleTV.setText(R.string.caygo_home_no_data_found);
                }

            } else if (PreferenceHelper.getInstance(mCtx).getScheduleStatus() == 1) {
                  noScheduleTV.setText(R.string.caygo_home_no_data_returned);
            } else if(mHomeData.afterLastCheck && mZones == null) {
                noScheduleTV.setText(R.string.no_more_schedule_checks);
            }else if(mHomeData.afterLastCheck && mZones.size() == 0){
                noScheduleTV.setText(R.string.no_more_schedule_checks);
            }
        }else {
            noScheduleTV.setVisibility(View.GONE);
        }

        if (isManager) {
            //init manager tour view
            managerView.setVisibility(View.VISIBLE);
            managerViewButton.setOnClickListener(v -> {
                if (siteCheck == null) return;
                if (!siteCheck.isComplete() && ! allChecked()) {
                    Intent intent = new Intent(getActivity(), ManagerTourActivity.class);
                    intent.putExtra(UtilStrings.TOUR_NAME, tourName);
                    startActivity(intent);
                    managerSelect = true;

                } else {
                    Logger.showToast(mCtx, "You have already did CAYGO tour today.");
                }
            });

            DateFormat df = new SimpleDateFormat("dd MMM", Locale.getDefault());
            String date = df.format(Calendar.getInstance().getTime());
            mManagerTourDate.setText(String.format("Today, %s", date));
            mMangerSelectIcon.setTypeface(SCApplication.FontMaterial());
            updateManagerView();
        } else {
            managerView.setVisibility(View.GONE);
        }
    }

    private void loadCheckListData (boolean reload) {
        if (siteCheck != null) {
            expandableListDetail = SiteCheckUtil.getHomeData(siteCheck);
            expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
            liveCheckTimes = SiteCheckUtil.getHomeDataList(siteCheck);
            if (reload) {
                //first time load the list adapter, when load for the second time, only need to refresh the listView
                expandableListAdapter = new HomeExpandListViewAdapter(getActivity(), expandableListTitle, expandableListDetail, liveCheckTimes,
                        nextCheckTime, mHomeData, 0);
                expandableListView.setAdapter(expandableListAdapter);
            } else {
                expandableListAdapter.refresh(getActivity(), expandableListTitle, expandableListDetail, liveCheckTimes,
                        nextCheckTime, mHomeData, 0);
                expandableListView.setAdapter(expandableListAdapter);
            }
            ViewCompat.setNestedScrollingEnabled(expandableListView, false);
            //expand all child items
            for (int i=0;i<expandableListTitle.size();i++) {
                expandableListView.expandGroup(i);
            }
            expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> true);
        }
    }

    private void initCheckClick() {
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            SiteZone siteZone = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
            final CheckTime checkTime = liveCheckTimes.get(groupPosition );
            final DBZoneCheckModel tmp = checkIfZonecheckInDB(checkTime, Integer.toString(siteZone.getZone_id()));

            if (checkTime.getStatus()<2){
                if (siteZone.isChecked()) {
                    Intent intent = new Intent(mCtx, ZoneCheckActivity.class);
                    intent.putExtra(UtilStrings.ZONE_CHECK_ID, siteZone.getZoneCheckId());
                    intent.putExtra(UtilStrings.DB_SITE_ZONE, Parcels.wrap(tmp));
                    intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(siteZone));
                    intent.putExtra(UtilStrings.CHECK_TIME, checkTime.getCheck_date());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(intent);
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
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(intent);
                }
            }
            return true;
        });
    }

    private DBZoneCheckModel checkIfZonecheckInDB(CheckTime checkTime, String zoneId) {
        for (int i= 0;i<mDBList.size();i++) {
            if(mDBList.get(i).isCurrentZoneCheck(zoneId, checkTime.getCheck_id())) {
                return mDBList.get(i);
            }
        }
        return null;
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

    private void updateManagerView() {
        if (siteCheck == null) return;
        //handle offline complete
        if (siteCheck.isComplete() || allChecked()) {
            int hazards = getCurrentHazards();
            mManagerTourHazards.setText(Integer.toString(hazards));
            mManagerTourHazards.setBackgroundResource(R.drawable.circle_grey);
            mMangerSelectIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.colorDefaultGreen));
            mMangerSelectIcon.setText(mCtx.getResources().getString(R.string.fa_check));
        } else {
            mManagerTourHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
            mManagerTourHazards.setText("");
            mMangerSelectIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.arrow_grey));
            mMangerSelectIcon.setText(mCtx.getResources().getString(R.string.fa_right_arrow));
        }
    }

    @SuppressLint("NewApi")
    private void initPageWithData() {
        if (mCaygoSite == null) return;
        //after got site data
        mSiteTitle.setText(mCaygoSite.getSite_name());
        mGroupTitle.setText(mCaygoSite.getGroup_name());
        if (mCaygoSite.getIcon_url() != null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.ic_image_black_24dp)
                    .showImageForEmptyUri(R.mipmap.ic_image_black_24dp)
                    .showImageOnFail(R.mipmap.ic_image_black_24dp)
                    .cacheInMemory(true)
                    .build();
            ImageLoader.getInstance().displayImage(mCaygoSite.getIcon_url(), mIcon, options);
        }
    }

    private void initStatsData() {
        mStats = PreferenceHelper.getInstance(mCtx).getStatsData();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) dayOfWeek = 7;
        mWeekTitle.setText(String.format(Locale.getDefault(),"This Week (day %d of 7)", dayOfWeek));

        if (mStats != null) {

            mComplianceProgress.setMax(100);
            mComplianceProgress.setSecondaryProgress(100);
            mComplianceProgress.setProgress(0);

            if(mStats.getNumCompleted() !=null) {
                mStreak.setText(Integer.toString(mStats.getNumCompleted()));
            } else {
                mStreak.setText("-");
            }

            if(mStats.getNumScheduled() !=null) {
                mScheduled.setText(String.format("/%s", Integer.toString(mStats.getNumScheduled())));
            } else {
                mScheduled.setText("/-");
            }

            if (mStats.getCompliancePercentage() == null){
                mComplianceView.setText("-");
            }else {
                mComplianceView.setText(String.format("%s%%", Integer.toString(mStats.getCompliancePercentage())));
                mComplianceProgress.setProgress(mStats.getCompliancePercentage());
            }
            if (mStats.getNumberOfHazards() == null) {
                mTotalHazards.setText("-");
            } else {
                mTotalHazards.setText(Integer.toString(mStats.getNumberOfHazards()));
            }


            //need to check if the value is null
            Integer compliancePercentage = mStats.getCompliancePercentage();
            if (compliancePercentage != null) {
                new Handler().postDelayed(() -> {
                    ObjectAnimator animation = ObjectAnimator.ofInt(mComplianceProgress, "progress", compliancePercentage);
                    animation.setDuration(500);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                }, 500);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HazardObserver.getInstance().addObserver(this);
        managerSelect = false;
        if (managerTour !=null) {
            if (expandableListAdapter == null) {
                loadCheckListData(true);
            } else {
                loadCheckListData(false);
                if (mZones !=null) {
                    if (managerTour.getCurrentCheckedZones(mZones) == mZones.size()) {
                        if (mHomeData.afterLastCheck) {
                            mZones = new ArrayList<>();
                            PreferenceHelper.getInstance(mCtx).fetchScheduledDataNotifyWithCallBack(this);
                        } else {
                            if (getActivity() != null) {
                                ((CaygoHomeActivity) getActivity()).loadNextCheckTime();
                            }
                        }
                    }
                }
            }
        }
        if (!firstLoad) {
            PreferenceHelper.getInstance(mCtx).fetchStatsDataWithCallBack(this);
        } else {
            firstLoad = false;
        }

        if (getActivity() !=null) {
            ((CaygoHomeActivity) getActivity()).getmHomeData().reloadDataFromCache();
            getDataFromParent();
        }

        loadUnsyncedCheck();
        updateTourOffineModel();

        if(isManager && siteTour !=null) {
            mDBTourCheckManager.getCurrentTourCheck(siteTour.getScheduledSiteTourId());
        }
    }


    @Override
    public void update(Observable observable, Object data) {

        if (!isLive) return;
        HazardObserver observer = (HazardObserver) observable;
        if (observer.isSiteDataReceived()) {
            initPageWithData();
            Logger.info("home screen site received.");
        }
        if (observer.isStatsReceived()) {
            initStatsData();
        }

        if (observer.isScheduledReceived()) {
            Logger.info("Home screen schedule data have received.");
        }

        if (observer.isQrCodeFailed()) {
            if (qrCodeDialogShowing) return;
            if (!managerSelect) {
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
                            .onPositive((dialog, which) -> {
                                dialog.dismiss();
                                qrCodeDialogShowing = false;
                            });

                    MaterialDialog dialog = builder.build();
                    dialog.show();
                    dialog.setOnDismissListener(dialogInterface -> qrCodeDialogShowing = false);
                    qrCodeDialogShowing = true;
                }
                HazardObserver.getInstance().setQrCodeFailed(false);
            }
        }
    }

    public void getDataFromParent() {
        if (getActivity() !=null) {
            HomeData homeData = ((CaygoHomeActivity) getActivity()).getmHomeData();
            tourName = homeData.tourName;
            mHomeData = homeData;
            managerTour = homeData.managerTour;
            siteCheck = homeData.siteCheck;

            ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleData();
            if (siteChecks != null && siteChecks.size() > 0) {
                siteTour = getTodayCheck(siteChecks);
                if (siteTour != null)
                    mZones = siteTour.getTourChecks();
                else mZones = null;
            } else mZones = null;

            nextCheckTime = homeData.nextCheckTime;
            loadPage();
        }
    }

    private SiteCheck getTodayCheck(ArrayList<SiteCheck> siteChecks) {
        SiteCheck siteCheck = null;
        for (int i = 0; i < siteChecks.size(); i++) {
            if (siteChecks.get(i).isToday()) {
                return siteChecks.get(i);
            }
        }
        return siteCheck;
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

            if (arrayList.size()>0) {
                mDBList = arrayList;
                if (expandableListAdapter !=null) {
                    expandableListAdapter.reloadDBChecks(mDBList);
                    expandableListAdapter.notifyDataSetChanged();
                }

                if(isManager) {
                    updateManagerView();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (expandableListAdapter !=null) {
            expandableListAdapter.stopTimer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void requestSucceed() {

    }

    @Override
    public void requestError(VolleyError result) {
        if (result !=null) {
            if(result.networkResponse !=null) {
                if(result.networkResponse.statusCode == 401 && PreferenceHelper.getInstance(mCtx).getRefreshToken() ==  null) {
                    goToLogInScreen();
                }
            }
        }
    }

    private void goToLogInScreen() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        if (mCurrentTourCheckModel == null) return;


        ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleDataWithOutRefresh();
        if (siteChecks!=null && siteChecks.size() > 0) {
            siteTour = getTodayCheck(siteChecks);
            if (siteTour!=null) {
                mZones = siteTour.getTourChecks();
            }
        }

        if (siteCheck == null) {
            mManagerTourHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
            mManagerTourHazards.setText("");
            mMangerSelectIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.arrow_grey));
            mMangerSelectIcon.setText(mCtx.getResources().getString(R.string.fa_right_arrow));
        }else if (siteCheck.isComplete() || allChecked()) {
            int hazards = getCurrentHazards();
            mManagerTourHazards.setText(Integer.toString(hazards));
            mManagerTourHazards.setBackgroundResource(R.drawable.circle_grey);
            mMangerSelectIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.colorDefaultGreen));
            mMangerSelectIcon.setText(mCtx.getResources().getString(R.string.fa_check));
        }else {
            mManagerTourHazards.setBackgroundResource(R.drawable.circle_textview_not_select);
            mManagerTourHazards.setText("");
            mMangerSelectIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.arrow_grey));
            mMangerSelectIcon.setText(mCtx.getResources().getString(R.string.fa_right_arrow));
        }
        updateManagerView();
    }

    public int getCurrentHazards() {
        int hazards = 0;
        if(mZones == null) return hazards;
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
            if (tmp.getZoneId() == mZoneId) {
                return tmp;
            }
        }
        return null;
    }
}
