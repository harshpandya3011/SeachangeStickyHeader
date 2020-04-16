package com.seachange.healthandsafty.model

import android.os.Parcel
import android.os.Parcelable

class DBCheckCancel() : Parcelable {
    var timeCancelled: String? = null

    constructor(parcel: Parcel) : this() {
        timeCancelled = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(timeCancelled)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DBCheckCancel> {
        override fun createFromParcel(parcel: Parcel): DBCheckCancel {
            return DBCheckCancel(parcel)
        }

        override fun newArray(size: Int): Array<DBCheckCancel?> {
            return arrayOfNulls(size)
        }
    }

}