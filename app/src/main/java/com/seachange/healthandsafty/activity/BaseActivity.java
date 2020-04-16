package com.seachange.healthandsafty.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seachange.healthandsafty.BuildConfig;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.activity.checklist_templates.CheckListTemplateActivity;
import com.seachange.healthandsafty.activity.healthandsafety.HealthAndSafetyActivity;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.db.DBCheck;
import com.seachange.healthandsafty.db.DBCheckViewModel;
import com.seachange.healthandsafty.db.DBTourCheck;
import com.seachange.healthandsafty.helper.CheckPreference;
import com.seachange.healthandsafty.helper.DBTourCheckHelper;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.UserData;
import com.seachange.healthandsafty.nfc.ui.NFCMainActivity;
import com.seachange.healthandsafty.nfc.ui.NFCSetUpActivity;
import com.seachange.healthandsafty.utils.Util;
import com.seachange.healthandsafty.utils.UtilStrings;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


/**
 * Created by kevinsong on 12/09/2017.
 */

public class BaseActivity extends BaseViewActivity {

    protected Context mCtx;
    protected AVLoadingIndicatorView mIndicator;
    protected Boolean isLive = true, loadDB = true, disableNFC;
    protected BroadcastReceiver mNetworkReceiver;
    public SCApplication mApplication;
    public boolean connected, showNoConnectionDialog, connectionDailogShowing, dateTimeDailogShowing, showNoConnectionBaseView = false;
    protected MaterialDialog connectionDialog;
    protected MaterialDialog dateTimeDialog;
    private ArrayList<DBZoneCheckModel> mDBList = new ArrayList<>();
    private ArrayList<DBTourCheckModel> mDBTourList = new ArrayList<>();
    private DBCheckViewModel mDBViewModel;
    private DBTourCheckHelper mDBTourChecker;
    private NfcAdapter mNfcAdapter;
    private CaygoSite mCaygoSite;
    private AppCompatActivity childActivity;

    public void setChildActivity(AppCompatActivity activity) {
        this.childActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getApplicationContext();
        mApplication = (SCApplication) this.getApplicationContext();
        mDBList = new ArrayList<>();
        mDBTourChecker = new DBTourCheckHelper(mCtx);
        setShowNoConnectionView(showNoConnectionBaseView);
        connected = new Util().isNetworkAvailable(mCtx);
        startNetWorkReceiver();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        disableNFC = true;
        mCaygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();

    }

