package com.seachange.healthandsafty.presenter

import android.content.Context
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.RiskAssement
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.JsonParser
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.RiskAssessView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class RiskAssessPresenter (private val mView:RiskAssessView, private val mCtx: Context) {

    private val TAG = "Risk Assessments";


    fun getAssessments() {
        val callBack = object : JsonCallBack {

            override fun callbackJSONObject(result: JSONObject) {

                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        Logger.info( TAG + "get user data from splash, load user screen, status code: " + statusCode)

                        if (statusCode == 304) {
                            Logger.info(TAG + "statusCode 304 returned, no changes from server")
                            return
                        } else if (statusCode == 200) {

                            if (result.has(UtilStrings.RESPONSE)) {
                                val gson = Gson()
                                val type = object : TypeToken<ArrayList<RiskAssement>>() {}.type
                                val array = gson.fromJson<ArrayList<RiskAssement>>(result.getJSONArray(UtilStrings.RESPONSE).toString(), type)
                                mView.receivedResponse(array)
                            }
                            Logger.info(TAG + ": have updates, response returned, update eTag and response" + result.toString())
                        }
                    } catch (e: JSONException) {
                        Logger.info(TAG + ": " + e.toString())
                    }
                }
            }

            override fun callbackJsonArray(result: JSONArray) {

            }

            override fun callbackErrorCalled(result: VolleyError) {
                    mView.errorReceived(result)
            }
        }

        VolleyJsonHelper(UtilStrings.RA_RISK_ASSESSMENT, TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(mCtx).requestToken(), "")
    }


}