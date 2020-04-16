package com.seachange.healthandsafty.nfc.helper

import android.content.Context
import com.android.volley.VolleyError
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.network.VolleyPostRequestListener
import com.seachange.healthandsafty.nfc.view.NFCSyncView
import com.seachange.healthandsafty.utils.UtilStrings

class NFCSyncPresenter(private val mCtx: Context, private val mView: NFCSyncView) {

    fun syncNFCZoneTags(siteId: String, data: String) {

        val callBack = object : VolleyPostRequestListener {

            override fun requestStarted() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun requestCompleted(response: String?) {
                Logger.info("sync tags successfully: " + response)
                mView.requestSucceed()
            }

            override fun requestEndedWithError(error: VolleyError?) {
                Logger.info("sync tags failed: " )
                mView.requestError(error)
            }
        }
        val url = UtilStrings.CAYGO_ROOT_API + siteId +"/sync-tags"
        VolleyJsonHelper(mCtx).postRequestWithRaw(url, data, "sync-tags", PreferenceHelper.getInstance(mCtx).requestToken(), callBack)
    }
}