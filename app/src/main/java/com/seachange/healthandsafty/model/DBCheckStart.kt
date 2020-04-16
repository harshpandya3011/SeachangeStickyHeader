package com.seachange.healthandsafty.model

import android.os.Parcel
import android.os.Parcelable

class DBCheckStart() : Parcelable {
    var qrCodeId: String? = null
    var timeStarted: String? = null

    constructor(parcel: Parcel) : this() {
        qrCodeId = parcel.readString()
        timeStarted = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(qrCodeId)
        parcel.writeString(timeStarted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DBCheckStart> {
        override fun createFromParcel(parcel: Parcel): DBCheckStart {
            return DBCheckStart(parcel)
        }

        override fun newArray(size: Int): Array<DBCheckStart?> {
            return arrayOfNulls(size)
        }
    }

}