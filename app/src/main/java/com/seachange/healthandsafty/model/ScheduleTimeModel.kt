package com.seachange.healthandsafty.model

import java.text.SimpleDateFormat
import java.util.*

data class ScheduleTimeModel(
        var timeHour: Int,
        var timeMinute: Int
){
    fun getTimeText(isEnd: Boolean):String{
        var calendar = Calendar.getInstance()
        if (isEnd) {
            calendar.set(Calendar.HOUR_OF_DAY, timeHour + 1)
        }else {
            if(timeHour>0)
            calendar.set(Calendar.HOUR_OF_DAY, timeHour - 1)
        }
        calendar.set(Calendar.MINUTE, timeMinute)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}

