package com.seachange.healthandsafty.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.seachange.healthandsafty.BuildConfig;
import com.seachange.healthandsafty.db.DBCheck;
import com.seachange.healthandsafty.db.DBCheckViewModel;
import com.seachange.healthandsafty.db.DBTourCheck;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.helper.interfacelistener.RequestCallBack;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.ZoneCheckHazard;
import com.seachange.healthandsafty.presenter.AppPresenter;
import com.seachange.healthandsafty.utils.Util;
import com.seachange.healthandsafty.view.AppView;
import com.seachange.healthandsafty.view.RefreshView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by kevinsong on 25/09/2017.
 */

public class SCApplication extends Application implements AppView {

    private static Typeface typeface;
    private ScheduledExecutorService scheduleTaskExecutor;
    private AppPresenter mPresenter;
    private boolean refreshRunning = false, refreshing = false, receiverEnabled = false, isConnected, syncTour, clickToSync;
    private AppCompatActivity mCurrentActivity = null;
    private BroadcastReceiver mNetworkReceiver;
    private DBCheckViewModel mDBViewModel;
    private RefreshView refreshView;

    @Override
    public void onCreate() {
        super.onCreate();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(500, true, false, false))
                .cacheInMemory(true)
                .showImageOnLoading(android.R.color.transparent)
                .showImageForEmptyUri(android.R.color.transparent)
                .showImageOnFail(android.R.color.transparent)
                .cacheOnDisk(true).
                        build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheSize(10000000)
                .threadPriority(Thread.NORM_PRIORITY - 2).build();
        ImageLoader.getInstance().init(config);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/material-design-iconic-font.ttf");
        mPresenter = new AppPresenter(this, this);
        scheduleTaskExecutor = Executors
                .newSingleThreadScheduledExecutor();

