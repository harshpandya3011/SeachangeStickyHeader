package com.seachange.healthandsafty.utils

class DateUtil {

    fun getDaySubString(mTAbNumber: Int): String {
        var tabDay = "Mon"
        when (mTAbNumber) {
            1 -> {
                tabDay = "Tue"
            }
            2 -> {
                tabDay = "Wed"
            }
            3 -> {
                tabDay = "Thur"
            }
            4 -> {
                tabDay = "Fri"
            }
            5 -> {
                tabDay = "Sat"
            }
            6 -> {
                tabDay = "Sun"
            }
        }
        return tabDay
    }

    fun getDayString(mTAbNumber: Int): String {
        var tabDay = "Monday"
        when (mTAbNumber) {
            1 -> {
                tabDay = "Tuesday"
            }
            2 -> {
                tabDay = "Wednesday"
            }
            3 -> {
                tabDay = "Thursday"
            }
            4 -> {
                tabDay = "Friday"
            }
            5 -> {
                tabDay = "Saturday"
            }
            6 -> {
                tabDay = "Sunday"
            }
        }
        return tabDay
    }

    fun getDayPosition(DayString: String): Int {
        var tabDay = 0
        when (DayString) {
            "Monday" -> {
                tabDay = 1
            }
            "Tuesday" -> {
                tabDay = 2
            }
            "Wednesday" -> {
                tabDay = 3
            }
            "Thursday" -> {
                tabDay = 4
            }
            "Friday" -> {
                tabDay = 5
            }
            "Saturday" -> {
                tabDay = 6
            }
            "Sunday" -> {
                tabDay = 7
            }
        }
        return tabDay
    }
}