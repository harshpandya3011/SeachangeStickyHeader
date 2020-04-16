package com.seachange.healthandsafty.model

import android.os.Parcel
import android.os.Parcelable

class DBCheckComplete() : Parcelable {
    var qrCodeId: String? = null
    var timeCompleted: String? = null

    constructor(parcel: Parcel) : this() {
        qrCodeId = parcel.readString()
        timeCompleted = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(qrCodeId)
        parcel.writeString(timeCompleted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DBCheckComplete> {
        override fun createFromParcel(parcel: Parcel): DBCheckComplete {
            return DBCheckComplete(parcel)
        }

        override fun newArray(size: Int): Array<DBCheckComplete?> {
            return arrayOfNulls(size)
        }
    }
}