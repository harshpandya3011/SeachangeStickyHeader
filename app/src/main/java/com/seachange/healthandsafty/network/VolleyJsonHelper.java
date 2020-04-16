package com.seachange.healthandsafty.network;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import com.seachange.healthandsafty.application.AppController;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.helper.UserDateHelper;
import com.seachange.healthandsafty.view.RefreshView;

/**
 * Created by kevinsong on 20/09/2017.
 */

public class VolleyJsonHelper {

    private String request_url;
    private String request_title;
    private JsonCallBack jsonCallBack;
    private Context mCtx;
    private UserDateHelper userDateHelper;
    private boolean withUserData = true, refreshTokenRequest = false;
    private Integer timeOut = 0;

    public VolleyJsonHelper(Context c) {
        mCtx = c;
        userDateHelper = new UserDateHelper(mCtx);
    }

    public VolleyJsonHelper(Context c, boolean userData) {
        mCtx = c;
        userDateHelper = new UserDateHelper(mCtx);
        withUserData = userData;
    }

    public VolleyJsonHelper(Context c, boolean userData, int mTimeOut) {
        mCtx = c;
        userDateHelper = new UserDateHelper(mCtx);
        withUserData = userData;
        timeOut = mTimeOut;
    }

    public VolleyJsonHelper(String url, String title, JsonCallBack jCallBack, Context c) {
        this.request_url = url;
        this.request_title = title;
        this.jsonCallBack = jCallBack;
        mCtx = c;
        userDateHelper = new UserDateHelper(mCtx);
    }

    public VolleyJsonHelper(String url, String title, JsonCallBack jCallBack, Context c, int mTimeOut) {
        this.request_url = url;
        this.request_title = title;
        this.jsonCallBack = jCallBack;
        mCtx = c;
        userDateHelper = new UserDateHelper(mCtx);
        timeOut = mTimeOut;
    }

    public void getJsonObjectFromVolleyHelper(final String hashKey, final String eTag) {

        if (userDateHelper.getUserLoggedIn()) {
            if (request_url.contains("?") && request_url.contains("=")) {
                request_url = request_url + "&" + userDateHelper.getUserInParameters();
            }
            else {
                request_url = request_url + "?" + userDateHelper.getUserInParameters();
            }
        }

        SCJsonObjectRequest jsObjRequest = new SCJsonObjectRequest(Request.Method.GET, request_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                jsonCallBack.callbackJSONObject(response);
            }

        }, error -> {

            if (error!=null) {
                if (error.networkResponse!=null) {
                    if (error.networkResponse.statusCode == 401){
                        if (!refreshTokenRequest && PreferenceHelper.getInstance(mCtx).getRefreshToken() != null) {
                                getRefreshToken(hashKey, eTag, error);
                                refreshTokenRequest = true;
                        }else {
                            jsonCallBack.callbackErrorCalled(error);
                        }
                    }else {
                        jsonCallBack.callbackErrorCalled(error);
                    }
                }else {
                    jsonCallBack.callbackErrorCalled(error);
                }
            }else {
                jsonCallBack.callbackErrorCalled(error);
            }
            Logger.info(request_title + "get result for as json object error" + error.toString());


        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", hashKey);
                if (eTag != null) {
                    params.put("If-None-Match", eTag);
                }
                return params;
            }
        };
        jsObjRequest.setShouldCache(false);
        if (timeOut>0) {
            int socketTimeout = timeOut;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsObjRequest.setRetryPolicy(policy);
        }

        AppController.getInstance().addToRequestQueue(jsObjRequest, request_title);
    }

    private void getRefreshToken(final String hashKey, final String eTag, final VolleyError initError) {
        Logger.info("fresh token background request sent");
        RefreshView refreshView = new RefreshView() {
            @Override
            public void onRefreshingInProgress() {

            }

            @Override
            public void tokenRefreshSuccessfully(@Nullable JSONObject result) {
                getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(mCtx).requestToken(), eTag);
                Logger.info("fresh token background request sent good");
            }

            @Override
            public void tokenRefreshWithError(@Nullable VolleyError error) {
                jsonCallBack.callbackErrorCalled(initError);
                Logger.info("fresh token background request sent bad");
            }
        };
        ((SCApplication)mCtx).refreshTokenOnFailedRequest(refreshView);
    }


    public void getJsonArrayFromVolleyHelper(final String hash) {

        SCJsonArrayRequest req = new SCJsonArrayRequest(request_url, response -> jsonCallBack.callbackJsonArray(response), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    Logger.info("HTTP Status Code: 401 Unauthorized");
                }
                jsonCallBack.callbackErrorCalled(error);
                Log.d("Result", "get result for as json array error" + error.toString());
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", hash);
                params.put("Device", "Android");
                return params;
            }

        };

        req.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(req, request_title);

    }

    public void postRequestWithParams(String posUrl, final Map<String, String> params, String title, final String hash, final VolleyPostRequestListener listener) {

        StringRequest sr = new StringRequest(Request.Method.POST, posUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                listener.requestCompleted(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.requestEndedWithError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                if (withUserData) {
                    if (userDateHelper.getUserLoggedIn()) {
                        params.put("passcodeUser", userDateHelper.getUserJsonString());
                    }
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", hash);
                return params;
            }
        };

        sr.setShouldCache(false);
        if (timeOut>0) {
            int socketTimeout = timeOut;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            sr.setRetryPolicy(policy);
        }
        AppController.getInstance().addToRequestQueue(sr, title);
    }

    public void postRequestWithRaw(String posUrl, final String raw, String title, final String hash, final VolleyPostRequestListener listener) {

        StringRequest sr = new StringRequest(Request.Method.POST, posUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                listener.requestCompleted(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.requestEndedWithError(error);
            }
        }) {

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return raw.getBytes();
            };

            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", hash);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(sr, title);
    }

    public void putRequestWithParams(String posUrl, final Map<String, String> params, String title, final String hash, final VolleyPostRequestListener listener) {

        StringRequest sr = new StringRequest(Request.Method.PUT, posUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                listener.requestCompleted(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.requestEndedWithError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (userDateHelper.getUserLoggedIn()) {
                    params.put("passcodeUser", userDateHelper.getUserJsonString());
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", hash);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(sr, title);
    }

    public void putRequestWithRaw(String posUrl, final String raw, String title, final String hash, final VolleyPostRequestListener listener) {

        StringRequest sr = new StringRequest(Request.Method.PUT, posUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                listener.requestCompleted(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.requestEndedWithError(error);
            }
        }) {

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return raw.getBytes();
            };

            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", hash);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(sr, title);
    }

    public void deleteRequestWithRaw(String posUrl, String title, final String hash, final VolleyPostRequestListener listener) {

        StringRequest sr = new StringRequest(Request.Method.DELETE, posUrl,

                listener::requestCompleted,
                listener::requestEndedWithError) {

            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", hash);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(sr, title);
    }


    public String getRequest_url() {
        return request_url;
    }

    public void setRequest_url(String request_url) {
        this.request_url = request_url;
    }

    public String getRequest_title() {
        return request_title;
    }

    public void setRequest_title(String request_title) {
        this.request_title = request_title;
    }
}
