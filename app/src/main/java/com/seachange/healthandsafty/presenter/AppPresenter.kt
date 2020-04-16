package com.seachange.healthandsafty.presenter

import android.content.Context
import android.os.Handler
import android.util.Base64
import com.android.volley.VolleyError
import com.seachange.healthandsafty.db.DBCheck
import com.seachange.healthandsafty.db.DBTourCheck
import com.seachange.healthandsafty.helper.*
import com.seachange.healthandsafty.helper.interfacelistener.RequestCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.network.VolleyPostRequestListener
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.AppView
import com.seachange.healthandsafty.view.RefreshView
import org.json.JSONObject

class AppPresenter(private val mCtx: Context, private val mView: AppView) {

    private var refreshing = false

    fun refreshToken() {

        if(refreshing) return
        if(PreferenceHelper.getInstance(mCtx).refreshToken == null) {
            Logger.info("REFRESH TOKEN: ERROR empty" )
            return
        }
        val callBack = object : VolleyPostRequestListener {

            override fun requestEndedWithError(error: VolleyError?) {

                if (error!!.networkResponse != null) {
                    if (error.networkResponse.statusCode !=null) {
                        if (error.networkResponse.statusCode == 400) {
                            PreferenceHelper.getInstance(mCtx).saveRefreshToken(null)
                        }
                    }
                    Logger.info("REFRESH TOKEN: ERROR " + error.networkResponse.statusCode)
                }
                mView.refreshWithError(error)
                Logger.info("REFRESH TOKEN tracking: request error - " + error.toString())
                refreshing= false
            }

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {

                val result = JSONObject(response)
                val token = result.getString("access_token")
                val refreshToken = result.getString("refresh_token")
                PreferenceHelper.getInstance(mCtx).saveToken(token)
                PreferenceHelper.getInstance(mCtx).saveRefreshToken(refreshToken)
                Logger.info("REFRESH TOKEN: request complete - save new refresh token - " + refreshToken)

                mView.refreshSuccessfully(result)
                refreshing = false
            }
        }

        refreshing = true
        val hashMap = HashMap<String, String>()
        hashMap["refresh_token"] = PreferenceHelper.getInstance(mCtx).refreshToken
        hashMap["grant_type"] = "refresh_token"
        Logger.info("REFRESH TOKEN: old refresh token - send request " + PreferenceHelper.getInstance(mCtx).refreshToken)


        val authBasic = String.format("%s:%s", UtilStrings.AUTH_CLIENT, UtilStrings.AUTH_BASIC)
        val auth = "Basic " + Base64.encodeToString(authBasic.toByteArray(), Base64.DEFAULT)
        VolleyJsonHelper(mCtx, false, 120000).postRequestWithParams(UtilStrings.LOGIN_API, hashMap, "refresh token", auth, callBack)
    }

    fun refreshTokenWithView(refreshView: RefreshView) {

        if(refreshing) return
        if(PreferenceHelper.getInstance(mCtx).refreshToken == null) {
            Logger.info("REFRESH TOKEN: ERROR empty" )
            return
        }
        val callBack = object : VolleyPostRequestListener {

            override fun requestEndedWithError(error: VolleyError?) {

                if (error!!.networkResponse != null) {
                    if (error.networkResponse.statusCode !=null) {
                        if (error.networkResponse.statusCode == 400) {
                            PreferenceHelper.getInstance(mCtx).saveRefreshToken(null)
                        }
                    }
                }
                refreshView.tokenRefreshWithError(error)
                refreshing= false
            }

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {

                val result = JSONObject(response)
                val token = result.getString("access_token")
                val refreshToken = result.getString("refresh_token")
                PreferenceHelper.getInstance(mCtx).saveToken(token)
                PreferenceHelper.getInstance(mCtx).saveRefreshToken(refreshToken)
                refreshView.tokenRefreshSuccessfully(result)
                refreshing = false
            }
        }

        refreshing = true
        val hashMap = HashMap<String, String>()
        //        hashMap["refresh_token"] = "ac14d192-a4fc-44f2-8144-1db47b3af1e0"
        hashMap["refresh_token"] = PreferenceHelper.getInstance(mCtx).refreshToken
        hashMap["grant_type"] = "refresh_token"

        val authBasic = String.format("%s:%s", UtilStrings.AUTH_CLIENT, UtilStrings.AUTH_BASIC)
        val auth = "Basic " + Base64.encodeToString(authBasic.toByteArray(), Base64.DEFAULT)
        VolleyJsonHelper(mCtx, false, 120000).postRequestWithParams(UtilStrings.LOGIN_API, hashMap, "refresh token", auth, callBack)
    }

