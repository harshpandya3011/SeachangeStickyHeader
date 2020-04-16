package com.seachange.healthandsafty.nfc.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.nfc.helper.NFCHelper;
import com.seachange.healthandsafty.nfc.helper.NFCSetUpHelper;
import com.seachange.healthandsafty.nfc.helper.NFCZoneSetUp;
import com.seachange.healthandsafty.nfc.model.NFCZoneData;
import com.seachange.healthandsafty.nfc.view.DrawDottedCurve;
import com.seachange.healthandsafty.nfc.view.NFCListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import kotlin.Pair;


public class NFCWriteFragment extends DialogFragment {

    public static final String TAG = NFCWriteFragment.class.getSimpleName();

    public static NFCWriteFragment newInstance() {
        return new NFCWriteFragment();
    }

    private TextView mTvMessage, mTvPoint, mLogo;
    private NFCListener mListener;
    private String mPoint;
    private SiteZone mZone;
    private Button mCloseBtn;
    private NFCSetUpHelper setUpHelper;
    private DrawDottedCurve mCurve;
    private RelativeLayout mCenterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nfcwrite, container, false);
        initViews(view);
        initPointTv();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpHelper = new NFCSetUpHelper(getActivity().getApplication());
        mCloseBtn.setOnClickListener(view1 -> exitScreen());
        initCurveView();
    }

    private void initViews(View view) {
        mTvMessage = view.findViewById(R.id.tv_message);
        mTvPoint = view.findViewById(R.id.nfc_dialog_write_title);
        mLogo = view.findViewById(R.id.nfc_write_logo);
        mCloseBtn = view.findViewById(R.id.nfc_tag_write_close);
        mCurve = view.findViewById(R.id.curve_view);
        mCenterView = view.findViewById(R.id.nfc_write_view_anim_rel);
    }

    private void initPointTv() {
        mLogo.setTypeface(SCApplication.FontMaterial());
        String tmp = "Tap NFC Tag Now to Setup Point ";
        if (mPoint.equals("A") && (NFCZoneSetUp.getInstance().isPointASetup() || mZone.isTagSetup())) {
            tmp = "Tap New NFC Tag Now to Setup Point ";
        }

        if (mPoint.equals("B") && (NFCZoneSetUp.getInstance().isPointBSetup() || mZone.isTagSetup())) {
            tmp = "Tap New NFC Tag Now to Setup Point ";
        }
        mTvPoint.setText(String.format("%s%s", tmp, mPoint));
    }

    private void initCurveView() {
        mCurve.init(getActivity().getApplicationContext(), 20, 65, 208, 50);
    }

    public void initData(SiteZone zone, String point) {
        this.mZone = zone;
        this.mPoint = point;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (NFCMainActivity) activity;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (NFCMainActivity) context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null)
            mListener.onDialogDismissed();
        if (mCurve != null)
            mCurve.stopTimer();
    }

    public void onNfcDetected(Ndef ndef, String messageToWrite, Intent intent) {
        writeToNfcData(messageToWrite, intent);
//        writeToNfc(ndef, messageToWrite, intent);
    }

    private void writeToNfc(Ndef ndef, String message, Intent intent) {
        mTvMessage.setVisibility(View.GONE);
        if (ndef != null) {
            try {
                ndef.connect();
                String pathPrefix = "seachange.com:caygo";
                NdefRecord mimeRecord = createTextRecord(message, true, pathPrefix);//new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, pathPrefix.getBytes(Charset.forName("US-ASCII")), new byte[0] , message.getBytes(Charset.forName("UTF-8")));
                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
//                if(ndef.canMakeReadOnly()){
//                    ndef.makeReadOnly();
//                    Log.e("Read Only", "Read Only");
//                }

                if (mPoint.equals("A")) {
                    NFCZoneSetUp.getInstance().setPointASetup(true);
                    NFCZoneData data = setUpHelper.getDataFromNFCTagURL(message);
                    setUpHelper.setCurrentZonePoint(mZone, true, data);
                } else {
                    NFCZoneSetUp.getInstance().setPointBSetup(true);
                    NFCZoneData data = setUpHelper.getDataFromNFCTagURL(message);
                    setUpHelper.setCurrentZonePoint(mZone, false, data);
                }
                NFCZoneSetUp.getInstance().setPoint(mPoint);
                NFCZoneSetUp.getInstance().setTagSet(true);
                ndef.close();
                //Write Successful
                setUpHelper.setNFCSetUpChanged(true);

                exitScreen();
            } catch (Exception e) {
                writeWithError(getActivity().getApplicationContext().getResources().getString(R.string.message_tap_tag));
                Logger.info(getString(R.string.message_write_error) + "format");
            } finally {
                Logger.info(getString(R.string.message_write_error) + "final");
            }
        } else {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NdefFormatable nForm = NdefFormatable.get(tag);
            if (nForm != null) {
                try {
                    nForm.connect();
                    String pathPrefix = "seachange.com:caygo";
                    NdefRecord mimeRecord = createTextRecord(message, true, pathPrefix);//new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, pathPrefix.getBytes(Charset.forName("US-ASCII")), new byte[0] , message.getBytes(Charset.forName("UTF-8")));
                    nForm.format(new NdefMessage(mimeRecord));
                    nForm.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
            }
            writeWithError(getActivity().getApplicationContext().getResources().getString(R.string.message_tap_tag));
        }
    }

    private void writeToNfcData(String message, Intent intent) {
        mTvMessage.setVisibility(View.GONE);
        Pair<Boolean, String> write = new NFCHelper().writeNFCMessage(message, intent, getActivity());
        if (write.getFirst()) {
            Logger.info("nfc write good, this is good");
            dataWriteSuccessfully(message);
        } else {
            Logger.info("nfc write good, this is shit");
            writeWithError(write.getSecond());
        }
    }

    private void dataWriteSuccessfully(String message) {
        if (mPoint.equals("A")) {
            NFCZoneSetUp.getInstance().setPointASetup(true);
            NFCZoneData data = setUpHelper.getDataFromNFCTagURL(message);
            if (data != null) setUpHelper.setCurrentZonePoint(mZone, true, data);
        } else {
            NFCZoneSetUp.getInstance().setPointBSetup(true);
            NFCZoneData data = setUpHelper.getDataFromNFCTagURL(message);
            if (data != null) setUpHelper.setCurrentZonePoint(mZone, false, data);
        }
        NFCZoneSetUp.getInstance().setPoint(mPoint);
        NFCZoneSetUp.getInstance().setTagSet(true);
        setUpHelper.setNFCSetUpChanged(true);
        exitScreen();
    }

    private void writeWithError(String message) {
        //set Error MSg in mTvMessage
        mTvMessage.setVisibility(View.VISIBLE);
        mLogo.setText(getActivity().getApplicationContext().getResources().getString(R.string.fa_block));
        if (message.equalsIgnoreCase(getString(R.string.tag_connection_lost_message))) {
            mTvPoint.setText(getActivity().getApplicationContext().getResources().getString(R.string.tag_connection_lost_title));
            mTvMessage.setText(getActivity().getApplicationContext().getResources().getString(R.string.tag_connection_lost_content));
            mCloseBtn.setText(R.string.try_again_btn);
        } else {
            mTvPoint.setText(getActivity().getApplicationContext().getResources().getString(R.string.write_tag_error));
            mTvMessage.setText(getActivity().getApplicationContext().getResources().getString(R.string.message_tap_tag));
            mCloseBtn.setText(R.string.close_btn);
        }

        mCenterView.setVisibility(View.GONE);
    }

    private void exitScreen() {
        getActivity().finish();
    }

    public NdefRecord createTextRecord(String payload, boolean encodeInUtf8, String prefix) {
        byte[] langBytes = Locale.getDefault().getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);

//        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
//                prefix.getBytes(Charset.forName("US-ASCII")), new byte[0], data);
        return record;
    }

    public NdefRecord createTextRecord(String language, String text) {
        byte[] languageBytes;
        byte[] textBytes;
        try {
            languageBytes = language.getBytes("US-ASCII");
            textBytes = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }

        byte[] recordPayload = new byte[1 + (languageBytes.length & 0x03F) + textBytes.length];

        recordPayload[0] = (byte) (languageBytes.length & 0x03F);
        System.arraycopy(languageBytes, 0, recordPayload, 1, languageBytes.length & 0x03F);
        System.arraycopy(textBytes, 0, recordPayload, 1 + (languageBytes.length & 0x03F), textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, null, recordPayload);
    }
}
