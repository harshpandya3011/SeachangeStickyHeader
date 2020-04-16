package com.seachange.healthandsafty.helper.tourcheck

import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.db.DBCheckViewModel
import com.seachange.healthandsafty.db.DBTourCheck
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.model.DBTourCheckModel
import java.util.ArrayList

class DBTourCheckManager(private val mView: DBTourCheckManagerView, private val mActivity: AppCompatActivity) {

    fun retrieveAllTourChecks() {

        val mWordViewModel = ViewModelProviders.of(mActivity).get(DBCheckViewModel::class.java)
        mWordViewModel.allTourChecks.observe(mActivity, androidx.lifecycle.Observer { checks ->

            val cacheList : ArrayList<DBTourCheckModel> = ArrayList()
            for (i in checks!!.indices) {
                Logger.info("DB - > tour and zone... tour" + checks.get(i).getTourZoneCheck())
            }
            val gson = Gson()
            val type = object : TypeToken<DBTourCheckModel>() {}.type

            for (tmp in checks) {
                val temp = gson.fromJson<DBTourCheckModel>(tmp.tourZoneCheck, type)
                cacheList.add(temp)
            }
            mView.allTourChecksInDB(cacheList)
        })
    }

    fun getCurrentTourCheck(mTourId: String) {

        val mWordViewModel = ViewModelProviders.of(mActivity).get(DBCheckViewModel::class.java)
        mWordViewModel.allTourChecks.observe(mActivity, androidx.lifecycle.Observer { checks ->

            val gson = Gson()
            val type = object : TypeToken<DBTourCheckModel>() {}.type

            var newTour: Boolean = true
            for (tmp in checks!!) {
                val temp = gson.fromJson<DBTourCheckModel>(tmp.tourZoneCheck, type)
                if(temp !=null) {
                    if (temp.scheduledSiteTourId.equals(mTourId)) {
                        mView.getTourCheckByIdInDB(temp)
                        newTour = false
                    }
                }
            }

            if (newTour) {
                mView.getTourCheckByIdInDB(null)
            }
        })
    }

    fun updateOrEnterTourChecks(tourCheckModel: DBTourCheckModel?, mCtx:SCApplication) {
        updateOrEnterTourChecksWithStatus(tourCheckModel, mCtx, 0, false)
    }

    fun updateOrEnterTourChecksWithStatus(tourCheckModel: DBTourCheckModel?, mCtx:SCApplication, status:Int, withStatus:Boolean) {

        val mWordViewModel = ViewModelProviders.of(mActivity).get(DBCheckViewModel::class.java)
        var update = true
        mWordViewModel.allTourChecks.observe(mActivity, androidx.lifecycle.Observer { checks ->

            if (update) {
                update = false
                val gson = Gson()
                val type = object : TypeToken<DBTourCheckModel>() {}.type

                var newObject = true

                if (tourCheckModel != null) {
                    for (i in checks!!.indices) {

                        val temp = gson.fromJson<DBTourCheckModel>(checks[i].tourZoneCheck, type)
                        if (temp != null) {
                            Logger.info("TOUR CHECK THE STATUS: " + status +  " before set: " + checks[i].status)

                            if (temp.siteTourId.equals(tourCheckModel.siteTourId)) {

                                if (checks[i].tourZoneCheck != gson.toJson(tourCheckModel)) {
                                    checks[i].tourZoneCheck = gson.toJson(tourCheckModel)
                                    if(withStatus){
                                        checks[i].status = status
                                        Logger.info("TOUR CHECK THE STATUS: " + status +  " after set: " + checks[i].status)
                                    }
                                    mWordViewModel.updateTourCheck(checks[i])
                                    Logger.info("DB tour check update:  " + gson.toJson(checks[i]) +"sdfdsf"  + checks[i].status)
                                }
                                newObject = false
                            }
                        }
                    }
                }

                //
                //insert new tour check object to database
                //
                if (newObject) {
                    mCtx.insertTourCheck(DBTourCheck(gson.toJson(tourCheckModel)))
                }
            }
        })
    }
}
