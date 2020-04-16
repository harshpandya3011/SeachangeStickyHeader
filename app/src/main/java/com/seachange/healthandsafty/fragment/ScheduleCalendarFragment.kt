package com.seachange.healthandsafty.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.prolificinteractive.materialcalendarview.*
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.ZoneSchedulingActivity
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.CaygoSite
import com.seachange.healthandsafty.model.SiteZone
import kotlinx.android.synthetic.main.fragment_schedule_calendar.*
import org.threeten.bp.LocalDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleCalendarFragment : BaseFragment(), OnDateSelectedListener {

    private var zone: SiteZone? = null
    private var mCaygoSite: CaygoSite? = null
    private var scheduledDates:ArrayList<CalendarDay>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.title = zone!!.zone_name
        calendarView.addDecorator(TodayDecorator())
        showSelectedDates()
    }

    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
        val currentDay = CalendarDay.from(LocalDate.now())
        if (isSelectedDateWithSchedule(date) || !date.isBefore(currentDay)) {
            try {
                val tmp = date.date.toString()
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val temp = sdf.parse(tmp)
                val sdformat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                val string = sdformat.format(temp)
                (activity as ZoneSchedulingActivity).selectTimeRangeSetUp(zone!!, string)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else {
            Logger.info("calendar........ can't set before today...")
        }
//        else {
//            try {
//                val tmp = date.date.toString()
//                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val temp = sdf.parse(tmp)
//                val sdformat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
//                val string = sdformat.format(temp)
//                (activity as ZoneSchedulingActivity).selectTimeRangeSetUp(zone!!, string)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun showSelectedDates() {
        scheduledDates = ArrayList()
        val scheduleSetting = mCaygoSite!!.getScheduleSettingByZoneId(zone!!.zone_id)
        if (scheduleSetting != null && !scheduleSetting.isHasRegularSchedule)  {
            if (scheduleSetting.schedules.size>0) {
                for (temp in scheduleSetting.schedules) {
                    try {
                        val tmp = temp.date
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val tmpDate = sdf.parse(tmp)
                        val calendarDate = Calendar.getInstance()
                        calendarDate.time = tmpDate
                        val day = CalendarDay.from(calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH)+1, calendarDate.get(Calendar.DAY_OF_MONTH))
                        scheduledDates!!.add(day)
                    }catch (ex:ParseException) {
                        ex.printStackTrace()
                    }
                }
            }
        }

        calendarView.setOnDateChangedListener(this)
        calendarView.addDecorator(EventDecorator(scheduledDates!!))
//        calendarView.addDecorators(showDisableDecorator())
    }

    private fun isSelectedDateWithSchedule(selectDate: CalendarDay):Boolean {
        for (temp in scheduledDates!!){
            if (temp.equals(selectDate)) return true
        }
        return false
    }


    fun initZone(selectZone: SiteZone) {
        zone = selectZone
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule_calendar, container, false)
    }

    private inner class TodayDecorator : DayViewDecorator {

        private val today: CalendarDay = CalendarDay.today()
        private val backgroundDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext, R.drawable.today_circle_background)!!

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return today == day
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(backgroundDrawable)
        }
    }

    private inner class EventDecorator(dates: Collection<CalendarDay>) : DayViewDecorator {
        private val dates: HashSet<CalendarDay> = HashSet(dates)
        private val backgroundDrawable: Drawable = AppCompatResources.getDrawable(activity!!.applicationContext, R.drawable.select_dates)!!

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(backgroundDrawable)
        }
    }

    private inner class showDisableDecorator : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.isBefore(CalendarDay.today())

        }

        override fun decorate(view: DayViewFacade) {
            view.setDaysDisabled(true)
        }
    }
}
