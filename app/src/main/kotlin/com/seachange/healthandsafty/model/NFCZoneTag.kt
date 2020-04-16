package com.seachange.healthandsafty.model

import android.os.Parcel
import android.os.Parcelable

class NFCZoneTag() : Parcelable {
    var zoneId: Int? = null
    var setupTagPoints: ArrayList<TagPoint>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        zoneId = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(zoneId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NFCZoneTag> {
        override fun createFromParcel(parcel: Parcel): NFCZoneTag {
            return NFCZoneTag(parcel)
        }

        override fun newArray(size: Int): Array<NFCZoneTag?> {
            return arrayOfNulls(size)
        }
    }
}


class TagPoint() : Parcelable {

    var tagSummaryId: String? = null
    var setupDate: String? = null

    constructor(parcel: Parcel) : this() {
        tagSummaryId = parcel.readString()
        setupDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tagSummaryId)
        parcel.writeString(setupDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TagPoint> {
        override fun createFromParcel(parcel: Parcel): TagPoint {
            return TagPoint(parcel)
        }

        override fun newArray(size: Int): Array<TagPoint?> {
            return arrayOfNulls(size)
        }
    }
}