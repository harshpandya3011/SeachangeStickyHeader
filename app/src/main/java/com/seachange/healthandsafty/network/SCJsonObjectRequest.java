package com.seachange.healthandsafty.network;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.utils.UtilStrings;

/**
 * Created by kevinsong on 28/09/2017.
 */

public class SCJsonObjectRequest extends JsonRequest<JSONObject> {

    public SCJsonObjectRequest(int method, String url, JSONObject jsonRequest,
                               Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }

    public SCJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {

            int statusCode = response.statusCode;
            JSONObject jsonResponse = new JSONObject();
            if (response.headers.containsKey(UtilStrings.ETag)) {
                String ETag = response.headers.get(UtilStrings.ETag).replace("\"", "");
                if (ETag != null) {
                    jsonResponse.put(UtilStrings.ETag, ETag);
                    Logger.info("ETag : " + ETag);
                }
            }
            jsonResponse.put(UtilStrings.STATUS_CODE, response.statusCode);
            if (statusCode == 200){
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                Object obj = new JSONTokener(jsonString).nextValue();
                if (obj instanceof JSONObject) {
                    jsonResponse.put(UtilStrings.RESPONSE, new JSONObject(jsonString));
                } else if(obj instanceof JSONArray) {
                    jsonResponse.put(UtilStrings.RESPONSE, new JSONArray(jsonString));
                }else {
                    jsonResponse.put(UtilStrings.RESPONSE, jsonString);
                }
            }

            return Response.success(jsonResponse,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
