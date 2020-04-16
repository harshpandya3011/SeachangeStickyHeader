package com.seachange.healthandsafty.presenter

import com.seachange.healthandsafty.model.HazardBP
import com.seachange.healthandsafty.view.HazardBPView

class HazardBPPresenter(private val mView:HazardBPView) {

    fun getData() {

        var mHazardBPs: ArrayList<HazardBP> = ArrayList()
        var mHazards: ArrayList<HazardBP> = ArrayList()
        var mBPs: ArrayList<HazardBP> = ArrayList()

        mHazardBPs.add(HazardBP("Hazard", "Draft"))
        mHazardBPs.add(HazardBP("Hazard", "Draft"))
        mHazardBPs.add(HazardBP("Hazard", "Draft"))
        mHazardBPs.add(HazardBP("Best Practice", "Draft", false))
        mHazardBPs.add(HazardBP("Best Practice", "Draft", false))
        mHazardBPs.add(HazardBP("Best Practice", "Draft", false))

        mHazards.add(HazardBP("Hazard", "Draft"))
        mHazards.add(HazardBP("Hazard", "Draft"))
        mHazards.add(HazardBP("Hazard", "Draft"))

        mBPs.add(HazardBP("Best Practice", "Draft", false))
        mBPs.add(HazardBP("Best Practice", "Draft", false))
        mBPs.add(HazardBP("Best Practice", "Draft", false))

        mView.hazardBPReceived(mHazardBPs, mHazards, mBPs)
    }
}