package com.seachange.healthandsafty.helper

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.annotation.ColorInt
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import org.threeten.bp.LocalDate


class RangeEndSelectionDecorator(
        dateRange: Pair<LocalDate, LocalDate>,
        @ColorInt private val color: Int,
        @ColorInt private val backgroundColor: Int,
        cornerRadius: Float?
) : DayViewDecorator {

    private val endDate: CalendarDay = CalendarDay.from(dateRange.second)
    private val selectionDrawable = ShapeDrawable(OvalShape()).apply {
        paint.color = color
    }
    private val backgroundDrawable = GradientDrawable().also {
        it.shape = GradientDrawable.RECTANGLE
        cornerRadius?.let { radius ->
            it.cornerRadii = floatArrayOf(0f, 0f, radius, radius, radius, radius, 0f, 0f)
        }
        it.setColor(backgroundColor)
    }

    override fun shouldDecorate(day: CalendarDay?) = day == endDate

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.WHITE))
        view?.setBackgroundDrawable(backgroundDrawable)
        view?.setSelectionDrawable(selectionDrawable)
    }
}