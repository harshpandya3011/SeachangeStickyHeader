package com.seachange.healthandsafty.presenter

import android.content.Context
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.RAHazards
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.RAHazardView
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class RAHazardsPresenter(private val mView:RAHazardView, private val mCtx: Context) {

    private val TAG = "Assessment_Hazards"

    fun fetchDataFromServer() {

        val callBack = object : JsonCallBack {

            override fun callbackJSONObject(result: JSONObject) {

                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        if (statusCode == 304) {
                            Logger.info(TAG + ": statusCode 304 returned, no changes from server")
                            return
                        } else if (statusCode == 200) {
//                            if (result.has(UtilStrings.ETag)) {
//                                PreferenceHelper.getInstance(mCtx).saveSiteETag(result.getString(UtilStrings.PREFERENCES_SITE_ETAG))
//                                Logger.info(SITE_USER_TAG + ": have updates, response returned, update eTag and response")
//                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<RAHazards>>() {}.type
                                val arrayList = gson.fromJson<ArrayList<RAHazards>>(result.getJSONArray(UtilStrings.RESPONSE).toString(), type)
                                mView.receivedResponse(arrayList)
                            }
                        }
                    } catch (e: Exception) {
                        Logger.infoRiskAssessment(TAG + ": " + e.toString())
                    }
                }
            }

            override fun callbackJsonArray(result: JSONArray) {

            }

            override fun callbackErrorCalled(result: VolleyError) {
                mView.errorReceived(result)
            }
        }

        VolleyJsonHelper(UtilStrings.RA_RISK_HAZARDS_API, TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(mCtx).requestToken(),null)
    }
}