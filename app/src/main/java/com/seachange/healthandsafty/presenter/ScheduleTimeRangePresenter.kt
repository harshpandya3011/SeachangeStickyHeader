package com.seachange.healthandsafty.presenter

import android.content.Context
import com.seachange.healthandsafty.view.ScheduleTimeRangeView

class ScheduleTimeRangePresenter (private val mCtx: Context, private val mView: ScheduleTimeRangeView) {

    fun getIntervalInMinutes(position: Int):Int {
        var interval = 20
        when (position) {
            1 -> {
                interval = 30
            }
            2 -> {
                interval = 60
            }
            3 -> {
                interval = 120
            }
            4 -> {
                interval = 180
            }
        }
        return interval
    }
}