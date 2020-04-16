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

class RangeStartSelectionDecorator(
        dateRange: Pair<LocalDate, LocalDate>,
        @ColorInt private val color: Int,
        @ColorInt private val backgroundColor: Int,
        cornerRadius: Float?
) : DayViewDecorator {

    private val startDate: CalendarDay = CalendarDay.from(dateRange.first)

    private val selectionDrawable = ShapeDrawable(OvalShape()).apply {
        paint.color = color
    }

    private val backgroundDrawable = GradientDrawable().also {
        it.shape = GradientDrawable.RECTANGLE
        cornerRadius?.let { radius ->
            it.cornerRadii = floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, radius, radius)
        }
        it.setColor(backgroundColor)
    }

    override fun shouldDecorate(day: CalendarDay?) = day == startDate

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.WHITE))
        view?.setBackgroundDrawable(backgroundDrawable)
        view?.setSelectionDrawable(selectionDrawable)
    }
}