        //temp work
        receiverEnabled = true;
        isConnected = new Util().isNetworkAvailable(this);
        initNetWork();
        timerAutoSync();
        VolleyLog.DEBUG = BuildConfig.DEBUG;
    }

    private void initNetWork() {
        mNetworkReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (!Util.isNetworkAvailable(getApplicationContext())) {
                    //lost connection
                    isConnected = false;
                    Logger.info("APP CONNECTION LOST");
                } else {
                    isConnected = true;
                    // connection established
                    getCachedChecks(true, true);
                    Logger.info("APP CONNECTION GOOD");
                }
            }
        };
        registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void checkRefreshToken(RefreshView view) {
        refreshView = view;
        String tmp = PreferenceHelper.getInstance(this).getRefreshToken();
        if (tmp != null) {
            if (!refreshing) {
                refreshTokenEveryHour(false);
                Logger.info("refresh token now");
            } else {
                refreshView.onRefreshingInProgress();
            }
        }
    }

    public void checkRefreshToken() {
        String tmp = PreferenceHelper.getInstance(this).getRefreshToken();
        if (tmp != null) {
            refreshTokenEveryHour(true);
            Logger.info("refresh token now");
        }
    }

    public void refreshIfNotRunning() {
        if (!refreshing) {
            checkRefreshToken();
        }
    }

    public void refreshTokenOnFailedRequest(RefreshView view) {
        if (mPresenter != null) {
            mPresenter.refreshTokenWithView(view);
        }
    }

    private void refreshTokenEveryHour(boolean withDelay) {
        if (refreshRunning) {
            scheduleTaskExecutor.shutdown();
            scheduleTaskExecutor = Executors
                    .newSingleThreadScheduledExecutor();
        }
        Runnable runnable = () -> {
            // task to run goes here
            if (!refreshing) {
                if (isConnected) {
                    mPresenter.refreshToken();
                    refreshing = true;
                    Logger.info("AUTO SYNC: refresh token called refreshing is true");
                }
            }
        };
        refreshRunning = true;

        //test refresh token
//        scheduleTaskExecutor.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);

        //refresh token
        if (withDelay) {
            scheduleTaskExecutor.scheduleAtFixedRate(runnable, 25, 25, TimeUnit.MINUTES);
        } else {
            scheduleTaskExecutor.scheduleAtFixedRate(runnable, 0, 25, TimeUnit.MINUTES);
        }
    }

    public static Typeface FontMaterial() {
        return typeface;
    }


    public AppCompatActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(AppCompatActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public void setCurrentConnectivity(boolean currentConnectivity) {
        isConnected = currentConnectivity;
    }

    public void getCachedChecks(Boolean checkConnection, boolean mSyncTour) {
        if (getCurrentActivity() == null) return;
        clickToSync = true;
        mDBViewModel = null;
        syncTour = mSyncTour;
        mDBViewModel = ViewModelProviders.of(getCurrentActivity()).get(DBCheckViewModel.class);
        mDBViewModel.getAllChecks().observe(mCurrentActivity, checks -> {
            // Update the cached copy of the words in the adapter.
            Logger.info("DB - > load db zone checks ");
            if (checks == null) return;
            List<DBCheck> tmpChecks = checks;
            Gson gson = new Gson();
            Type type = new TypeToken<DBZoneCheckModel>() {
            }.getType();

            ArrayList<String> unsyncedChecks = new ArrayList<>();
            ArrayList<DBCheck> unSyncedDBChecks = new ArrayList<>();

            for (DBCheck dbCheck : checks) {
                if (!dbCheck.isSync()) {
                    Logger.info("DB - > " + dbCheck.getZoneCheck());

                    DBZoneCheckModel temp = gson.fromJson(dbCheck.getZoneCheck(), type);
                    //
                    //make sure zone checks sending up is valid
                    //
                    if (temp.getGroupId()!=null && temp.getStartZoneCheckCommands().size() > 0 && temp.getZoneCheckId()!=null && temp.getZoneId() != null && temp.getScheduledZoneCheckTimeId() !=null) {

                        // test code add for getting the hazard mode
                        boolean needUpdate = false;
                        if (temp.getAddressHazardCommandModels().size() > 0) {
                            for (ZoneCheckHazard tmpHazard : temp.getAddressHazardCommandModels()) {
                                if (!new Util().isNumeric(tmpHazard.getTypeId())) {
                                    needUpdate = true;
                                }
                            }
                            if (needUpdate) temp.convertHazardsZoneCheckCommandsV2();
                        }

                        if (needUpdate) {
                            String tempCheckString = gson.toJson(temp);
                            unsyncedChecks.add(tempCheckString);
                            Logger.info("need to update string id issue");
                        } else {
                            unsyncedChecks.add(dbCheck.getZoneCheck());
                            Logger.info("need to update string id issue, everything good,");
                        }
//                        unsyncedChecks.add(tempCheckString);
                        unSyncedDBChecks.add(dbCheck);
                        //this is the old methods for adding zone check
//                       unsyncedChecks.add(dbCheck.getZoneCheck());
//                       unSyncedDBChecks.add(dbCheck);
                    }
                }
            }

            if (unsyncedChecks.size() > 0) {
//                found cached zone checks, need to post data to server.
                RequestCallBack requestCallBack = new RequestCallBack() {
                    @Override
                    public void onSucceed() {
                        updateSyncedChecks(tmpChecks);
                    }

                    @Override
                    public void onError(@Nullable VolleyError error) {
                        Logger.info("zone check sync failed; send one by one now");

                        //
                        //send sync request individually, may need a condition on the error type
                        //
                        if (error!=null) {
                            if (error.networkResponse !=null) {
                                if (error.networkResponse.statusCode == 400) {
                                    sendZoneCheckRequestByBatch(unsyncedChecks, unSyncedDBChecks, checks);
                                }
                            }
                        }

                    }
                };

                //
                //only send sync request when there is connection available
                //also check is sync is in progress
                //sync now button can trigger to sync, don't check connection when that happens
                //

                if (clickToSync) {
                    if (checkConnection) {
                        if (isConnected) {
                            if (HazardObserver.getInstance().isSyncInProgress()) return;
                            HazardObserver.getInstance().setSyncInProgress(true);
                            Logger.info("DB - > load db message app sync start check connection true");

                            new Handler().postDelayed(() -> mPresenter.syncCachedZoneChecks(unsyncedChecks.toString(), checks, requestCallBack), 1000);
                        } else {
                            Logger.info("DB - > load db message app sync start check connection true not connected.... shite");
                        }
                    } else {
                        if (HazardObserver.getInstance().isSyncInProgress()) return;
                        HazardObserver.getInstance().setSyncInProgress(true);
                        Logger.info("DB - > load db message app sync start check connection false");

                        //test for failed request
                        new Handler().postDelayed(() -> mPresenter.syncCachedZoneChecks(unsyncedChecks.toString(), checks, requestCallBack), 1000);
                    }
                }
            }
        });

        mDBViewModel.getAllTourChecks().observe(mCurrentActivity, checks -> {
            // Update the cached copy of the words in the adapter.
            Logger.info("DB - > load db tour checks");
            List<DBTourCheck> tmp = checks;
            ArrayList<String> unsyncedChecks = new ArrayList<>();
            Gson gson = new Gson();
            Type type = new TypeToken<DBTourCheckModel>() {
            }.getType();

            for (int i = 0; i < tmp.size(); i++) {
                Logger.info("DB - > tour and zone... tour" + tmp.get(i).getTourZoneCheck());
                DBTourCheckModel temp = gson.fromJson(tmp.get(i).getTourZoneCheck(), type);
                if (temp != null && temp.getTimeStarted() != null && temp.getScheduledSiteTourId() != null && temp.getSiteTourId() != null) {
                    //if status is 0, is
                    if (tmp.get(i).getStatus() == 0) {

                        //
                        //check if type is wrong
                        //
                        boolean needUpdate = false;
                        if (temp.getZoneChecks().size() > 0) {
                            for (DBZoneCheckModel tempCheck : temp.getZoneChecks()) {
                                if (tempCheck.getAddressHazardCommandModels().size() > 0) {
                                    for (ZoneCheckHazard tmpHazard : tempCheck.getAddressHazardCommandModels()) {
                                        if (!new Util().isNumeric(tmpHazard.getTypeId())) {
                                            needUpdate = true;
                                        }
                                    }
                                    if (needUpdate) tempCheck.convertHazardsZoneCheckCommandsV2();
                                }
                            }
                        }
                        String updatedString = gson.toJson(temp);
                        if (needUpdate) {
                            tmp.get(i).setTourZoneCheck(updatedString);
                        }
                        unsyncedChecks.add(tmp.get(i).getTourZoneCheck());
                    }
                }
            }

            if (syncTour) {
                if (unsyncedChecks.size() > 0) {
//                found cached zone checks, need to post data to server.
                    RequestCallBack requestCallBack = new RequestCallBack() {
                        @Override
                        public void onSucceed() {
                            removeSyncedTourChecks(tmp);
                        }

                        @Override
                        public void onError(@Nullable VolleyError error) {

                        }
                    };

                    //
                    //only send sync request when there is connection available
                    //also check is sync is in progress
                    //sync now button can trigger to sync, don't check connection when that happens
                    //

                    if (clickToSync) {
                        if (checkConnection) {
                            if (isConnected) {
                                if (HazardObserver.getInstance().isSyncTourInProgress()) return;
                                HazardObserver.getInstance().setSyncTourInProgress(true);

                                if (!HazardObserver.getInstance().isSyncInProgress()) {
                                    HazardObserver.getInstance().setSyncInProgress(true);
                                }

                                Logger.info("DB - > load db message app tour sync start");
                                new Handler().postDelayed(() -> mPresenter.syncCachedTourZoneChecks(unsyncedChecks.toString(), tmp, requestCallBack), 1000);
                            }
                        } else {
                            if (HazardObserver.getInstance().isSyncTourInProgress()) return;
                            HazardObserver.getInstance().setSyncTourInProgress(true);
                            HazardObserver.getInstance().setSyncInProgress(true);

                            Logger.info("DB - > load db message app tour sync start");
                            new Handler().postDelayed(() -> mPresenter.syncCachedTourZoneChecks(unsyncedChecks.toString(), tmp, requestCallBack), 1000);
                        }
                    }
                }

                syncTour = false;

            }
        });
        clickToSync = false;
    }

    //
    //temp fix
    //
    private void sendZoneCheckRequestByBatch(ArrayList<String> unsyncedChecks, ArrayList<DBCheck> unSyncedDBChecks, List<DBCheck> checks) {

        for (int i=0;i<unsyncedChecks.size();i++) {
            final int pos = i;
            RequestCallBack requestCallBack = new RequestCallBack() {
                @Override
                public void onSucceed() {
                    List<DBCheck> tmp = new ArrayList<>();
                    tmp.add(unSyncedDBChecks.get(pos));
                    updateSyncedChecks(tmp);
                    Logger.info("zone check sync failed; send one by one now one has successful");
                }

                @Override
                public void onError(@Nullable VolleyError error) {

                }
            };
             String check = "[" +unsyncedChecks.get(i) +"]";
             new Handler().postDelayed(() -> mPresenter.syncCachedZoneChecks(check, checks, requestCallBack), 1000);
        }
    }

    public void insertCheck(DBCheck check) {
        if (getCurrentActivity() == null) {
            Logger.info("DB -> Save this to DB  insert not good");
            return;
        }
        if (mDBViewModel == null) {
            mDBViewModel = ViewModelProviders.of(getCurrentActivity()).get(DBCheckViewModel.class);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<DBZoneCheckModel>() {
        }.getType();
        DBZoneCheckModel temp = gson.fromJson(check.getZoneCheck(), type);

        //
        //make sure zone checks is valid before save it...
        //
        Logger.info("DB -> Save this to DB  insert");
        if (temp.getStartZoneCheckCommands().size() > 0) {
            mDBViewModel.insert(check);
        }
    }

    public void updateSyncedChecks(List<DBCheck> tmp) {
        if (getCurrentActivity() == null) return;
        if (mDBViewModel == null) {
            mDBViewModel = ViewModelProviders.of(getCurrentActivity()).get(DBCheckViewModel.class);
        }
        for (DBCheck dbCheck : tmp) {
            updateSyncedCheck(mDBViewModel, dbCheck);
        }
    }

    public void updateSyncedCheck(DBCheck dbCheck) {
        if (getCurrentActivity() == null) return;
        if (mDBViewModel == null) {
            mDBViewModel = ViewModelProviders.of(getCurrentActivity()).get(DBCheckViewModel.class);
        }
        updateSyncedCheck(mDBViewModel, dbCheck);
    }

    public void updateSyncedCheck(DBCheckViewModel dbCheckViewModel, DBCheck dbCheck) {
        dbCheck.setSync(true);
        dbCheckViewModel.updateSync(dbCheck);
    }

    //tour checks...
    public void insertTourCheck(DBTourCheck check) {
        if (getCurrentActivity() == null) return;
        if (mDBViewModel == null) {
            mDBViewModel = ViewModelProviders.of(getCurrentActivity()).get(DBCheckViewModel.class);
        }

        if (isTourCheckValid(check)) {
            Logger.info("DB - > tour and zone save: " + check.getTourZoneCheck());
            mDBViewModel.insertTourCheck(check);
        }
    }

    public void updateTourCheck(DBTourCheck check) {
        if (getCurrentActivity() == null) return;
        if (mDBViewModel == null) {
            mDBViewModel = ViewModelProviders.of(getCurrentActivity()).get(DBCheckViewModel.class);
        }

        mDBViewModel.updateTourCheck(check);
    }

    private boolean isTourCheckValid(DBTourCheck check) {
        if (check.getTourZoneCheck() == null) return false;
        return true;
    }

    public void removeSyncedTourChecks(List<DBTourCheck> tmp) {
        if (getCurrentActivity() == null) return;
        if (mDBViewModel == null) {
            mDBViewModel = ViewModelProviders.of(getCurrentActivity()).get(DBCheckViewModel.class);
        }
        for (int i = 0; i < tmp.size(); i++) {
            Gson gson = new Gson();
            Type type = new TypeToken<DBTourCheckModel>() {
            }.getType();
            DBTourCheckModel temp = gson.fromJson(tmp.get(i).getTourZoneCheck(), type);

            if (temp != null) {
                String time = temp.getTimeStarted();
                if (time != null) {
                    if (new Util().isToday(time)) {
//                if (new Util().isToday(time) && temp.getTimeCompleted() != null) {
                        //if it is today tour and tour not completed yet, don't delete...
                        DBTourCheck check = tmp.get(i);
                        String checkString = check.getTourZoneCheck();
                        DBTourCheckModel tempTourCheck = gson.fromJson(checkString, type);

                        for (DBZoneCheckModel checkModel : tempTourCheck.getZoneChecks()) {
                            checkModel.setSync(true);
                        }
                        String updatedCheckString = gson.toJson(tempTourCheck);
                        check.setTourZoneCheck(updatedCheckString);
                        tmp.get(i).setTourZoneCheck(updatedCheckString);
                        tmp.get(i).setStatus(1);
                        mDBViewModel.updateTourCheck(tmp.get(i));

                    } else {
                        mDBViewModel.deleteTourCheck(tmp.get(i));
                    }

                }
            }
        }
    }

    @Override
    public void refreshSuccessfully(@Nullable JSONObject result) {
        refreshing = false;
        if (refreshView != null) {
            refreshView.tokenRefreshSuccessfully(result);
        }
    }

    @Override
    public void refreshWithError(@Nullable VolleyError error) {
        refreshing = false;
        boolean timeOutError = false;
        if (error != null) {
            if (error instanceof TimeoutError) {
                timeOutError = true;
            }
        }
        if (timeOutError) {
            if (refreshView != null) {
                refreshView.tokenRefreshWithError(error);
            }
            return;
        }
        scheduleTaskExecutor.shutdown();
        refreshRunning = false;
        scheduleTaskExecutor = Executors
                .newSingleThreadScheduledExecutor();
        if (refreshView != null) {
            refreshView.tokenRefreshWithError(error);
        }
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            new autoSyncTask().execute();
        }
    }

    private class autoSyncTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... param) {
            //Do some work
            return null;
        }

        protected void onPostExecute(Void param) {
            Logger.info("AUTO SYNC: check if there is anything to sync scheduler get checks and sync now");
            processAutoSync();
        }
    }

    private void processAutoSync() {
        if (isConnected && PreferenceHelper.getInstance(this).getRefreshToken() !=null)
            getCachedChecks(false, true);
    }

    private void timerAutoSync() {
        Timer myTimer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        myTimer.scheduleAtFixedRate(myTimerTask, 0, 30000);
    }

    public void deleteAllFromDB() {
        if (getCurrentActivity() == null) return;
        if (mDBViewModel == null) {
            mDBViewModel = ViewModelProviders.of(getCurrentActivity()).get(DBCheckViewModel.class);
        }
        mDBViewModel.deleteAllChecks();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            if (receiverEnabled) {
                unregisterReceiver(mNetworkReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
