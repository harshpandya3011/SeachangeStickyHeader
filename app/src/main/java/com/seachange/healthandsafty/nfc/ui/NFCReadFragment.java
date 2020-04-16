package com.seachange.healthandsafty.nfc.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.android.volley.VolleyError;
import com.seachange.healthandsafty.BuildConfig;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.activity.BaseActivity;
import com.seachange.healthandsafty.activity.CaygoZoneCheckActivity;
import com.seachange.healthandsafty.activity.EndCheckActivity;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.db.DBCheck;
import com.seachange.healthandsafty.db.DBTourCheck;
import com.seachange.healthandsafty.helper.CheckPreference;
import com.seachange.healthandsafty.helper.DBTourCheckHelper;
import com.seachange.healthandsafty.helper.DBZoneCheckHelper;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.helper.ZoneCheckHelper;
import com.seachange.healthandsafty.helper.interfacelistener.RequestCallBack;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManager;
import com.seachange.healthandsafty.helper.tourcheck.DBTourCheckManagerView;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.nfc.helper.NFCSetUpHelper;
import com.seachange.healthandsafty.nfc.model.NFCZoneData;
import com.seachange.healthandsafty.nfc.view.DrawDottedCurve;
import com.seachange.healthandsafty.nfc.view.NFCListener;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NFCReadFragment extends DialogFragment implements DBTourCheckManagerView {

    public static final String TAG = NFCReadFragment.class.getSimpleName();

    public static NFCReadFragment newInstance() {

        return new NFCReadFragment();
    }

    private TextView mTvMessage, mLogo, mDialogTitle, mSiteTitle, mZoneTitle, mPoint, mTagId;
    private Button mCloseBtn;
    private NFCListener mListener;
    private LinearLayout mDefaultView, mDataView;
    private NFCSetUpHelper setUpHelper;
    private int mNFCType = 0;
    private DBZoneCheckHelper mDBChecker;
    private DBTourCheckManager mDBTourCheckManager;
    private DBTourCheckHelper mDBTourCheckHelper;
    private Context mCtx;
    private NFCZoneData mNFCData;
    private DrawDottedCurve mCurve;
    private RelativeLayout mCenterView;

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initCurveView();
    }

    private void initData() {
        mCtx = getActivity().getApplicationContext();
        new PreferenceHelper(mCtx).saveShowTourQRFailed(false);
        mDBTourCheckManager = new DBTourCheckManager(this, (BaseActivity)getActivity());
        mDBTourCheckHelper = new DBTourCheckHelper(mCtx);
        mDBChecker = new DBZoneCheckHelper(mCtx);
    }

    @Override
    public void onResume() {
        super.onResume();
        HazardObserver.getInstance().setQrCodeFailed(false);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nfcread,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mTvMessage = view.findViewById(R.id.tv_message);
        mLogo = view.findViewById(R.id.nfc_read_logo);
        mDefaultView = view.findViewById(R.id.nfc_tag_default_view);
        mDataView =  view.findViewById(R.id.nfc_data_view);
        mSiteTitle = view.findViewById(R.id.nfc_site_name);
        mZoneTitle = view.findViewById(R.id.nfc_zone_name);
        mPoint = view.findViewById(R.id.nfc_point_name);
        mTagId = view.findViewById(R.id.nfc_point_id);
        mCurve = view.findViewById(R.id.read_curve_view);
        mCenterView = view.findViewById(R.id.nfc_read_view_anim_rel);

        mDialogTitle = view.findViewById(R.id.nfc_dialog_title);
        mCloseBtn = view.findViewById(R.id.nfc_tag_close);
        mLogo.setTypeface(SCApplication.FontMaterial());
        mDataView.setVisibility(View.GONE);

        mCloseBtn.setOnClickListener(view1 -> {
                existScreen();
        });

        setUpHelper = new NFCSetUpHelper(getActivity().getApplication());
        initMessageView();
    }

    public void initType(Integer type) {
        mNFCType = type;
    }

    private void initMessageView() {
        switch (mNFCType) {
            case 2:
                mCenterView.setVisibility(View.VISIBLE);
                mDialogTitle.setText(getActivity().getApplicationContext().getString(R.string.message_read_tag));
                break;
            case 3:
                mCenterView.setVisibility(View.GONE);
                mDialogTitle.setText(getActivity().getApplicationContext().getString(R.string.message_start_tag));
                break;
            case 4:
                mCenterView.setVisibility(View.GONE);
                mDialogTitle.setText(getActivity().getApplicationContext().getString(R.string.message_end_tag));
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (NFCMainActivity)context;
        mListener.onDialogDisplayed();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
        if (mCurve !=null)
            mCurve.stopTimer();
    }

    public void onNfcDetected(Ndef ndef, NdefMessage ndefMessage){
        readFromNFC(ndef, ndefMessage);
    }

    private void initCurveView() {
        mCurve.init(getActivity().getApplicationContext(), 20,65,208,50);
    }

    private void readFromNFC(Ndef ndef, NdefMessage ndefMessage) {
         try {
//                ndef.connect();
//                NdefMessage ndefMessage = ndef.getNdefMessage();
                if (ndefMessage != null) {
                    String message = new String(ndefMessage.getRecords()[0].getPayload());
                    if (message != null &&  !message.isEmpty() && !message.equals("null")) {
                        mNFCData = setUpHelper.getDataFromNFCTagURL(message);
                        Log.d(TAG, "readFromNFCdf: " + message);

                        if (mNFCData == null) {
                            showErrorView();
                        } else {
                            if (mNFCType ==1) {
                                showDataView(mNFCData);
                            } else if(mNFCType ==3 || mNFCType == 4) {
                                if (isNFCTagMatch(mNFCData)) {
                                    validateDataFromNFCTag(mNFCData);
                                    if (mNFCType == 3) {
                                        new CheckPreference(getActivity().getApplicationContext()).saveStartedQRCode(mNFCData.getId());
                                    }
                                }else {
                                    if(mNFCType ==3)
                                        showStartOrEndZoneCheckErrorView(true);
                                    else
                                        showStartOrEndZoneCheckErrorView(false);
                                }
                            }
                        }
                    } else {
                        showEmptyView();
                        Log.d(TAG, "readFromNFC: " + "Empty");
                    }
                } else {
                    Log.d(TAG, "readFromNFC: " + "Empty");
                    showEmptyView();
                }
//                ndef.close();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorView();
            }
    }

    private void validateDataFromNFCTag(NFCZoneData data) {

        if (mNFCType == 3) {
            String mTourName = "caygo_tour";
            if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().hasExtra(UtilStrings.TOUR_NAME)) {
                mTourName = getActivity().getIntent().getExtras().getString(UtilStrings.TOUR_NAME);
            }

            SiteZone mCurrentZone = null;
            CheckTime checkTime = null;
            if (getActivity().getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                mCurrentZone = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
            }

            if (getActivity().getIntent().hasExtra(UtilStrings.SCHEDULED_CHECK_TIME)) {
                checkTime = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.SCHEDULED_CHECK_TIME));
            }

            Intent intent = new Intent(getActivity(), CaygoZoneCheckActivity.class);
            intent.putExtra(UtilStrings.MANAGER_HOME, false);
            intent.putExtra(UtilStrings.TOUR_NAME, mTourName);
            intent.putExtra(UtilStrings.QRCODE, data.getId());

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (mCurrentZone != null) {
                intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(mCurrentZone));
            }

            if (checkTime != null) {
                intent.putExtra(UtilStrings.SCHEDULED_CHECK_TIME, Parcels.wrap(checkTime));
            }

            if (getActivity().getIntent().hasExtra(UtilStrings.CHECK_TIME_ID)) {
                intent.putExtra(UtilStrings.CHECK_TIME_ID, getActivity().getIntent().getExtras().getString(UtilStrings.CHECK_TIME_ID));
            }

            if (getActivity().getIntent().hasExtra(UtilStrings.CHECK_TIME)) {
                intent.putExtra(UtilStrings.CHECK_TIME, getActivity().getIntent().getExtras().getString(UtilStrings.CHECK_TIME));
            }

            if (getActivity().getIntent().hasExtra(UtilStrings.TOUR_CHECK)) {
                intent.putExtra(UtilStrings.TOUR_CHECK, getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK));
            }

            if (getActivity().getIntent().hasExtra(UtilStrings.SITE_TOUR_ID)) {
                intent.putExtra(UtilStrings.SITE_TOUR_ID, getActivity().getIntent().getExtras().getString(UtilStrings.SITE_TOUR_ID));
            }

            if (getActivity().getIntent().hasExtra(UtilStrings.TOUR_LAST_CHECK)) {
                intent.putExtra(UtilStrings.TOUR_LAST_CHECK, getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_LAST_CHECK));
            }


            startActivity(intent);

        } else if (mNFCType == 4) {

            mDBChecker.addComplete(data.getId());
            boolean tourCheck = false;
            if (getActivity().getIntent().hasExtra(UtilStrings.TOUR_CHECK)) {
                tourCheck = getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK);
            }
            if (tourCheck) {
                SiteZone mCurrentZone = null;
                boolean lastTourCheck = false;

                if (getActivity().getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                    mCurrentZone = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                }

                if (getActivity().getIntent().hasExtra(UtilStrings.TOUR_LAST_CHECK)) {
                    lastTourCheck = getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_LAST_CHECK);
                }

                mDBTourCheckHelper.addZoneCheckComplete(mNFCData.getId(), mCurrentZone.getZone_id(), ((NFCMainActivity)getActivity()).connected);
                DBTourCheckModel mCurrentDBTourCheck = mDBTourCheckHelper.getCurrentTourCheck();


                if (lastTourCheck) {
                    mCurrentDBTourCheck.setTimeCompleted(getDateString());
                    mDBTourCheckHelper.saveCurrentTourCheck(mCurrentDBTourCheck);
                }

                int status = 0;
                if (((NFCMainActivity)getActivity()).connected) status =1;
                ((NFCMainActivity)getActivity()).mApplication.setCurrentActivity((NFCMainActivity)getActivity());
                mDBTourCheckManager.updateOrEnterTourChecksWithStatus(mCurrentDBTourCheck, ((NFCMainActivity)getActivity()).mApplication, status, true);
            }

            if (((NFCMainActivity)getActivity()).connected) {
                endCheck();
            } else {
                showEndCheck();
                if (!tourCheck) {
                    insertCheckToDBAndReset(true);
                }
                HazardObserver.getInstance().setCheckEnded(true);
            }
        }
    }
    private String parseNFCData(NdefMessage ndefMessage) {
        //
        try
        {
            byte[] payload = ndefMessage.getRecords()[0].getPayload();

            /*
             * payload[0] contains the "Status Byte Encodings" field, per the
             * NFC Forum "Text Record Type Definition" section 3.2.1.
             *
             * bit7 is the Text Encoding Field.
             *
             * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
             * The text is encoded in UTF16
             *
             * Bit_6 is reserved for future use and must be set to zero.
             *
             * Bits 5 to 0 are the length of the IANA language code.
             */

            //Get the Text Encoding
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";

            //Get the Language Code
            int languageCodeLength = payload[0] & 0077;
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

            //Get the Text
            String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            return text;
        }
        catch(Exception e)
        {
//            throw new RuntimeException("Record Parsing Failure!!");
            e.printStackTrace();
        }
        return null;
    }

    private boolean isNFCTagMatch(NFCZoneData data) {

        //need to check if qrcode scanned match the zone selected
        //if match load next screen,
        // if not need show popup and return to previous page
        //only handle start check at moment

        if (BuildConfig.DEBUG) {
            return true;
        }
        if (data == null) return false;
        if (mNFCType == 3) {
            SiteZone mCurrentZone;
            if (getActivity().getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                mCurrentZone = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                SiteZone tmp = PreferenceHelper.getInstance(getActivity().getApplicationContext()).getSiteZoneById(mCurrentZone.getZone_id());
                if (data.getId() == null) return false;
                return data.getId().equals(tmp.getQrCodeIdA()) || data.getId().equals(tmp.getQrCodeIdB());
            }
        } else if (mNFCType == 4) {
            SiteZone mCurrentZone;
            if (getActivity().getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                mCurrentZone = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                SiteZone tmp = PreferenceHelper.getInstance(getActivity().getApplicationContext()).getSiteZoneById(mCurrentZone.getZone_id());
                String endCode = tmp.getQrCodeIdB();
                if (new CheckPreference(getActivity().getApplicationContext()).getStaredQRCode().equals(tmp.getQrCodeIdB())) {
                    endCode = tmp.getQrCodeIdA();
                }
                if (data.getId() == null) return false;
                return data.getId() .equals(endCode);
            }
        }
        return true;
    }

    private void existScreen() {
        getActivity().finish();
    }

    private void showEmptyView() {
        if (mDefaultView.getVisibility() != View.VISIBLE) mDefaultView.setVisibility(View.VISIBLE);
        if (mDataView.getVisibility() == View.VISIBLE) mDataView.setVisibility(View.GONE);
        if (mCenterView.getVisibility() == View.VISIBLE) mCenterView.setVisibility(View.GONE);

        mLogo.setText(getActivity().getApplicationContext().getResources().getString(R.string.fa_tab_play));

        mDialogTitle.setText(getActivity().getApplicationContext().getResources().getString(R.string.empty_tag));
        mCloseBtn.setText(getActivity().getApplicationContext().getResources().getString(R.string.cancel_button));
    }

    private void showDataView(NFCZoneData data) {
        mDefaultView.setVisibility(View.GONE);
        mCenterView.setVisibility(View.GONE);
        mDataView.setVisibility(View.VISIBLE);
        mCloseBtn.setText(getActivity().getApplicationContext().getResources().getString(R.string.close_button));

        mSiteTitle.setText(data.getSiteName());
        mZoneTitle.setText(data.getZoneName());
        mPoint.setText(data.getPoint());
        mTagId.setText(data.getId());
    }

    private void showErrorView() {
        switch (mNFCType) {
            case 1:
                showReadTagErrorView();
                break;
            case 2:
               showReadTagErrorView();
                break;
            case 3:
                showStartOrEndZoneCheckErrorView(true);
                break;
            case 4:
                showStartOrEndZoneCheckErrorView(false);
                break;
        }
    }

    private void showReadTagErrorView() {
        mLogo.setText(getActivity().getApplicationContext().getResources().getString(R.string.fa_block));
        mDialogTitle.setText(getActivity().getApplicationContext().getResources().getString(R.string.wrong_data_tag));
        mCloseBtn.setText(getActivity().getApplicationContext().getResources().getString(R.string.cancel_button));
        Logger.info(TAG + " show error view");
    }

    private void showStartOrEndZoneCheckErrorView(boolean start) {
        getActivity().finish();
        if (start)
        new Handler().postDelayed(() -> HazardObserver.getInstance().setQrCodeFailed(true), 600);
        else
        new Handler().postDelayed(() -> HazardObserver.getInstance().setCheckEndedFailed(true), 600);
    }

    private void insertCheckToDBAndReset(boolean insert) {
        DBZoneCheckHelper mDBChecker = new DBZoneCheckHelper(mCtx);
        Logger.info("DB -> Save this to DB " + mDBChecker.getCheckForDB());
        if (insert) {
            ((NFCMainActivity)getActivity()).mApplication.setCurrentActivity((NFCMainActivity)getActivity());
            ((NFCMainActivity)getActivity()).mApplication.insertCheck(new DBCheck(mDBChecker.getCheckForDB(),false));
        }
        mDBChecker.resetDBCheck();
    }

    private void endCheck() {

        CaygoSite caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        boolean isTourCheck = getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK, false);
        DBZoneCheckHelper mDBChecker = new DBZoneCheckHelper(mCtx);
        DBTourCheckHelper mTourDBChecker = new DBTourCheckHelper(mCtx);

        Logger.info("DB -> Save this to DB " + mDBChecker.getCheckForDB());
        DBCheck check = new DBCheck(mDBChecker.getCheckForDB(),false);
        DBTourCheck tourCheck = new DBTourCheck(mTourDBChecker.getCurrentTourCheckStr());

        ((NFCMainActivity)getActivity()).mApplication.setCurrentActivity((NFCMainActivity)getActivity());
        if (!isTourCheck) {
            ((NFCMainActivity)getActivity()).mApplication.insertCheck(check);
        }

        RequestCallBack call = new RequestCallBack() {
            @Override
            public void onSucceed() {
                if (!isTourCheck) {
                    if(getActivity() !=null) {
                        if (((NFCMainActivity) getActivity()).mApplication !=null) {
                            ((NFCMainActivity) getActivity()).mApplication.updateSyncedCheck(check);
                        }
                    }
                }

                if (isTourCheck) {
                    tourCheck.setStatus(1);
                    if (getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_LAST_CHECK, false)) {
                        new DBTourCheckHelper(mCtx).saveCurrentTourCheck(null);
                    }
                    DBTourCheckModel mCurrentDBTourCheck = mDBTourCheckHelper.getCurrentTourCheck();
                    mDBTourCheckManager.updateOrEnterTourChecksWithStatus(mCurrentDBTourCheck, ((NFCMainActivity)getActivity()).mApplication, 1, true);

                }
            }

            @Override
            public void onError(@org.jetbrains.annotations.Nullable VolleyError error) {

                if (isTourCheck) {
                    if (getActivity().getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                        SiteZone mCurrentZone = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                        mDBTourCheckHelper.addZoneCheckUnsynced(mCurrentZone.getZone_id());
                    }
                    DBTourCheckModel mCurrentDBTourCheck = mDBTourCheckHelper.getCurrentTourCheck();
                    mDBTourCheckManager.updateOrEnterTourChecksWithStatus(mCurrentDBTourCheck, ((NFCMainActivity)getActivity()).mApplication, 0, true);
                }
            }
        };

        if (isTourCheck) {
            new ZoneCheckHelper(mCtx).zoneTourCheckSync(caygoSite.getSite_id(), PreferenceHelper.getInstance(mCtx).requestToken(), call);
        } else {
            new ZoneCheckHelper(mCtx).zoneCheckSync(caygoSite.getSite_id(), PreferenceHelper.getInstance(mCtx).requestToken(), call);
        }

        mDBChecker.resetDBCheck();
        HazardObserver.getInstance().setCheckEnded(true);
        showEndCheck();
    }


    private void showEndCheck() {

        Intent intent = new Intent(getActivity(), EndCheckActivity.class);
        SiteZone mCurrentZone;
        if (getActivity().getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
            mCurrentZone = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(mCurrentZone));
        }

        boolean isTourCheck = getActivity().getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK, false);
        if (isTourCheck) {
            getActivity().finish();
        } else {
            startActivity(intent);
        }
    }

    private String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    @Override
    public void allTourChecksInDB(@org.jetbrains.annotations.Nullable ArrayList<DBTourCheckModel> mList) {

    }

    @Override
    public void getTourCheckByIdInDB(@org.jetbrains.annotations.Nullable DBTourCheckModel mTour) {

    }
}
