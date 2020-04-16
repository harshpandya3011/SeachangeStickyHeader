package com.seachange.healthandsafty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.seachange.healthandsafty.R
import kotlinx.android.synthetic.main.fragment_schedule_date_time.*
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import com.google.gson.Gson
import com.seachange.healthandsafty.helper.*
import com.seachange.healthandsafty.model.*
import com.seachange.healthandsafty.utils.DateUtil
import com.seachange.healthandsafty.utils.Util
import com.seachange.healthandsafty.view.ScheduleTimeRangeView
import com.seachange.healthandsafty.view.TimerUpdateView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ScheduleDateTimeFragment : BaseFragment(), ScheduleTimeRangeView {

    private var zone: SiteZone? = null
    private var startTimeSetter: TimeSetter? = null
    private var endTimeSetter: TimeSetter? = null
    private var mCurrentTab: Int = 0
    private var regular: Boolean = true
    private var mDate: String? = null
    private var startView: TimerUpdateView? = null
    private var endView: TimerUpdateView? = null
    private var mCurrentTimeRange: ZoneCheckTimeRange? = null
    private var frequencyPosition: Int = 0
    private var mScheduleHelper: ScheduleSetUpHelper? = null
    private var mCurrentTimeRanges: ArrayList<ZoneCheckTimeRange> = ArrayList()
    private var scheduleSetting: ScheduleSetting? = null
    private var currentSchedule: Schedule? = null
    private var mCaygoSite: CaygoSite? = null
    private var mStartTimeModel: ScheduleTimeModel? = null
    private var mEndTimeModel: ScheduleTimeModel? = null

    private var btnEnableStatus = false
    private var isEdit = false
    private var editTimeRange: ZoneCheckTimeRange? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInterfaceView()
        mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
        mScheduleHelper = ScheduleSetUpHelper(mCtx)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startTimeSetter = TimeSetter(schedule_start_timer, startView)
        endTimeSetter = TimeSetter(schedule_end_timer, endView)

        initSpinner()
        initButton()
        initClickView()
        initTimeSetter()
        initCurrentTimeRange()
        updateAddTimeRangeButton()
        initTimeSetter()

        if (isEdit) setEditTimeRange()
        else  initCalendarClick()
    }

    private fun initTimeSetter() {
        startTimeSetter!!.initCalendar(8, 0)
        endTimeSetter!!.initCalendar(18, 0)
        mStartTimeModel = ScheduleTimeModel(8, 0)
        mEndTimeModel = ScheduleTimeModel(18, 0)
    }

    private fun initCalendarClick() {
        startView!!.timerUpdated(8,0, startTimeSetter!!.currentCalendar)
        endView!!.timerUpdated(18, 0, endTimeSetter!!.currentCalendar)
    }

    private fun initCurrentTimeRange() {
        scheduleSetting = mCaygoSite!!.getScheduleSettingByZoneId(zone!!.zone_id)
        val savedScheduleSetting = mScheduleHelper!!.getCurrentScheduleSettingByZoneId(zone!!.zone_id, true)
        mCurrentTimeRanges = ArrayList()

        if (regular) {
//            if (scheduleSetting != null && scheduleSetting!!.isHasRegularSchedule) {
//                currentSchedule = scheduleSetting!!.getScheduleByDay(mCurrentTab + 1)
//                if (currentSchedule != null && currentSchedule!!.zoneCheckTimeRanges.size > 0) {
//                    mCurrentTimeRanges.addAll(currentSchedule!!.zoneCheckTimeRanges!!)
//                }
//            }

            if (savedScheduleSetting != null && savedScheduleSetting.isHasRegularSchedule) {
                currentSchedule = savedScheduleSetting.getScheduleByDay(mCurrentTab + 1)
                if (currentSchedule != null && currentSchedule!!.zoneCheckTimeRanges.size > 0) {
                    mCurrentTimeRanges.addAll(currentSchedule!!.zoneCheckTimeRanges!!)
                }
            }
        } else {
            if (scheduleSetting != null && !scheduleSetting!!.isHasRegularSchedule) {
                currentSchedule = savedScheduleSetting.getScheduleByDate(mDate)
                if (currentSchedule != null && currentSchedule!!.zoneCheckTimeRanges.size>0) {
                    mCurrentTimeRanges = currentSchedule!!.zoneCheckTimeRanges
                }
            }
        }
    }

    private fun checkIfTimeIsValid(time_str: String): Boolean {
        val tempArrayList: ArrayList<ZoneCheckTimeRange> = mCurrentTimeRanges.clone() as ArrayList<ZoneCheckTimeRange>
        if (isEdit) {
            for (timeRange in mCurrentTimeRanges) {
                if (timeRange.startTime.equals(editTimeRange!!.startTime) && timeRange.endTime.equals(editTimeRange!!.endTime)) {
                    tempArrayList.remove(timeRange)
                    Logger.info("this is the time now: remove " + timeRange.startTime)
                }
            }
        }

        if (tempArrayList.size > 0) {
            for (timeRange in tempArrayList) {
                Logger.info("this is the time now: " + time_str)
                Logger.info("this is the time start: " + timeRange.startTime)
                Logger.info("this is the time end: " + timeRange.endTime)
                if (Util().isTimeBetweenTwoTime(timeRange.startTime, timeRange.endTime, time_str)) {
                    return true
                }
                else if (checkIfTimeRangeOverlap(timeRange.startTime, timeRange.endTime)) {
                    return true
                }

            }
        }
        return false
    }

    private fun checkIfTimeRangeOverlap( argStartTime: String,
                                         argEndTime:String):Boolean {

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val start = sdf.parse(argStartTime)
        val end = sdf.parse(argEndTime)
        val cal = Calendar.getInstance()
        val calEnd = Calendar.getInstance()

        cal.time = start
        calEnd.time = end

        val hours = cal.get(Calendar.HOUR_OF_DAY)
        val minutes = cal.get(Calendar.MINUTE)

        val hoursEnd = calEnd.get(Calendar.HOUR_OF_DAY)
        val minutesEnd = calEnd.get(Calendar.MINUTE)
        Logger.info("this is time range: end timer: " + mEndTimeModel!!.timeHour + "start hour: " + mStartTimeModel!!.timeHour )
        if (hours >= mStartTimeModel!!.timeHour && minutes >= mStartTimeModel!!.timeMinute && hoursEnd <= mEndTimeModel!!.timeHour && minutesEnd <= mEndTimeModel!!.timeMinute) return true

        return false
    }

    private fun initInterfaceView() {
        startView = object : TimerUpdateView {
            override fun timerUpdated(hourOfDay: Int, minute: Int, calendar: Calendar) {
                mStartTimeModel = ScheduleTimeModel(hourOfDay, minute)
                startTimeSetter!!.initCalendar(hourOfDay, minute)
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime = sdf.format(calendar.time) + ":00"
                if (checkIfTimeIsValid(currentTime)) {
                    btnEnableStatus = false
                } else {
                    btnEnableStatus = true
                    if (isEdit) btnEnableStatus = true
                    //check start time if is valid
                    val endTime = schedule_end_timer.text.toString() + ":00"
                    if(checkIfTimeIsValid(endTime)) {
                        btnEnableStatus = false
                    }

                    if (!next_day_switch.isChecked) {
                        if (!timeIsEndBeforeStart( mStartTimeModel!!, mEndTimeModel!!)) {
                            schedule_start_timer.text = mEndTimeModel!!.getTimeText(false)
                            startTimeSetter!!.setHour(mEndTimeModel!!.timeHour -1)
                            mStartTimeModel!!.timeHour = mEndTimeModel!!.timeHour -1

                            val startTime = schedule_start_timer.text.toString() + ":00"
                            if(checkIfTimeIsValid(startTime)) {
                                btnEnableStatus = false
                            }
                        }
                    }
                    //update current object
                    createOrUpdateTimeRange()
                }
                Logger.info("START AND END: START: " + btnEnableStatus )
                updateAddTimeRangeButton()
            }
        }
        endView = object : TimerUpdateView {
            override fun timerUpdated(hourOfDay: Int, minute: Int, calendar: Calendar) {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime = sdf.format(calendar.time) + ":00"
                mEndTimeModel = ScheduleTimeModel(hourOfDay, minute)
                endTimeSetter!!.setHour(hourOfDay)
                endTimeSetter!!.initCalendar(hourOfDay, minute)
                if (checkIfTimeIsValid(currentTime)) {
                    btnEnableStatus = false
                } else {
                    btnEnableStatus = true
                    if(isEdit) btnEnableStatus = true

                    //check start time if is valid
                    val startTime = schedule_start_timer.text.toString() + ":00"
                    if(checkIfTimeIsValid(startTime)) {
                        btnEnableStatus = false
                    }

                    if(mStartTimeModel != null && mEndTimeModel != null) {
                        if (!timeIsEndBeforeStart(mStartTimeModel!!, mEndTimeModel!!)) {
                            schedule_end_timer.text = mStartTimeModel!!.getTimeText(true)
                            mEndTimeModel!!.timeHour = mStartTimeModel!!.timeHour+1
                            endTimeSetter!!.setHour(mStartTimeModel!!.timeHour+1)

                            val endTime = sdf.format(endTimeSetter!!.currentCalendar.time) + ":00"
                            if(checkIfTimeIsValid(endTime)) {
                                btnEnableStatus = false
                            }
                        }
                    }
                    //update current object
                    createOrUpdateTimeRange()
                }
                updateAddTimeRangeButton()
            }
        }
    }

    private fun timeIsEndBeforeStart(start: ScheduleTimeModel, end: ScheduleTimeModel): Boolean {
        if(end.timeHour> start.timeHour) return true
        if (end.timeHour == start.timeHour && end.timeMinute > start.timeMinute) return true
        return false
    }

    private fun updateAddTimeRangeButton() {
        save_scheduled_time_button.isEnabled = btnEnableStatus
        if (isEdit) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val start = sdf.parse(editTimeRange!!.startTime)
            val end = sdf.parse(editTimeRange!!.endTime)
            val diff = end.time - start.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            if (minutes < getFrequencyByPosition(frequencyPosition)) {
                save_scheduled_time_button.isEnabled = false
            }
        }
    }

    private fun initTitle() {
        if (regular) {
            (activity as AppCompatActivity).supportActionBar!!.title = DateUtil().getDayString(mCurrentTab)
        } else {
            try {
                val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                val tmp = sdf.parse(mDate)
                val format = SimpleDateFormat("EEEE", Locale.getDefault())
                val dateString = format.format(tmp)
                (activity as AppCompatActivity).supportActionBar!!.title = dateString
                processTabData(dateString)
            } catch (ex: ParseException) {
                ex.printStackTrace()
            }
        }
    }

    private fun initClickView() {
        start_time_view.setOnClickListener {
            startTimeSetter!!.onClick(startTimeSetter!!.getmTv())
        }

        schedule_start_time.setOnClickListener {
            startTimeSetter!!.onClick(startTimeSetter!!.getmTv())
        }

        end_time_view.setOnClickListener {
            endTimeSetter!!.onClick(endTimeSetter!!.getmTv())
        }

        schedule_end_time.setOnClickListener {
            endTimeSetter!!.onClick(endTimeSetter!!.getmTv())
        }

        schedule_time_frequency_view.setOnClickListener {
            schedule_timer_frequency_spinner.performClick()
        }

        schedule_time_frequency.setOnClickListener {
            schedule_timer_frequency_spinner.performClick()
        }

        next_day_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val tmp = DateUtil().getDaySubString(mCurrentTab + 1)
                schedule_end_time.text = "End Time ($tmp)"
            } else {
                schedule_end_time.text = resources.getString(R.string.end_time)
            }
            createOrUpdateTimeRange()
        }
        initTitle()
    }

    private fun processTabData(date: String) {
        for (i in 0..9) {
            if (DateUtil().getDayString(i).equals(date)) {
                mCurrentTab = i
                return
            }
        }
    }

    private fun createOrUpdateTimeRange() {
        if (mCurrentTimeRange == null) mCurrentTimeRange = ZoneCheckTimeRange()
        mCurrentTimeRange!!.isEndTimeIsNextDay = next_day_switch.isChecked
        mCurrentTimeRange!!.startTime = schedule_start_timer.text.toString() + ":00"
        mCurrentTimeRange!!.endTime = schedule_end_timer.text.toString() + ":00"
        mCurrentTimeRange!!.intervalInMinutes = getFrequencyByPosition(frequencyPosition)
        mCurrentTimeRange!!.timeToCompleteInMinutes = getFrequencyByPosition(frequencyPosition)

        val gson = Gson()
        val temp = gson.toJson(mCurrentTimeRange)
        Logger.info("TIME_RANGE" + temp)
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(
                activity!!.applicationContext,
                R.array.pref_schedule_frequency,
                R.layout.right_spinner_align
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.right_spinner_align)
            schedule_timer_frequency_spinner.adapter = adapter
        }

        schedule_timer_frequency_spinner.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(adapter: AdapterView<*>, v: View,
                                        position: Int, id: Long) {
                frequencyPosition = position
                if (isEdit) {
                    if (getFrequencyByPosition(position) != editTimeRange!!.intervalInMinutes){
                        btnEnableStatus = true
                        updateAddTimeRangeButton()
                    } else {
                        if (mCurrentTimeRange !=null && editTimeRange !=null) {
                            if (mCurrentTimeRange!!.startTime == editTimeRange!!.startTime && mCurrentTimeRange!!.endTime == editTimeRange!!.endTime) {
                                btnEnableStatus = false
                                updateAddTimeRangeButton()
                            }
                        }
                    }
                }
                createOrUpdateTimeRange()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    fun initData(selectZone: SiteZone, tab: Int, isReg: Boolean, date: String, edit: Boolean, timeRange: ZoneCheckTimeRange?) {
        zone = selectZone
        mCurrentTab = tab
        regular = isReg
        mDate = date
        isEdit = edit
        editTimeRange = timeRange
    }

    private fun setEditTimeRange() {
        val gson = Gson()
        Logger.info("edit time range " + gson.toJson(editTimeRange))
        schedule_start_timer.text = getTimeString(editTimeRange!!.startTime)
        schedule_end_timer.text = getTimeString(editTimeRange!!.endTime)
        schedule_timer_frequency_spinner.setSelection(getFrequencyPosition(editTimeRange!!.intervalInMinutes!!))

        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.parse(editTimeRange!!.startTime)
        val end = sdf.parse(editTimeRange!!.endTime)
        startCalendar.time = start
        endCalendar.time = end
        mStartTimeModel = ScheduleTimeModel(startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE))
        mEndTimeModel = ScheduleTimeModel(endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE))

        startTimeSetter!!.initCalendar(startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE))
        endTimeSetter!!.initCalendar(endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE))
    }

    private fun getFrequencyPosition(frequency: Int): Int {
        when (frequency) {
            30 ->
                return 0
            60 ->
                return 1
            120 ->
                return 2
            180 ->
                return 3
            240 ->
                return 4
            300 ->
                return 5
            360 ->
                return 6
            420 ->
                return 7
            480 ->
                return 8
        }
        return 0
    }

    private fun getFrequencyByPosition(position: Int): Int {
        when (position) {
            0 ->
                return 30
            1 ->
                return 60
            2 ->
                return 120
            3 ->
                return 180
            4 ->
                return 240
            5 ->
                return 300
            6 ->
                return 360
            7 ->
                return 420
            8 ->
                return 480
        }
        return 0
    }

    private fun getTimeString(timeString: String): String {
        var time = ""
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val mFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            val date = format.parse(timeString)
            time = mFormat.format(date)
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return time
    }

    private fun initButton() {
        if (isEdit) {
            save_scheduled_time_button.text = mCtx.resources.getString(R.string.edit_time_range)
        } else {
            save_scheduled_time_button.text = mCtx.resources.getString(R.string.add_time_range)
        }
        save_scheduled_time_button.setOnClickListener {
            //add current time range
           if (regular) {
               if (isEdit) {
                   mScheduleHelper!!.editTimeRangeToExistingZoneSettingRegular(zone!!, mCurrentTab + 1, mCurrentTimeRange, editTimeRange)
               } else {
                   mScheduleHelper!!.AddTimeRangeToExistingZoneSettingRegular(zone!!, mCurrentTab + 1, mCurrentTimeRange)
               }

           } else {
               if (isEdit){
                   val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                   val sdFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                   mScheduleHelper!!.editTimeRangeToExistingZoneSettingIrregular(zone!!, sdf.format(sdFormat.parse(mDate)), mCurrentTimeRange, editTimeRange)
               } else {
                   val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                   val sdFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
                   mScheduleHelper!!.AddTimeRangeToExistingZoneSettingIrregular(zone!!, sdf.format(sdFormat.parse(mDate)), mCurrentTimeRange)
               }
           }
            HazardObserver.getInstance().isScheduleUpdated = true
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule_date_time, container, false)
    }
}
