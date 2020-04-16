package com.seachange.healthandsafty.helper

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.ColorInt
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import org.threeten.bp.LocalDate

class RangeExclusiveSelectionDecorator(dateRange: Pair<LocalDate, LocalDate>, @ColorInt private val color: Int) : DayViewDecorator {

    private val startDate: CalendarDay = CalendarDay.from(dateRange.first)
    private val endDate: CalendarDay = CalendarDay.from(dateRange.second)
    private val backgroundDrawable = ShapeDrawable(RectShape()).apply {
        paint.color = color
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.isAfter(startDate) == true && day.isBefore(endDate)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(backgroundDrawable)
    }
}