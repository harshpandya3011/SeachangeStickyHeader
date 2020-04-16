package com.seachange.healthandsafty.view

import java.util.*

interface TimerUpdateView {
    fun timerUpdated(hourOfDay: Int, minute: Int, calendar: Calendar)
}