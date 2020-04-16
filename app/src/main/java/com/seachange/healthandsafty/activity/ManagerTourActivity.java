package com.seachange.healthandsafty.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import com.google.gson.Gson;
import com.seachange.healthandsafty.db.DBTourCheck;
import com.seachange.healthandsafty.helper.DBTourCheckHelper;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManager;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManagerView;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.adapter.ZoneRecyclerViewAdapter;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.helper.CheckPreference;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.ManagerTour;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.utils.Util;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.jetbrains.annotations.Nullable;

public class ManagerTourActivity extends BaseActivity implements View.OnClickListener, DBTourCheckManagerView, Observer {

    private ZoneRecyclerViewAdapter mAdapter;
    private TextView mTimeView, progressView, mTotalHazards;
    private Chronometer mChronometer;
    private RecyclerView mRecyclerView;
    private ArrayList<SiteZone> mZones;
    private ProgressBar mProgressBar;
    private int mCheckedZones = 0;
    private ManagerTour managerTour;
    private static String tourName = "manager_tour";
    private CheckPreference mTourCheckPref;
    private String mTourCheckId;
    private SiteCheck siteTour;
    private Button mCloseButton;
    private CaygoSite caygoSite;
    private MaterialDialog dialog;
    private boolean firstLoad = true, qrCodeDialogShowing;
    private DBTourCheckHelper mDBTourCheckHelper;
    private DBTourCheckManager mDBTourCheckManager;
    private DBTourCheckModel mCurrentTourCheckModel = new DBTourCheckModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_tour);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mTimeView = findViewById(R.id.manager_tour_time);
        mChronometer = findViewById(R.id.manager_chronometer);
        mRecyclerView = findViewById(R.id.manager_hazard_source_type);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mCtx));
        mProgressBar = findViewById(R.id.progressBar);
        progressView = findViewById(R.id.manager_tour_hazard_count);
        mTotalHazards = findViewById(R.id.manager_tour_hazards);
        mCloseButton = findViewById(R.id.manager_tour_close);
        mCloseButton.setOnClickListener(this);
        mTourCheckPref = new CheckPreference(mCtx);

        caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        reloadChecks();

        boolean isEmpty = siteTour.getSiteTourId().equals("null");

        if (!isEmpty) {
            mTourCheckId = siteTour.getSiteTourId();
            mTourCheckPref.saveTourUUID(mTourCheckId);
            Logger.infoTourCheck("TOUR CHECK ID : existing tour check id: " + mTourCheckId);
        } else {
            mTourCheckId = UUID.randomUUID().toString();
            mTourCheckPref.saveTourUUID(mTourCheckId);
            Logger.infoTourCheck("TOUR CHECK ID :  new tour check id: " + mTourCheckId);
        }

        mChronometer.start();
        setDateAndTime();

        mDBTourCheckHelper = new DBTourCheckHelper(mCtx);
        mCloseButton.setText(mCtx.getString(R.string.fa_close_circle));
        mCloseButton.setTypeface(SCApplication.FontMaterial());
        PreferenceHelper.getInstance(mCtx).saveShowTourQRFailed(false);

        //init site tour offline work
        mDBTourCheckManager = new DBTourCheckManager(this, this);
        mDBTourCheckManager.retrieveAllTourChecks();
        mDBTourCheckManager.getCurrentTourCheck(siteTour.getScheduledSiteTourId());

        showNoConnectionBaseView = true;
        initLoad();
    }

    private void updateEndButton() {
        if (allChecked()) {

//            if (mCurrentTourCheckModel !=null) {
//                mCurrentTourCheckModel.setTimeCompleted(getDateString());
//                mDBTourCheckHelper.saveCurrentTourCheck(mCurrentTourCheckModel);
//                mDBTourCheckManager.updateOrEnterTourChecks(mCurrentTourCheckModel, mApplication);
//            }
            showEndTour();
        }
    }

    private boolean allChecked() {
        if (mZones == null) return false;
        for (SiteZone zone : mZones) {
            if (!zone.isChecked()) {
                if (getDBModel(zone.getZone_id()) != null) {
                    if (!getDBModel(zone.getZone_id()).isComplete())
                        return false;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    private boolean zoneCheckStarted() {
        if (mZones != null) {
            for (SiteZone zone : mZones) {
                if (zone.isChecked()) return true;
            }
            return false;
        }
        return false;
    }

    private void updateProgressIfNeed() {
        int tmp = getZonesChecked();
        if (mCheckedZones != tmp) {
            mCheckedZones = tmp;
            new Handler().postDelayed(() -> {
                updateProgress();
                updateHazards();
                if (mCloseButton.getVisibility() == View.VISIBLE)
                    mCloseButton.setVisibility(View.GONE);
            }, 500);
        }
    }

    private int getZonesChecked() {
        if (mZones == null) return 0;
        int checked = 0;
        for (int i = 0; i < mZones.size(); i++) {
            if (mZones.get(i).isChecked()) {
                checked++;
            } else if (getDBModel(mZones.get(i).getZone_id()) != null) {
                if (getDBModel(mZones.get(i).getZone_id()).isComplete())
                    checked++;
            }
        }
        return checked;
    }

    private boolean isLastCheck() {
        if (mZones == null) return false;
        int tmp = getZonesChecked();
        if (mZones.size() == tmp + 1) return true;
        return false;
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

    private void updateHazards() {
        int hazards = managerTour.getCurrentHazards(mZones);
        if (hazards > 0) {
            mTotalHazards.setText(Integer.toString(hazards));
        } else if (mCheckedZones > 0 && hazards == 0) {
            mTotalHazards.setText("0");
        }
    }

    private void updateProgress() {
        String progress = mCheckedZones + "/" + mZones.size() + " Zones checked";
        progressView.setText(progress);
        setProgressWithAnimation(mCheckedZones * 100);
    }

    public void setProgressWithAnimation(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(mProgressBar, "progress", progress);
        animation.setDuration(500); // 0.5 second
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void setDateAndTime() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.getDefault());
        String date = df.format(Calendar.getInstance().getTime());
        mTimeView.setText(date);
    }

    private void showEndTour() {
        Intent intent = new Intent(this, EndTourActivity.class);
        intent.putExtra(UtilStrings.MANAGER_HOME, false);
        startActivity(intent);
        this.finish();
    }

    private void showAlert() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("Incorrect QR Code")
                .content("Please scan a QR Code for the Zone you select.")
                .positiveText("Try Again")
                .onPositive((dialog, which) -> dialog.dismiss());

        dialog = builder.build();
        dialog.show();
    }

    private void initLoad() {
        if (mZones != null) {
            mZones = managerTour.getTourSiteZone();
            if (mAdapter == null) {
                mAdapter = new ZoneRecyclerViewAdapter(mZones, mCtx, tourName, "", this, true, mTourCheckId, true, mCurrentTourCheckModel, isLastCheck(), caygoSite);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.reLoadZoneData(mZones, "", mCurrentTourCheckModel, isLastCheck());
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDBTourCheckHelper = new DBTourCheckHelper(mCtx);

        mCurrentTourCheckModel = mDBTourCheckHelper.getCurrentTourCheck();
        if (mCurrentTourCheckModel != null && mTourCheckId != null) {
            if (mCurrentTourCheckModel.getSiteTourId() != null) {
                if (!mCurrentTourCheckModel.getSiteTourId().equals(mTourCheckId)) {
                    mCurrentTourCheckModel = new DBTourCheckModel();
                }
            }
        }
        if (mZones != null) {
            mZones = managerTour.getTourSiteZone();
            updateProgressIfNeed();

        }

        if (firstLoad) {
            firstLoad = false;
        } else {
            updateEndButton();
            reloadChecks();
        }
        if (zoneCheckStarted()) mCloseButton.setVisibility(View.GONE);
        HazardObserver.getInstance().setQrCodeFailed(false);
        new Handler().postDelayed(() -> {
            if (PreferenceHelper.getInstance(mCtx).getShowTourQRFailed()) showAlert();
        }, 500);
        HazardObserver.getInstance().addObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceHelper.getInstance(mCtx).saveShowTourQRFailed(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (managerTour != null) {
            managerTour.removeAll();
        }
        HazardObserver.getInstance().setQrCodeFailed(false);
        if (dialog != null) {
            dialog.dismiss();
        }
        PreferenceHelper.getInstance(mCtx).fetchScheduledDataNotify();
        mDBTourCheckManager.updateOrEnterTourChecks(mCurrentTourCheckModel, mApplication);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manager_tour_close:
                this.finish();
                break;
        }
    }

    @Override
    public void allTourChecksInDB(@Nullable ArrayList<DBTourCheckModel> mList) {
        Gson gson = new Gson();
        Logger.info("current checks in db received...." + gson.toJson(mList.toString()));
    }

    @Override
    public void getTourCheckByIdInDB(@Nullable DBTourCheckModel mTour) {
        Logger.info("current check in db found, maybe not in db....");

        if (allChecked()) {
            //do nothing
        }
        //build db check model
        else if (mTour == null) {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            String time = df.format(Calendar.getInstance().getTime());
            //build check model,
            mCurrentTourCheckModel = new DBTourCheckModel();
            mCurrentTourCheckModel.setGroupId(caygoSite.getGroupId());
            mCurrentTourCheckModel.setSiteTourId(mTourCheckId);
            mCurrentTourCheckModel.setScheduledSiteTourId(siteTour.getScheduledSiteTourId());
            mCurrentTourCheckModel.setTimeStarted(time);

            DBTourCheck check = new DBTourCheck();
            check.setTourZoneCheck(new Util().gsonHelper().toJson(mCurrentTourCheckModel));
        } else {
            if (mCurrentTourCheckModel != null) {
                mTourCheckId = mCurrentTourCheckModel.getSiteTourId();
                mTourCheckPref.saveTourUUID(mTourCheckId);
            }

            mCurrentTourCheckModel = mTour;

            // check if tour object is set up properly
            if (mCurrentTourCheckModel.getGroupId() == null) {
                mCurrentTourCheckModel.setGroupId(caygoSite.getGroupId());
            }

            if (mCurrentTourCheckModel.getSiteTourId() == null) {
                mCurrentTourCheckModel.setSiteTourId(mTourCheckId);
            }

            if (mCurrentTourCheckModel.getTimeStarted() == null) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                String time = df.format(Calendar.getInstance().getTime());
                mCurrentTourCheckModel.setTimeStarted(time);
            }

            if (mCurrentTourCheckModel.getScheduledSiteTourId() == null) {
                mCurrentTourCheckModel.setScheduledSiteTourId(siteTour.getScheduledSiteTourId());
            }
        }

        mDBTourCheckHelper.saveCurrentTourCheck(mCurrentTourCheckModel);
        mAdapter.reLoadZoneData(mZones, "", mCurrentTourCheckModel, isLastCheck());
        mAdapter.notifyDataSetChanged();
        updateProgressIfNeed();
    }

    private void reloadChecks() {
        ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleData();
        if (siteChecks.size() > 0) {
            siteTour = siteChecks.get(0);
            mZones = siteTour.getTourChecks();
        }

        if (mZones != null) {
            mProgressBar.setMax(mZones.size() * 100);
            mProgressBar.setProgress(0);
            updateProgress();
            managerTour = new ManagerTour(mCtx, mZones, tourName);
            updateProgressIfNeed();
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Boolean isSync = updateSyncFromObserver(observable);
        if (isSync) {
            HazardObserver.getInstance().resetSyncObserver();
        } else {
            HazardObserver observer = (HazardObserver) observable;
            if (observer.isScheduledReceived()) {
                reloadChecks();
                if (mAdapter == null) {
                    mAdapter = new ZoneRecyclerViewAdapter(mZones, mCtx, tourName, "", this, true, mTourCheckId, true, mCurrentTourCheckModel, isLastCheck(), caygoSite);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.reLoadZoneData(mZones, "", mCurrentTourCheckModel, isLastCheck());
                    mAdapter.notifyDataSetChanged();
                }
                Logger.info("DB -> manager get schedules...");
            }

            if (observer.isQrCodeFailed()) {
                if (qrCodeDialogShowing) return;
                String title = "Incorrect QR Code";
                String message = "Please scan a QR Code for the Zone you select.";
                if (caygoSite.siteTypeNFC(mCtx)) {
                    title = "Incorrect NFC Tag";
                    message = "Please tap an NFC tag for the Zone you selected.";
                }
                MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                        .title(title)
                        .content(message)
                        .positiveText("Try Again")
                        .onPositive((dialog, which) -> {
                            dialog.dismiss();
                            qrCodeDialogShowing = false;
                        });

                MaterialDialog dialog = builder.build();
                if (isLive)dialog.show();
                dialog.setOnDismissListener(dialogInterface -> qrCodeDialogShowing = false);
                qrCodeDialogShowing = true;
                HazardObserver.getInstance().setQrCodeFailed(false);

            }
        }
    }
}


