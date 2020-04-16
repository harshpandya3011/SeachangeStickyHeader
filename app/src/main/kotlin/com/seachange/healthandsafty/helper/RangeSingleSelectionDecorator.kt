package com.seachange.healthandsafty.helper

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.annotation.ColorInt
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import org.threeten.bp.LocalDate

class RangeSingleSelectionDecorator(
        date: LocalDate,
        @ColorInt private val color: Int
) : DayViewDecorator {

    private val date: CalendarDay = CalendarDay.from(date)

    private val selectionDrawable = ShapeDrawable(OvalShape()).apply {
        paint.color = color
    }

    override fun shouldDecorate(day: CalendarDay?) = day == date

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.WHITE))
        view?.setSelectionDrawable(selectionDrawable)
    }
}