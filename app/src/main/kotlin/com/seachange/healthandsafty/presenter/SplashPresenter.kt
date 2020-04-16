package com.seachange.healthandsafty.presenter

import android.content.Context
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.CaygoSite
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.SplashView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SplashPresenter(private val mView: SplashView, private val mCtx: Context) {

    private val SITE_USER_TAG = "site_user_tag"

    fun fetchSiteDataFromServer() {

        val callBack = object : JsonCallBack {

            override fun callbackJSONObject(result: JSONObject) {

                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        if (statusCode == 304) {
                            Logger.info("$SITE_USER_TAG: statusCode 304 returned, no changes from server")
                            return
                        } else if (statusCode == 200) {
                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveSiteETag(result.getString(UtilStrings.PREFERENCES_SITE_ETAG))
                                Logger.info("$SITE_USER_TAG: have updates, response returned, update eTag and response")
                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                val gson = Gson()
                                val type = object : TypeToken<CaygoSite>() {

                                }.type

                                val caygoSite = gson.fromJson<CaygoSite>(result.getJSONObject(UtilStrings.RESPONSE).toString(), type)
                                //The new Endpoint sets user data,
                                //so ignore users from this old endpoint and reset to already saved users
                                caygoSite?.siteUsers = PreferenceHelper.getInstance(mCtx).siteData?.siteUsers?.sortedBy { it.fullName?.toLowerCase() }
                                PreferenceHelper.getInstance(mCtx).saveSiteData(caygoSite)
                                Logger.info("$SITE_USER_TAG: $caygoSite")
                            }

                        }
                    } catch (e: JSONException) {
                        Logger.info("$SITE_USER_TAG: $e")
                    }
                }
                mView.receivedResponse(result)
                Logger.info("get user data from splash, load user screen")
            }

            override fun callbackJsonArray(result: JSONArray) {

            }

            override fun callbackErrorCalled(result: VolleyError) {
                Logger.info("get user data from splash failed, load user screen, need to get data again on next screen")
                mView.errorReceived(result)
            }
        }
        VolleyJsonHelper(UtilStrings.CAYGO_ROOT_API, SITE_USER_TAG, callBack, mCtx, 2000).getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(mCtx).requestToken(), PreferenceHelper.getInstance(mCtx).siteETag)
    }
}