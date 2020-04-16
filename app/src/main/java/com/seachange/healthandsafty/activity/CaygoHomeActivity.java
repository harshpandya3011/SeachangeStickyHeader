package com.seachange.healthandsafty.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.View.RequestErrorView;
import com.seachange.healthandsafty.fragment.HomeFragment;
import com.seachange.healthandsafty.fragment.ScheduleFragment;
import com.seachange.healthandsafty.fragment.SiteGroupDashboardFragment;
import com.seachange.healthandsafty.helper.CheckPreference;
import com.seachange.healthandsafty.helper.ForegroundBackgroundListener;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.HomeData;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.helper.UserDateHelper;
import com.seachange.healthandsafty.model.UserData;
import com.seachange.healthandsafty.view.ForegroundBackgroundView;
import com.seachange.healthandsafty.viewmodel.SiteGroupViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class CaygoHomeActivity extends BaseActivity implements Observer, ForegroundBackgroundView, RequestErrorView {

    private HomeFragment homeFragment;
    private ScheduleFragment scheduleFragment;
    private FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private HomeData mHomeData;
    private int mPage;
    private boolean isLive;
    public SwipeRefreshLayout swipeLayout;
    private BroadcastReceiver mTimeTickReceiver;
    private ForegroundBackgroundListener appStateListener;
    private SiteGroupViewModel siteGroupViewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            updateSwipeRefreshProgressStatus(item.getItemId());
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(mCtx.getString(R.string.caygo));
                    }
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.caygo_home_frame, homeFragment);
                    fragmentTransaction.commit();
                    mPage = 1;
                    return true;
                case R.id.navigation_time:
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(mCtx.getString(R.string.title_schedule));
                    }
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.caygo_home_frame, scheduleFragment);
                    fragmentTransaction.commit();
                    mPage = 2;
                    return true;
                case R.id.navigation_site:
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(R.string.site_dashboard);
                    }
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.caygo_home_frame, SiteGroupDashboardFragment.newInstance(false));
                    fragmentTransaction.commit();
                    mPage = 3;
                    return true;
                case R.id.navigation_group:
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(R.string.group_dashboard);
                    }
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.caygo_home_frame, SiteGroupDashboardFragment.newInstance(true));
                    fragmentTransaction.commit();
                    mPage = 4;
                    return true;
            }
            return false;
        }

    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caygo_home);

        initViewModelAndObserver();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mCtx.getString(R.string.caygo));
            getSupportActionBar().setElevation(0);
        }
        isLive = true;
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        disableShiftMode(navigation);
        mHomeData = new HomeData(mCtx, "Home Tour");

        homeFragment = new HomeFragment();
        scheduleFragment = new ScheduleFragment();
        mPage = 1;
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.caygo_home_frame, homeFragment, "Home");
        fragmentTransaction.commit();

        swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(() -> {

            if (mPage == 3) {
                siteGroupViewModel.refreshSiteStatistics();
                return;
            }

            if (mPage == 4) {
                siteGroupViewModel.refreshGroupStatistics();
                return;
            }

            //your method to refresh content
            if (connected) {
                PreferenceHelper.getInstance(mCtx).fetchScheduledDataNotifyWithCallBack(this);
                Logger.info("pull to refresh, get the latest schedule data");
            } else {
                swipeLayout.setRefreshing(false);
            }
        });
        HazardObserver.getInstance().setCheckEnded(false);

        appStateListener = new ForegroundBackgroundListener(this);
        ProcessLifecycleOwner.get()
                .getLifecycle()
                .addObserver(appStateListener);

        showNoConnectionBaseView = true;

        updateSwipeRefreshProgressStatus(navigation.getSelectedItemId());
    }

    private void initViewModelAndObserver() {
        siteGroupViewModel = ViewModelProviders.of(this).get(SiteGroupViewModel.class);
        siteGroupViewModel.getProgressSiteLiveData().observe(this, showProgress -> {
            if (mPage == 3) {
                swipeLayout.setRefreshing(showProgress != null && showProgress);
            }
        });
        siteGroupViewModel.getProgressGroupLiveData().observe(this, showProgress -> {
            if (mPage == 4) {
                swipeLayout.setRefreshing(showProgress != null && showProgress);
            }
        });
    }

    private void updateSwipeRefreshProgressStatus(@IdRes int itemId) {
        if (itemId == R.id.navigation_site) {
            Boolean siteProgress = siteGroupViewModel.getProgressSiteLiveData().getValue();
            swipeLayout.setRefreshing(siteProgress != null && siteProgress);
        } else if (itemId == R.id.navigation_group) {
            Boolean groupProgress = siteGroupViewModel.getProgressGroupLiveData().getValue();
            swipeLayout.setRefreshing(groupProgress != null && groupProgress);
        } else {
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (PreferenceHelper.getInstance(mCtx).getHomeScreenRefresh()) {
            PreferenceHelper.getInstance(mCtx).fetchScheduledDataNotifyWithCallBack(this);
            PreferenceHelper.getInstance(mCtx).setHomeScreenRefresh(false);
            Logger.info("fetch data -> " + "home screen resume need to refresh ");
        } else {
            Logger.info("fetch data -> " + "home screen resume");
        }

        if (mHomeData != null) {
            mTimeTickReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    //before the first check...... check time has reached first zone check time
                    if (mHomeData.beforeFirstCheck) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        try {
                            if (mHomeData.siteCheck.getCheckTimes().size() > 0) {
                                Date mDate = sdf.parse(mHomeData.siteCheck.getCheckTimes().get(0).getCheck_date());
                                Date currentDate = new Date();
                                Logger.info("time clock tick, zone check available refresh page panel");

                                if (currentDate.after(mDate)) {
                                    mHomeData = new HomeData(mCtx, "Home Tour");
                                    switch (mPage) {
                                        case 1:
                                            homeFragment.getDataFromParent();
                                            break;
                                        case 2:
                                            scheduleFragment.getDataFromParent();
                                            break;
                                        default:
                                            return;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (mHomeData.afterLastCheck) {
                        if (!mHomeData.getCheckEndTime()) {
                            mHomeData = new HomeData(mCtx, "Home Tour");
                            switch (mPage) {
                                case 1:
                                    homeFragment.getDataFromParent();
                                    break;
                                case 2:
                                    scheduleFragment.getDataFromParent();
                                    break;
                                default:
                                    return;
                            }
                            Logger.info("time clock tick last check page reload................");

                        }
                        Logger.info("time clock tick last check................");
                    }
                    Logger.info("time clock tick................");
                }
            };
            registerReceiver(mTimeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
            Logger.info("time clock reg................");

            if (mHomeData.afterLastCheck) {
                if (!mHomeData.getCheckEndTime()) {
                    mHomeData = new HomeData(mCtx, "Home Tour");
                    switch (mPage) {
                        case 1:
                            homeFragment.getDataFromParent();
                            break;
                        case 2:
                            scheduleFragment.getDataFromParent();
                            break;
                        default:
                            return;
                    }
                    Logger.info("time clock tick last check page reload................");

                }
            }
        }

        if (mHomeData != null && mHomeData.managerTour != null) {
            if (mHomeData.mCurrentCheck != null) {
                mHomeData.mCurrentCheck.setScheduledCheckses(mHomeData.managerTour.getTourSiteZone());
                //check time zone.
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                SimpleDateFormat fmtDay = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                try {
                    Date mDate = sdf.parse(mHomeData.siteCheck.getDate());
                    if (!fmtDay.format(c).equals(fmtDay.format(mDate))) {
                        PreferenceHelper.getInstance(mCtx).fetchScheduledDataNotifyWithCallBack(this);
                        Logger.info("date changed, need to refresh data: " + fmt.format(c) + " saved date: " + fmt.format(mDate));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (mHomeData != null) mHomeData.updateScheduleInPrefs();
        HazardObserver.getInstance().addObserver(this);
        if (mPage == 2 && scheduleFragment != null) {
            scheduleFragment.scrollToCurrent();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimeTickReceiver != null) {
            unregisterReceiver(mTimeTickReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLive = false;
        ProcessLifecycleOwner.get()
                .getLifecycle()
                .removeObserver(appStateListener);
        new UserDateHelper(mCtx).resetUserLoggedInDetails();
        HazardObserver.getInstance().deleteObserver(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        CheckPreference mZonePref = new CheckPreference(mCtx);
        UserData mUser = mZonePref.getCurrentCaygoUser();
        if (mUser != null) {
            menu.findItem(R.id.action_logout).setTitle("Exit " + (mUser.getFirstName() == null ? "" : mUser.getFirstName()));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @SuppressLint("RestrictedApi")
//    public static void disableShiftMode(BottomNavigationView view) {
//        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
//        try {
//            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
//            shiftingMode.setAccessible(true);
//            shiftingMode.setBoolean(menuView, false);
//            shiftingMode.setAccessible(false);
//            for (int i = 0; i < menuView.getChildCount(); i++) {
//                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
//                //noinspection RestrictedApi
//                item.setShiftingMode(false);
//                // set once again checked value, so view will be updated
//                //noinspection RestrictedApi
//                item.setChecked(item.getItemData().isChecked());
//            }
//        } catch (NoSuchFieldException e) {
//            Log.e("BNVHelper", "Unable to get shift mode field", e);
//        } catch (IllegalAccessException e) {
//            Log.e("BNVHelper", "Unable to change value of shift mode", e);
//        }
//    }

    public HomeData getmHomeData() {
        return mHomeData;
    }

    public void loadNextCheckTime() {
        mHomeData.updateCurrentCheck();
        switch (mPage) {
            case 1:
                homeFragment.getDataFromParent();
                break;
            case 2:
                scheduleFragment.getDataFromParent();
                break;
            default:
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (!isLive) return;
        boolean isSync = updateSyncFromObserver(observable);
        if (isSync) {
            HazardObserver.getInstance().resetSyncObserver();
        } else {
            HazardObserver observer = (HazardObserver) observable;
            if (observer.isScheduledReceived()) {

                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
                mHomeData = new HomeData(mCtx, "Home Tour");
                switch (mPage) {
                    case 1:
                        homeFragment.getDataFromParent();
                        break;
                    case 2:
                        scheduleFragment.getDataFromParent();
                        break;
                    default:
                        return;
                }
                Logger.info("fetch data -> home data received.");
                HazardObserver.getInstance().setScheduledReceived(false);
            }
        }
    }

    @Override
    public void backForeground() {

    }

    @Override
    public void wentBackground() {
        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList;
        if (mngr != null) {
            taskList = mngr.getRunningTasks(10);
            if (taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
                Logger.info("background - > This is last activity in the stack");
                this.finish();
            } else {
                Logger.info("background - > This is not last activity in the stack");
            }
        }
    }

    @Override
    public void requestSucceed() {
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void requestError(VolleyError result) {
        if (connected) {
            swipeLayout.setRefreshing(false);
        }

        //401, show login screen...
        if (result != null) {
            if (result.networkResponse != null) {
                if (result.networkResponse.statusCode == 401 && PreferenceHelper.getInstance(mCtx).getRefreshToken() == null) {
                    viewLogin();
                }
            }
        }
    }
}