    fun syncCachedZoneChecks(data: String, tmp : List<DBCheck> , callBack: RequestCallBack) {

        val mCallBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
                HazardObserver.getInstance().isSyncInProgress = true
                HazardObserver.getInstance().isSyncComplete = false
                HazardObserver.getInstance().isSyncEndWithError = false
            }

            override fun requestCompleted(response: String?) {
                HazardObserver.getInstance().isSyncComplete = true
                HazardObserver.getInstance().isSyncInProgress = false

                HazardObserver.getInstance().isSyncEndWithError = false
                Logger.info("END ZONE CHECK: SYNC: complete successfully!!!")
                //reset db, remove list of synced checks from db
                Handler().postDelayed({
                    callBack.onSucceed()
                    PreferenceHelper.getInstance(mCtx).fetchScheduledDataNotify()
                }, 2000)
            }

            override fun requestEndedWithError(error: VolleyError?) {
                HazardObserver.getInstance().isSyncEndWithError = true
                HazardObserver.getInstance().isSyncInProgress = false
                callBack.onError(error)
                Logger.info("END ZONE CHECK: SYNC: error:" + error!!.message)
            }
        }

        val caygoSite = PreferenceHelper.getInstance(mCtx).siteData
        val completeURL = UtilStrings.CAYGO_ROOT_API + caygoSite.site_id + "/sync-zone-checks"
        Logger.info("END ZONE CHECK: SYNC: " + data)
        VolleyJsonHelper(mCtx).postRequestWithRaw(completeURL, data, "Zone Check Complete", PreferenceHelper.getInstance(mCtx).requestToken(), mCallBack)
    }

    fun syncCachedTourZoneChecks(data: String, tmp : List<DBTourCheck> , callBack: RequestCallBack) {

        val mCallBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
                HazardObserver.getInstance().isSyncTourInProgress = true
                HazardObserver.getInstance().isSyncInProgress = true
                HazardObserver.getInstance().isSyncComplete = false

            }

            override fun requestCompleted(response: String?) {
                HazardObserver.getInstance().isSyncTourInProgress = false
                HazardObserver.getInstance().isSyncInProgress = false
                HazardObserver.getInstance().isSyncComplete = true

                Logger.info("END ZONE CHECK: TOUR SYNC: complete successfully!!!")
                //reset db, remove list of synced checks from db
                Handler().postDelayed({
                    callBack.onSucceed()
                    PreferenceHelper.getInstance(mCtx).fetchScheduledDataNotify()
                }, 2000)
            }

            override fun requestEndedWithError(error: VolleyError?) {
                HazardObserver.getInstance().isSyncEndWithError = true
                HazardObserver.getInstance().isSyncTourInProgress = false
                HazardObserver.getInstance().isSyncInProgress = false

                Logger.info("END ZONE CHECK: TOUR SYNC: error:" + error!!.message)
            }
        }

        val caygoSite = PreferenceHelper.getInstance(mCtx).siteData
        val completeURL = UtilStrings.CAYGO_ROOT_API + caygoSite.site_id + "/sync-site-tours"
        val dataToTrack = caygoSite.site_id.toString() + "/"+ caygoSite.site_name + "/"+data
        Logger.info("END ZONE CHECK: SYNC TOUR: " + dataToTrack)
        VolleyJsonHelper(mCtx).postRequestWithRaw(completeURL, data, "Site Tour Complete", PreferenceHelper.getInstance(mCtx).requestToken(), mCallBack)
    }
}