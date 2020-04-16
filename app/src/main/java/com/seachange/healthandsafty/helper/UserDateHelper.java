package com.seachange.healthandsafty.helper;


import android.content.Context;
import android.content.SharedPreferences;

import com.seachange.healthandsafty.utils.UtilStrings;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDateHelper {

    private Context mCtx;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public UserDateHelper(Context c) {
        mCtx = c;
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public boolean getUserLoggedIn() {
        return prefs.getBoolean(UtilStrings.PREFERENCES_USER_LOGGEDIN, false);
    }

    public void setUserLoggedIn(boolean login) {
        editor.putBoolean(UtilStrings.PREFERENCES_USER_LOGGEDIN, login).apply();
    }

    public String getUserPassCodeId() {
        return prefs.getString(UtilStrings.PREFERENCES_USER_PASSCODE, null);
    }

    public void setUserPassCodeId(String passId) {
        editor.putString(UtilStrings.PREFERENCES_USER_PASSCODE, passId).apply();
    }

    public String getUserHashedCode() {
        return prefs.getString(UtilStrings.PREFERENCES_USER_HASHEDCODE, null);
    }

    public void setUserHashedCode(String hashed) {
        editor.putString(UtilStrings.PREFERENCES_USER_HASHEDCODE, hashed).apply();
    }

    public void saveUserLoggedInDetail(String passId, String hashed) {
        setUserLoggedIn(true);
        setUserPassCodeId(passId);
        setUserHashedCode(hashed);
    }

    public void resetUserLoggedInDetails() {
        setUserLoggedIn(false);
        setUserPassCodeId(null);
        setUserHashedCode(null);
    }

    public String getUserJsonString() {
        try {
            JSONObject tmp = new JSONObject();
            tmp.put("id", getUserPassCodeId());
            tmp.put("hashedPasscode", getUserHashedCode());
            return tmp.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserInParameters() {
        return "passcodeuserId=" + getUserPassCodeId()+ "&poasscodeUserHashedPasscode=" + getUserHashedCode();
    }
}
