package com.seachange.healthandsafty.model

import android.os.Parcel
import android.os.Parcelable

class DBTourCheckModel() : Parcelable {

    var groupId: Int? = null

    var siteTourId: String? = null
    var scheduledSiteTourId: String? = null

    var timeStarted: String? = null
    var timeCompleted: String? = null

    var pauseSiteTourCommands: ArrayList<DBCheckPause>? = ArrayList()
    var resumeSiteTourCommands: ArrayList<DBCheckResume>? = ArrayList()

    var zoneChecks: ArrayList<DBZoneCheckModel> = ArrayList()

    constructor(parcel: Parcel) : this() {
        groupId = parcel.readValue(Int::class.java.classLoader) as? Int
        siteTourId = parcel.readString()
        scheduledSiteTourId = parcel.readString()
        timeStarted = parcel.readString()
        timeCompleted = parcel.readString()
    }

    fun getExistingTourCheck(mZoneId: Int):DBZoneCheckModel? {

        for (tmp in zoneChecks) {
            if (tmp.zoneId == mZoneId) {
                return tmp
            }
        }
        return null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(groupId)
        parcel.writeString(siteTourId)
        parcel.writeString(scheduledSiteTourId)
        parcel.writeString(timeStarted)
        parcel.writeString(timeCompleted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DBTourCheckModel> {
        override fun createFromParcel(parcel: Parcel): DBTourCheckModel {
            return DBTourCheckModel(parcel)
        }

        override fun newArray(size: Int): Array<DBTourCheckModel?> {
            return arrayOfNulls(size)
        }
    }

}