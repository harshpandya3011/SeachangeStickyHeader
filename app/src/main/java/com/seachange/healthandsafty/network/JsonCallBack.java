package com.seachange.healthandsafty.network;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by kevinsong on 20/09/2017.
 */

public interface JsonCallBack {
    void callbackJSONObject(JSONObject result);
    void callbackJsonArray(JSONArray result);
    void callbackErrorCalled(VolleyError result);
}

