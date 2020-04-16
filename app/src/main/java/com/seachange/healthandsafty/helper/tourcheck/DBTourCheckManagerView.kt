package com.seachange.healthandsafty.helper.tourcheck

import com.seachange.healthandsafty.model.DBTourCheckModel

import java.util.ArrayList

interface DBTourCheckManagerView {
    fun allTourChecksInDB(mList: ArrayList<DBTourCheckModel>?)
    fun getTourCheckByIdInDB(mTour: DBTourCheckModel?)
}
