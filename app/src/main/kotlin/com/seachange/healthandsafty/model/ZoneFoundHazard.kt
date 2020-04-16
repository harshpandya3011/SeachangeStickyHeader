package com.seachange.healthandsafty.model

import com.google.gson.annotations.SerializedName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ZoneFoundHazard {

    var zoneId: Int? = null
    var zoneName: String? = null
    var siteId: Int? = null
    var siteName: String? = null
    var timeStarted: String? = null
    var timeCompleted: String? = null
    var duration: String? = null
    var numOfHazardsIdentified: Int? = null
    var status: Int? = null
    var identifiedHazards: ArrayList<ZoneHazard>? = null
    var timeScheduled: String? = null
    var isScheduled: Boolean = false
    var isSiteTour: Boolean = false
    var id: String? = null

    fun getStartDateString(): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = sdf.parse(timeStarted)
            return format.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    fun getCompleteDateString(): String {
        if (timeCompleted == null) return ""
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = sdf.parse(timeCompleted)
            return format.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getScheduledDateString(): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
            if (timeScheduled !=null) {
                val date = sdf.parse(timeScheduled)
                return format.format(date)
            } else {
                return ""
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getDurationString():String {

        if (duration!=null) {
            val times = duration!!.split(":")

            if (times.size == 3) {
                val hour = Integer.parseInt(times[0])
                val mins =  Integer.parseInt(times[1])
                val secs  = Integer.parseInt(times[2])

                if (hour>0) {
                    return hour.toString() + "h " + mins.toString() +"m "+ secs.toString() + "s"
                }else if(mins>0) {
                    return mins.toString() +"m "+ secs.toString() + "s"
                } else {
                    return secs.toString() + "s"
                }
            }
        }
        return ""
    }
}

class ZoneHazard {

    var name: String? = null
    var zoneId: Int? = null
    var zoneName: String? = null
    var hazardType: HazardType? = null
    var hazardTypeV2: HazardType? = null
    var dateTimeIdentified: String? = null
    var status: Int? = null
    var isScheduledZoneCheckHazard: Boolean? = false
    var isSiteTourHazard: Boolean? = false
    var id: String? = null

    fun getFoundDateString(): String {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = sdf.parse(dateTimeIdentified)
            return format.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

}

