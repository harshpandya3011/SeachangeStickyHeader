package com.seachange.healthandsafty.helper

import android.content.Context
import com.android.volley.VolleyError
import com.seachange.healthandsafty.helper.interfacelistener.RequestCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.network.VolleyPostRequestListener
import com.seachange.healthandsafty.utils.UtilStrings

/**
 * Created by kevinsong on 16/01/2018.
 */

class ZoneCheckHelper(private val mCtx:Context) {

    //
    //check sync
    //
    fun zoneCheckSync(mSiteId: Int, token:String, callBack: RequestCallBack) {

        val mCallBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {
                callBack.onSucceed()
                Logger.info("END ZONE CHECK: SYNC: complete successfully!!!")
            }

            override fun requestEndedWithError(error: VolleyError?) {
                Logger.info("END ZONE CHECK: SYNC: error:" + error!!.message)
                callBack.onError(error)
            }
        }

        val mDBChecker = DBZoneCheckHelper(mCtx)
        val completeURL = UtilStrings.CAYGO_ROOT_API + mSiteId + "/sync-zone-checks"
        val data = "["+mDBChecker.checkForDB+"]"
        Logger.info("END ZONE CHECK: SYNC: " + data)
        VolleyJsonHelper(mCtx).postRequestWithRaw(completeURL, data, "Zone Check Complete", token, mCallBack)
    }

    //
    //check sync
    //
    fun zoneTourCheckSync(mSiteId: Int, token:String, callBack: RequestCallBack) {

        val mCallBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {
                callBack.onSucceed()
                Logger.info("END ZONE CHECK TOUR: SYNC: complete successfully!!!")
            }

            override fun requestEndedWithError(error: VolleyError?) {
                Logger.info("END ZONE CHECK TOUR: SYNC: error:" + error!!.message)
                callBack.onError(error)
            }
        }

        val mDBChecker = DBTourCheckHelper(mCtx)
        val completeURL = UtilStrings.CAYGO_ROOT_API + mSiteId + "/sync-site-tours"
        val data = "["+mDBChecker.currentTourCheckStr+"]"
        Logger.info("END ZONE CHECK TOUR: SYNC: " + data)
        VolleyJsonHelper(mCtx).postRequestWithRaw(completeURL, data, "Zone Check Complete", token, mCallBack)
    }
}
