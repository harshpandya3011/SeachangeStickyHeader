package com.seachange.healthandsafty.nfc.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Button;

import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.activity.BaseActivity;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.nfc.helper.NFCSetUpHelper;
import com.seachange.healthandsafty.nfc.model.TextRecord;
import com.seachange.healthandsafty.nfc.view.NFCListener;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.parceler.Parcels;

//type = 1, read
//type = 2. write,
//type = 3, read to start zone check
//type = 4, read to end zone check

public class NFCMainActivity extends BaseActivity implements NFCListener {

    public static final String TAG = NFCMainActivity.class.getSimpleName();

    private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;

    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;
    private int mNFCType = 0;
    private SiteZone mZone;
    private String mPoint;
    private NFCSetUpHelper zoneDataHelper;

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcmain);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        initViews();
        initNFC();
        disableNFC = false;
    }

    private void initViews() {
        Button mBtWrite = findViewById(R.id.btn_write);
        Button mBtRead = findViewById(R.id.btn_read);
        mBtWrite.setOnClickListener(view -> showWriteFragment());
        mBtRead.setOnClickListener(view -> showReadFragment());
    }

    private void initNFC() {
        zoneDataHelper = new NFCSetUpHelper(mCtx);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNFCType = getIntent().getExtras().getInt(UtilStrings.NFC_TYPE);

        switch (mNFCType) {
            case 1:
                showReadFragment();
                break;
            case 2:
                mPoint = getIntent().getExtras().getString(UtilStrings.NFC_POINT);
                mZone = Parcels.unwrap(getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                showWriteFragment();
                break;
            case 3:
                showReadFragment();
                break;
            case 4:
                showReadFragment();
                break;
        }
    }

    private void showWriteFragment() {
        isWrite = true;
        mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
        if (mNfcWriteFragment == null) {
            mNfcWriteFragment = NFCWriteFragment.newInstance();
        }
        mNfcWriteFragment.initData(mZone, mPoint);
        mNfcWriteFragment.show(getFragmentManager(), NFCWriteFragment.TAG);
    }

    private void showReadFragment() {
        mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
        if (mNfcReadFragment == null) {
            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.initType(mNFCType);
        mNfcReadFragment.show(getFragmentManager(), NFCReadFragment.TAG);
    }

    public boolean getConnectionStatus() {
        return connected;
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
        isWrite = false;
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    private String extractText(NdefMessage ndefMessage) {
        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord record : records) {
            if (NdefRecord.TNF_WELL_KNOWN == record.getTnf()) {
                TextRecord textRecord = new TextRecord.Builder(record).build();
                return textRecord.getText();
            }
        }
        return null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage tmpMsg = null;
        if (parcelableArrayExtra != null) {
            tmpMsg = (NdefMessage) parcelableArrayExtra[0];
            String text = this.extractText(tmpMsg);
            Logger.info("nfc intent message: " + text);
        } else {
            Logger.info("nfc intent message: empty" );
        }

        if (tag != null) {
            Ndef ndef = Ndef.get(tag);
            if (isDialogDisplayed) {
                if (isWrite) {
                    String messageToWrite = zoneDataHelper.createNFCPointJsonObject(mZone, mPoint);
                    mNfcWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    mNfcWriteFragment.onNfcDetected(ndef, messageToWrite, intent);
                } else {
                    mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                    mNfcReadFragment.onNfcDetected(ndef, tmpMsg);
                }
            }
        }
    }
}
