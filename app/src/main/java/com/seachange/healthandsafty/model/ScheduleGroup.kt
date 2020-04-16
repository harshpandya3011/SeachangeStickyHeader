package com.seachange.healthandsafty.model

data class ScheduleGroup(
        var title:String,
        var selected: Boolean,
        var tabDay: Int,
        var withSchedule: Boolean
)