package com.seachange.healthandsafty.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by kevinsong on 18/01/2018.
 */
class ZoneCheckHazard() : Parcelable {

        var groupId: Int? = null
        var zoneId: Int? = null
        var zoneCheckId: String? = null
        var scheduledZoneCheckTimeId: String? = null
        var siteTourId: String? = null
        var hazardId: String? = null
        var timeFound: String? = null
        var typeId: String? = null
        var typeName: String? = null
        var categoryName: String? = null
        var riskName: String? = null
        var isResolved: Boolean? = true
        var passcodeUser: String? = null

        constructor(parcel: Parcel) : this() {
            groupId = parcel.readValue(Int::class.java.classLoader) as? Int
            zoneId = parcel.readValue(Int::class.java.classLoader) as? Int
            zoneCheckId = parcel.readString()
            scheduledZoneCheckTimeId = parcel.readString()
            siteTourId = parcel.readString()
            hazardId = parcel.readString()
            timeFound = parcel.readString()
            typeId = parcel.readString()
            typeName = parcel.readString()
            categoryName = parcel.readString()
            riskName = parcel.readString()
            isResolved = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
            passcodeUser = parcel.readString()
        }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(groupId)
        parcel.writeValue(zoneId)
        parcel.writeString(zoneCheckId)
        parcel.writeString(scheduledZoneCheckTimeId)
        parcel.writeString(siteTourId)
        parcel.writeString(hazardId)
        parcel.writeString(timeFound)
        parcel.writeString(typeId)
        parcel.writeString(typeName)
        parcel.writeString(categoryName)
        parcel.writeString(riskName)
        parcel.writeValue(isResolved)
        parcel.writeString(passcodeUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ZoneCheckHazard> {
        override fun createFromParcel(parcel: Parcel): ZoneCheckHazard {
            return ZoneCheckHazard(parcel)
        }

        override fun newArray(size: Int): Array<ZoneCheckHazard?> {
            return arrayOfNulls(size)
        }
    }

}