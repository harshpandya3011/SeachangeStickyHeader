package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.seachange.healthandsafty.utils.UtilStrings;

/**
 * Created by kevinsong on 27/09/2017.
 */

public class Logger {

    public static String TAG = "SeaChange";

    public static void info(String message) {
        Log.d(TAG, message);
    }

    public static void infoRiskAssessment(String message) {
        Log.d(TAG, UtilStrings.LOG_RISK_ASSESSMENT + " : " + message);
    }

    public static void infoZoneCheck(String message) {
        Log.d(TAG, UtilStrings.LOG_ZONE_CHECK + " : " + message);
    }

    public static void infoTourCheck(String message) {
        Log.d(TAG, UtilStrings.LOG_TOUR_CHECK + " : " + message);
    }

    public static void infoTourDBCheck(String message) {
        Log.d(TAG, UtilStrings.LOG_TOUR_DB_CHECK + " : " + message);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
