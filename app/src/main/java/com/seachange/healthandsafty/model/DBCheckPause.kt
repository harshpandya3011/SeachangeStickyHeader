package com.seachange.healthandsafty.model

import android.os.Parcel
import android.os.Parcelable

class DBCheckPause() : Parcelable {
     var timePaused: String? = null

    constructor(parcel: Parcel) : this() {
        timePaused = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(timePaused)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DBCheckPause> {
        override fun createFromParcel(parcel: Parcel): DBCheckPause {
            return DBCheckPause(parcel)
        }

        override fun newArray(size: Int): Array<DBCheckPause?> {
            return arrayOfNulls(size)
        }
    }
}