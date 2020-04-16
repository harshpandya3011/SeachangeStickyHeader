package com.seachange.healthandsafty.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.seachange.healthandsafty.BuildConfig;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.db.DBCheck;
import com.seachange.healthandsafty.db.DBTourCheck;
import com.seachange.healthandsafty.fragment.MessageDialogFragment;
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
import com.seachange.healthandsafty.model.SCQRCode;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.qrcode.BarcodeGraphic;
import com.seachange.healthandsafty.qrcode.BarcodeGraphicTracker;
import com.seachange.healthandsafty.qrcode.BarcodeTrackerFactory;
import com.seachange.healthandsafty.qrcode.ScannerOverlay;
import com.seachange.healthandsafty.qrcode.ui.camera.CameraSource;
import com.seachange.healthandsafty.qrcode.ui.camera.CameraSourcePreview;
import com.seachange.healthandsafty.qrcode.ui.camera.GraphicOverlay;
import com.seachange.healthandsafty.utils.Util;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class QRCodeCaptureActivity extends BaseActivity implements BarcodeGraphicTracker.BarcodeUpdateListener, DBTourCheckManagerView, MessageDialogFragment.MessageDialogListener {
    private static final String TAG = "Barcode-reader";

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private ScannerOverlay mScannOverLay;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private int scanLevel;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private SCQRCode qrCode;
    private boolean qrCodeEnabled = true;
    private DBZoneCheckHelper mDBChecker;
    private DBTourCheckManager mDBTourCheckManager;
    private DBTourCheckHelper mDBTourCheckHelper;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.content_qrcode_capture);

        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.graphicOverlay);
        mScannOverLay = findViewById(R.id.scanOverlay);

        HazardObserver.getInstance().setQrCodeFailed(false);
        mDBChecker = new DBZoneCheckHelper(mCtx);

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, true);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
        scanLevel = getIntent().getIntExtra(UtilStrings.SCAN_LEVEL, 0);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Scan QRCode");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        mGraphicOverlay.setVisibility(View.INVISIBLE);
        new PreferenceHelper(mCtx).saveShowTourQRFailed(false);
        mDBTourCheckManager = new DBTourCheckManager(this, this);
        mDBTourCheckHelper = new DBTourCheckHelper(mCtx);
    }



    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = view -> ActivityCompat.requestPermissions(thisActivity, permissions,
            RC_HANDLE_CAMERA_PERM);

        findViewById(R.id.topLayout).setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok, listener)
            .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);
        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }


    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);
        barcodeDetector.setProcessor(
            new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1600, 1024)
            .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        builder = builder.setFocusMode(
            autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);

        mCameraSource = builder
            .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
            .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
        qrCodeEnabled = true;
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, true);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
            " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = (dialog, id) -> finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SeaChange")
            .setMessage(R.string.no_camera_permission)
            .setPositiveButton(R.string.ok, listener)
            .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        }

        if (best != null) {
            Intent data = new Intent();
            data.putExtra(BarcodeObject, best);
            setResult(CommonStatusCodes.SUCCESS, data);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void allTourChecksInDB(@Nullable ArrayList<DBTourCheckModel> mList) {

    }

    @Override
    public void getTourCheckByIdInDB(@Nullable DBTourCheckModel mTour) {

    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {


        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }


        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    @Override
    public void onBarcodeDetected(final Barcode barcode) {
        if (!qrCodeEnabled) return;
        if (qrCodeEnabled) {
            qrCodeEnabled = false;
        }
        if (qrCode != null) return;
        runOnUiThread(new Runnable() {
            public void run() {
                mPreview.stop();
                mScannOverLay.setOnPause(true);
                parseQRCode(barcode.rawValue);
                new Util().playBeep(mCtx);
                if (isQrCodeMatch()) {
                    validateDataFromQRCode();
                    if(scanLevel ==1) {
                        new CheckPreference(mCtx).saveStartedQRCode(qrCode.getPointQRId());
                    }
                } else {
                    if (scanLevel == 1) {
                        HazardObserver.getInstance().setQrCodeFailed(true);
                    } else if (scanLevel == 2) {
                        HazardObserver.getInstance().setCheckEndedFailed(true);
                    }
                    qrCodeValidateFailed();
                }
            }
        });
    }

    private boolean isQrCodeMatch() {

        //need to check if qrcode scanned match the zone selected
        //if match load next screen,
        // if not need show popup and return to previous page
        //only handle start check at moment

        if (BuildConfig.DEBUG) {
            return true;
        }
        if (qrCode == null) return false;
        if (scanLevel == 1) {
            SiteZone mCurrentZone;
            if (getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                mCurrentZone = Parcels.unwrap(getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                SiteZone tmp = PreferenceHelper.getInstance(mCtx).getSiteZoneById(mCurrentZone.getZone_id());
                if (qrCode.getPointQRId() == null) return false;
                return qrCode.getPointQRId().equals(tmp.getQrCodeIdA()) || qrCode.getPointQRId().equals(tmp.getQrCodeIdB());
            }
        } else if (scanLevel == 2) {
            SiteZone mCurrentZone;
            if (getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                mCurrentZone = Parcels.unwrap(getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                SiteZone tmp = PreferenceHelper.getInstance(mCtx).getSiteZoneById(mCurrentZone.getZone_id());
                String endCode = tmp.getQrCodeIdB();
                if (new CheckPreference(mCtx).getStaredQRCode().equals(tmp.getQrCodeIdB())) {
                    endCode = tmp.getQrCodeIdA();
                }
                if (qrCode.getPointQRId() == null) return false;
                return qrCode.getPointQRId().equals(endCode);
            }
        }
        return true;
    }

    private void qrCodeValidateFailed() {

        if (getIntent().hasExtra(UtilStrings.FROM_TOUR)) {
            if (getIntent().getExtras().getBoolean(UtilStrings.FROM_TOUR)) {
                PreferenceHelper.getInstance(mCtx).saveShowTourQRFailed(true);
            }
        }
        this.finish();
    }

    private void parseQRCode(String rawValue) {

        qrCode = new SCQRCode();
        String[] items = rawValue.split("/");
        if (rawValue.contains("seachange") && rawValue.contains("caygo")) {
            qrCode.setPoint(items[items.length - 2]);
            qrCode.setPointQRId(items[items.length - 1]);
            Logger.info("QRCODE point: " + qrCode.getPoint() + " QRCODE: " + qrCode.getPointQRId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMessageDialog(String name, String email) {

        DialogFragment fragment = MessageDialogFragment.Companion.newInstance(name, email, this);
        fragment.show(getSupportFragmentManager(), "scan_results");
    }

    @Override
    public void onDialogPositiveClick(@NotNull DialogFragment dialog) {
        validateDataFromQRCode();
    }

    private void validateDataFromQRCode() {

        if (scanLevel == 1) {
            String mTourName = "caygo_tour";
            if (getIntent().getExtras() != null && getIntent().hasExtra(UtilStrings.TOUR_NAME)) {
                mTourName = getIntent().getExtras().getString(UtilStrings.TOUR_NAME);
            }

            SiteZone mCurrentZone = null;
            CheckTime checkTime = null;
            if (getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                mCurrentZone = Parcels.unwrap(getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
            }

            if (getIntent().hasExtra(UtilStrings.SCHEDULED_CHECK_TIME)) {
                checkTime = Parcels.unwrap(getIntent().getParcelableExtra(UtilStrings.SCHEDULED_CHECK_TIME));
            }

            Intent intent = new Intent(this, CaygoZoneCheckActivity.class);
            intent.putExtra(UtilStrings.MANAGER_HOME, false);
            intent.putExtra(UtilStrings.TOUR_NAME, mTourName);
            intent.putExtra(UtilStrings.QRCODE, qrCode.getPointQRId());

            if (mCurrentZone != null) {
                intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(mCurrentZone));
            }

            if (checkTime != null) {
                intent.putExtra(UtilStrings.SCHEDULED_CHECK_TIME, Parcels.wrap(checkTime));
            }

            if (getIntent().hasExtra(UtilStrings.CHECK_TIME_ID)) {
                intent.putExtra(UtilStrings.CHECK_TIME_ID, getIntent().getExtras().getString(UtilStrings.CHECK_TIME_ID));
            }

            if (getIntent().hasExtra(UtilStrings.CHECK_TIME)) {
                intent.putExtra(UtilStrings.CHECK_TIME, getIntent().getExtras().getString(UtilStrings.CHECK_TIME));
            }

            if (getIntent().hasExtra(UtilStrings.TOUR_CHECK)) {
                intent.putExtra(UtilStrings.TOUR_CHECK, getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK));
            }

            if (getIntent().hasExtra(UtilStrings.SITE_TOUR_ID)) {
                intent.putExtra(UtilStrings.SITE_TOUR_ID, getIntent().getExtras().getString(UtilStrings.SITE_TOUR_ID));
            }

            if (getIntent().hasExtra(UtilStrings.TOUR_LAST_CHECK)) {
                intent.putExtra(UtilStrings.TOUR_LAST_CHECK, getIntent().getExtras().getBoolean(UtilStrings.TOUR_LAST_CHECK));
            }

            startActivity(intent);

        } else if (scanLevel == 2) {

            mDBChecker.addComplete(qrCode.getPointQRId());
            Boolean tourCheck = false;
            if (getIntent().hasExtra(UtilStrings.TOUR_CHECK)) {
                tourCheck = getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK);
            }
            if (tourCheck) {
                SiteZone mCurrentZone = null;
                boolean lastTourCheck = false;

                if (getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                    mCurrentZone = Parcels.unwrap(getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                }

                if (getIntent().hasExtra(UtilStrings.TOUR_LAST_CHECK)) {
                    lastTourCheck = getIntent().getExtras().getBoolean(UtilStrings.TOUR_LAST_CHECK);
                }

                mDBTourCheckHelper.addZoneCheckComplete(qrCode.getPointQRId(), mCurrentZone.getZone_id(), connected);
                DBTourCheckModel mCurrentDBTourCheck = mDBTourCheckHelper.getCurrentTourCheck();


                if (lastTourCheck) {
                    mCurrentDBTourCheck.setTimeCompleted(getDateString());
                    mDBTourCheckHelper.saveCurrentTourCheck(mCurrentDBTourCheck);
                }

                int status = 0;
                if (connected) status =1;
                mDBTourCheckManager.updateOrEnterTourChecksWithStatus(mCurrentDBTourCheck, mApplication, status, true);
            }

            if (connected) {
                endCheck();


//                test for not syncing checks
//                showEndCheck();
//                if (!tourCheck) {
//                    insertCheckToDBAndReset(true);
//                }
//                HazardObserver.getInstance().setCheckEnded(true);

            } else {

                //
                //no internet connection case
                //insert current check to database
                //reset current check
                //set observer current check ended
                //show end check screen
                //
                showEndCheck();
                if (!tourCheck) {
                    insertCheckToDBAndReset(true);
                }
                HazardObserver.getInstance().setCheckEnded(true);
            }
        }
    }

    private void insertCheckToDBAndReset(boolean insert) {
        DBZoneCheckHelper mDBChecker = new DBZoneCheckHelper(mCtx);
        Logger.info("DB -> Save this to DB " + mDBChecker.getCheckForDB());
        if (insert) {
            mApplication.insertCheck(new DBCheck(mDBChecker.getCheckForDB(),false));
        }
        mDBChecker.resetDBCheck();
    }

    private void endCheck() {

        CaygoSite caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        Boolean isTourCheck = getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK, false);
        DBZoneCheckHelper mDBChecker = new DBZoneCheckHelper(mCtx);
        DBTourCheckHelper mTourDBChecker = new DBTourCheckHelper(mCtx);

        Logger.info("DB -> Save this to DB " + mDBChecker.getCheckForDB());
        DBCheck check = new DBCheck(mDBChecker.getCheckForDB(),false);
        DBTourCheck tourCheck = new DBTourCheck(mTourDBChecker.getCurrentTourCheckStr());
        if (!isTourCheck) {
            mApplication.insertCheck(check);
        }


        RequestCallBack call = new RequestCallBack() {
            @Override
            public void onSucceed() {
                if (!isTourCheck) {
                    mApplication.updateSyncedCheck(check);
                }

                if (isTourCheck) {
                    tourCheck.setStatus(1);
                    if (getIntent().getExtras().getBoolean(UtilStrings.TOUR_LAST_CHECK, false)) {
                        new DBTourCheckHelper(mCtx).saveCurrentTourCheck(null);
                    }
                    DBTourCheckModel mCurrentDBTourCheck = mDBTourCheckHelper.getCurrentTourCheck();
                    mDBTourCheckManager.updateOrEnterTourChecksWithStatus(mCurrentDBTourCheck, mApplication, 1, true);

                }
            }

            @Override
            public void onError(@Nullable VolleyError error) {

                if (isTourCheck) {
                    if (getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
                        SiteZone mCurrentZone = Parcels.unwrap(getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
                        mDBTourCheckHelper.addZoneCheckUnsynced(mCurrentZone.getZone_id());
                    }
                    DBTourCheckModel mCurrentDBTourCheck = mDBTourCheckHelper.getCurrentTourCheck();
                    mDBTourCheckManager.updateOrEnterTourChecksWithStatus(mCurrentDBTourCheck, mApplication, 0, true);
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

        Intent intent = new Intent(this, EndCheckActivity.class);
        SiteZone mCurrentZone;
        if (getIntent().hasExtra(UtilStrings.SITE_ZONE)) {
            mCurrentZone = Parcels.unwrap(getIntent().getParcelableExtra(UtilStrings.SITE_ZONE));
            intent.putExtra(UtilStrings.SITE_ZONE, Parcels.wrap(mCurrentZone));
            String mTourName = "caygo_tour";
            if (getIntent().getExtras() != null && getIntent().hasExtra(UtilStrings.TOUR_NAME)) {
                mTourName = getIntent().getExtras().getString(UtilStrings.TOUR_NAME);
            }
//            ManagerTour managerTour = new ManagerTour(mCtx, mTourName);
//            mCurrentZone.setChecked(true);
//            managerTour.updateZones(mCurrentZone);
        }

        Boolean isTourCheck = getIntent().getExtras().getBoolean(UtilStrings.TOUR_CHECK, false);
        if (isTourCheck) {
            this.finish();
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onDialogNegativeClick(@NotNull DialogFragment dialog) {
        startCameraSource();
        mScannOverLay.setOnPause(false);
    }

    private String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }
}

