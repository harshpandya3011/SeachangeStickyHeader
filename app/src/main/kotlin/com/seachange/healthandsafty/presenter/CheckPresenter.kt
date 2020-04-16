package com.seachange.healthandsafty.presenter

import android.content.Context
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.ZoneFoundHazard
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.CheckItemView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CheckPresenter (private val mView:CheckItemView, private val mCtx:Context){

    private val TAG = "Zone Check";

    fun getData(zoneCheckId: String) {

        val callBack = object : JsonCallBack {

            override fun callbackJSONObject(result: JSONObject) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        if (statusCode == 304) {
                            return
                        } else if (statusCode == 200) {
                            if (result.has(UtilStrings.RESPONSE)) {
                                val gson = Gson()
                                val type = object : TypeToken<ZoneFoundHazard>() {}.type
                                val zoneCheck = gson.fromJson<ZoneFoundHazard>(result.getJSONObject(UtilStrings.RESPONSE).toString(), type)
                                mView.getResponse(zoneCheck)
                            }
                            Logger.info(TAG + "eTag and response" + result.toString())
                        }
                    } catch (e: JSONException) {
                        Logger.info(TAG + ": error" + e.toString())
                    }
                }
            }

            override fun callbackJsonArray(result: JSONArray) {

            }

            override fun callbackErrorCalled(result: VolleyError) {
                mView.errorReceived(result)
            }
        }

        val url = UtilStrings.CAYGO_ROOT_API + PreferenceHelper.getInstance(mCtx).siteData.site_id + "/zone-checks/" + zoneCheckId
        VolleyJsonHelper(url, TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(mCtx).requestToken(), "")
    }
}