    private void startNetWorkReceiver() {
        mNetworkReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                connected = new Util().isNetworkAvailable(mCtx);
                Logger.info("connection changes: " + connected);
                setMConnected(connected);
                if (connected) connectionEstablished();
                else connectionLost();
                if (showNoConnectionDialog) {
                    if (connected) {
                        if (connectionDialog != null) {
                            connectionDialog.dismiss();
                            connectionDailogShowing = false;
                        }
                    } else {
                        showNoConnectionDialog();
                    }
                } else {
                    if (connected) {
                        if (connectionDialog != null) {
                            connectionDialog.dismiss();
                            connectionDailogShowing = false;
                        }
                    }
                }
            }
        };
        registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setShowNoConnectionView(showNoConnectionBaseView);
        isLive = true;
        connected = new Util().isNetworkAvailable(mCtx);
        mApplication.setCurrentActivity(this);
        mApplication.setCurrentConnectivity(connected);
        if (connectionDialog != null && new Util().isNetworkAvailable(mCtx)) {
            connectionDialog.dismiss();
        }

        if (loadDB) loadUnsyncedCheck();
        if (disableNFC) {

            setupForegroundDispatch(this, mNfcAdapter);
        }

        if (mNetworkReceiver == null) {
            startNetWorkReceiver();
        }

        int autoZone = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);
        Logger.info("======autoone: " + autoZone);
        if (childActivity == null) {
            if (autoZone != 1) {
                showDateTimeDialog();
            } else {
                if (dateTimeDialog != null && dateTimeDailogShowing) {
                    dateTimeDialog.dismiss();
                    dateTimeDailogShowing = false;
                }
            }
        }

    }

    private void loadUnsyncedCheck() {
        mDBViewModel = null;
        mDBViewModel = ViewModelProviders.of(this).get(DBCheckViewModel.class);
        mDBViewModel.getAllChecks().observe(this, checks -> {
            // Update the cached copy of the words in the adapter.
            if (checks == null) return;
            if (mDBList == null) {
                mDBList = new ArrayList<>();
            } else {
                mDBList.clear();
            }

            Gson gson = new Gson();
            int notSyncedCount = 0;
            for (DBCheck check : checks) {
                DBZoneCheckModel dbZoneCheckModel
                        = gson.fromJson(check.getZoneCheck(), DBZoneCheckModel.class);
                boolean sync = check.isSync();
                if (!sync) notSyncedCount++;
                dbZoneCheckModel.setSync(sync);
                mDBList.add(dbZoneCheckModel);
            }
            setNotSyncedChecks(notSyncedCount);
        });

        mDBViewModel.getAllTourChecks().observe(this, checks -> {
            // Update the cached copy of the words in the adapter.
            List<DBTourCheck> tmp = checks;
            Type type = new TypeToken<DBTourCheckModel>() {
            }.getType();
            ArrayList<String> arrayList = new ArrayList<>();
            Gson gson = new Gson();
            for (int i = 0; i < tmp.size(); i++) {
                DBTourCheckModel temp = gson.fromJson(tmp.get(i).getTourZoneCheck(), type);
                if (temp != null && temp.getTimeStarted() != null && tmp.get(i).getStatus() == 0 && temp.getScheduledSiteTourId() != null && temp.getSiteTourId() != null) {
                    arrayList.add(tmp.get(i).getTourZoneCheck());
                }
            }
            mDBTourList = mDBTourChecker.getUnsyncedTourChecks(arrayList.toString());
            setNotTourSyncedChecks(mDBTourList.size());
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        clearReferences();
        isLive = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
//        try {
//            unregisterReceiver(mNetworkReceiver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReferences();
        try {
            if (mNetworkReceiver != null) unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (connectionDialog != null) connectionDialog.dismiss();
    }


    private void clearReferences() {
        Activity currActivity = mApplication.getCurrentActivity();
        if (this.equals(currActivity))
            mApplication.setCurrentActivity(null);
    }

    protected void refreshCaygoSiteData() {
        mCaygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        invalidateOptionsMenu();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (BuildConfig.DEBUG) {
            if (menu.findItem(R.id.action_settings) != null) {
                menu.findItem(R.id.action_settings).setVisible(true);
            }
            if (menu.findItem(R.id.action_ra_flow1) != null) {
                menu.findItem(R.id.action_ra_flow1).setVisible(true);
            }
            if (menu.findItem(R.id.action_nfc) != null) {
                menu.findItem(R.id.action_nfc).setVisible(true);
            }
            if (menu.findItem(R.id.action_nfc_read) != null) {
                menu.findItem(R.id.action_nfc_read).setVisible(true);
            }
            if (menu.findItem(R.id.action_check_list) != null) {
                menu.findItem(R.id.action_check_list).setVisible(true);
            }
            if (menu.findItem(R.id.actionCheckListTemplates) != null) {
                menu.findItem(R.id.actionCheckListTemplates).setVisible(true);
            }
            if (menu.findItem(R.id.action_schedule) != null) {
                menu.findItem(R.id.action_schedule).setVisible(true);
            }
        } else {
            menu.findItem(R.id.action_settings).setVisible(false);
            UserData currentCaygoUser = new CheckPreference(this).getCurrentCaygoUser();
            if (currentCaygoUser != null) {
                //enable manage users option menu for site enabled and manager role
                Integer userRole = currentCaygoUser.getUserRole();
                if (userRole != null && userRole == UserData.USER_ROLE_MANAGER && mCaygoSite != null && mCaygoSite.getToggles().isCanManageUsers()) {
                    menu.findItem(R.id.action_ra_flow1).setVisible(true);
                } else {
                    menu.findItem(R.id.action_ra_flow1).setVisible(false);
                }
                //schedule zones menu option button
                if (userRole != null && userRole == UserData.USER_ROLE_MANAGER && mCaygoSite != null && mCaygoSite.getToggles().isCanManageZoneSettings()) {
                    menu.findItem(R.id.action_schedule).setVisible(true);
                } else {
                    menu.findItem(R.id.action_schedule).setVisible(false);
                }

                //nfc menu option button
                if (userRole != null && userRole == UserData.USER_ROLE_MANAGER && mCaygoSite != null && mCaygoSite.isUsingNfc()) {
                    menu.findItem(R.id.action_nfc).setVisible(true);
                } else {
                    menu.findItem(R.id.action_nfc).setVisible(false);
                }

            } else {
                menu.findItem(R.id.action_ra_flow1).setVisible(false);
                menu.findItem(R.id.action_schedule).setVisible(false);
                menu.findItem(R.id.action_nfc).setVisible(false);
            }

            if (mCaygoSite != null && mCaygoSite.isUsingNfc()) {
                menu.findItem(R.id.action_nfc_read).setVisible(true);
            } else {
                menu.findItem(R.id.action_nfc_read).setVisible(false);
            }
            menu.findItem(R.id.action_check_list).setVisible(false);
            menu.findItem(R.id.actionCheckListTemplates).setVisible(false);
        }

        menu.findItem(R.id.app_version).setTitle(getString(R.string.version_value, BuildConfig.VERSION_NAME));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //hide setting for production
        //show menu setting option for debug
        if (id == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                this.finish();
            }
        } else if (id == R.id.action_logout) {
            userLogOut();
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_ra_flow1) {
            PreferenceHelper.getInstance(mCtx).saveRAFlow(1);
            Intent intent = new Intent(this, ManageUsersActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_schedule) {
            Intent intent = new Intent(this, ZoneSchedulingActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_nfc) {
            Intent intent = new Intent(this, NFCSetUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_nfc_read) {
            Intent intent = new Intent(this, NFCMainActivity.class);
            intent.putExtra(UtilStrings.NFC_TYPE, 1);
            startActivity(intent);
        } else if (id == R.id.action_check_list) {
            Intent intent = new Intent(this, HealthAndSafetyActivity.class);
            startActivity(intent);
        } else if (id == R.id.actionCheckListTemplates) {
            Intent intent = new Intent(this, CheckListTemplateActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showNoConnectionDialog() {
        if (connectionDailogShowing) {
            return;
        }

        if (!showNoConnectionBaseView) {
            return;
        }

        if (!isLive) {
            return;
        }

        String tmp = "Please return to coverage to proceed.";
        String title = mCtx.getResources().getString(R.string.fa_wifi_off) + " Connection Lost \n";
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .content(tmp)
                .title(title)
                .typeface(SCApplication.FontMaterial(), null)
                .titleColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .contentColor(ContextCompat.getColor(mCtx, R.color.alertContent));

        connectionDialog = builder.build();
        connectionDialog.setCancelable(false);
        connectionDialog.show();
        connectionDailogShowing = true;
    }

    public Boolean checkShowDateTimeDialog() {
        return dateTimeDailogShowing;
    }

    public MaterialDialog showDateTimeDialog() {
        dateTimeDialog = new MaterialDialog.Builder(this)
                .content(mCtx.getResources().getString(R.string.clock_error_msg))
                .title(mCtx.getResources().getString(R.string.clock_error))
                .titleColorRes(R.color.alertTitle)
                .contentColorRes(R.color.alertContent)
                .positiveText(R.string.adjust_date)
                .positiveColorRes(R.color.colorDefaultYellow)
                .onPositive((dialog, which) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                }).build();

        dateTimeDialog.setCancelable(false);
        dateTimeDialog.show();

        dateTimeDailogShowing = true;
        return dateTimeDialog;
    }

    public void userLogOut() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        PreferenceHelper.getInstance(mCtx).saveUpdatedToken(null);
        PreferenceHelper.getInstance(mCtx).saveRefreshToken(null);
        this.finish();
    }

    public void viewLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    protected void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    protected void connectionLost() {
        if (showNoConnectionBaseView) {
            if (getMNoConnectionView() != null) {
                updateBarText();
                getMNoConnectionView().setVisibility(View.VISIBLE);
            }
        }
        setActiveConnection(false);
    }

    protected void connectionEstablished() {
        if (getMNoConnectionView() != null) {
            removeSyncViewIfPossible();
        }
        setActiveConnection(true);
        removeSyncViewIfShowing();
        hideNoDataViewIfShowing();
    }

    protected boolean updateSyncFromObserver(Observable observable) {
        boolean isSync = false;
        HazardObserver observer = (HazardObserver) observable;
        if (observer.isSyncInProgress()) {

            syncingInProgress();
            isSync = true;
        } else if (observer.isSyncComplete()) {
            syncingEndSuccessfully();
            isSync = true;
        } else if (observer.isSyncEndWithError()) {
            syncingEndWithError();
            isSync = true;
        }

        HazardObserver.getInstance().resetSyncObserver();
        return isSync;
    }

    protected ArrayList<DBZoneCheckModel> getCurrentSavedCheckList() {
        return mDBList;
    }

    public void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        if (activity != null) {
            final Intent intent = new Intent(activity, activity.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            final PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
            if (pendingIntent != null && adapter != null) {
                adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
            }
        }
    }
}
