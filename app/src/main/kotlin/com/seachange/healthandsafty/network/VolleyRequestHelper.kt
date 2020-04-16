package com.seachange.healthandsafty.network

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.application.AppController
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.utils.UtilStrings
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class VolleyRequestHelper (

        private var request_url: String,
        private var mCtx: Context,
        private var callBack: JsonCallBack){

    fun getJsonObjectFromServer(json: JSONObject) {

        val jsObjRequest = object : JsonObjectRequest(Request.Method.GET, request_url, json, Response.Listener { response ->
            callBack.callbackJSONObject(response)

        }, Response.ErrorListener { error ->
            callBack.callbackErrorCalled(error)
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Device"] = "Android"
                return params
            }
        }

        VolleyNetworkHelper.getInstance(mCtx).addToRequestQueue(jsObjRequest)
    }

    fun getJsonArrayFromVolleyHelper(hash: String) {

        val arrayReq = object : JsonArrayRequest(request_url, Response.Listener { response ->
            callBack.callbackJsonArray(response)
        }, Response.ErrorListener { error ->
            val networkResponse = error.networkResponse
            if (networkResponse != null && networkResponse.statusCode == 401) {
                Logger.info("HTTP Status Code: 401 Unauthorized")
            }
            callBack.callbackErrorCalled(error)
            Log.d("Result", "get result for as json array error" + error.toString())
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = hash
                params["Device"] = "Android"
                return params
            }

        }
        VolleyNetworkHelper.getInstance(mCtx).addToRequestQueue(arrayReq)
    }

    fun postRequestWithParams(posUrl: String, params: Map<String, String>, title: String, hash: String, listener: VolleyPostRequestListener) {

        val postRequest = object : StringRequest(Request.Method.POST, posUrl, Response.Listener { response -> listener.requestCompleted(response) }, Response.ErrorListener { error -> listener.requestEndedWithError(error) }) {
            override fun getParams(): Map<String, String> {
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = hash
                return params
            }
        }

        VolleyNetworkHelper.getInstance(mCtx).addToRequestQueue(postRequest)
    }


}

