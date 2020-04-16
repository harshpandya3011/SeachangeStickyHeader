package com.seachange.healthandsafty.presenter

import android.content.Context
import com.android.volley.VolleyError
import com.google.gson.GsonBuilder
import com.seachange.healthandsafty.helper.HazardObserver
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.GalleryImage
import com.seachange.healthandsafty.model.ImageHazard
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.JsonParser
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.network.VolleyPostRequestListener
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.HazardCategoryView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.collections.ArrayList

class RAHazardCategoryPresenter(private val mView: HazardCategoryView, private val context: Context) {

    private val TAG = "Risk Category TAG";

    fun getRiskCategories() {

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
//                                if (result.has(UtilStrings.ETag)) {
//                                    PreferenceHelper.getInstance(mCtx).saveSiteETag(result.getString(UtilStrings.PREFERENCES_SITE_ETAG))
//                                }
                            if (result.has(UtilStrings.RESPONSE)) {
                                val array = result.getJSONArray(UtilStrings.RESPONSE);
                                val arrayList = JsonParser().getRiskCategories(array)
                                mView.catetoriesReceived(arrayList)
                            }
                            Logger.info(TAG + ": have updates, response returned, update eTag and response" + result.toString())

                        }
                    } catch (e: Exception) {
                        Logger.info(TAG + ": " + e.toString())
                    }
                }
            }

            override fun callbackJsonArray(result: JSONArray) {

            }

            override fun callbackErrorCalled(result: VolleyError) {

            }
        }
        VolleyJsonHelper(UtilStrings.RA_RISK_API, TAG, callBack, context).getJsonObjectFromVolleyHelper(PreferenceHelper.getInstance(context).requestToken(), "")
    }

    fun postHazards(images: ArrayList<ImageHazard>) {

        val callBack = object : VolleyPostRequestListener {

            override fun requestStarted() {

            }

            override fun requestCompleted(response: String) {
                Logger.info(response)
                deleteSelectedImages()
                mView.postHazardSuccessfully()
            }

            override fun requestEndedWithError(error: VolleyError) {
                Logger.info(error.toString())
            }
        }

        val checkURL = UtilStrings.RA_POST_HAZARD + PreferenceHelper.getInstance(context).siteData.site_id + "/hazards"
        val gson = GsonBuilder().create()
        val params = gson.toJsonTree(images).asJsonArray

        VolleyJsonHelper(context).postRequestWithRaw(checkURL, params.toString(), "Risk Assessment Add Hazards", PreferenceHelper.getInstance(context).requestToken(), callBack)
    }


    fun deleteSelectedImages() {
        val mImageList = PreferenceHelper.getInstance(context).savedImages
        val tmpList = mImageList.clone() as ArrayList<GalleryImage>

        for (tmp in mImageList) {
            if (tmp.selected) {
                val file = File(tmp.path)
                if (file.exists()) {
                    file.delete()
                    tmpList.remove(tmp)
                    Logger.info("IMAGE DELETE: " + tmp.path)
                }
            }
        }
        PreferenceHelper.getInstance(context).saveImageTakenData(tmpList)
        HazardObserver.getInstance().isImagesChanged = true
    }
}