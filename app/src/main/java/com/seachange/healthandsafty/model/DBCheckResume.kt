package com.seachange.healthandsafty.model

import android.os.Parcel
import android.os.Parcelable

class DBCheckResume() : Parcelable {
    var timeResumed: String? = null

    constructor(parcel: Parcel) : this() {
        timeResumed = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(timeResumed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DBCheckResume> {
        override fun createFromParcel(parcel: Parcel): DBCheckResume {
            return DBCheckResume(parcel)
        }

        override fun newArray(size: Int): Array<DBCheckResume?> {
            return arrayOfNulls(size)
        }
    }
}