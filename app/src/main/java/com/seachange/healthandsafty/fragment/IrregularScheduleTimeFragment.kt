package com.seachange.healthandsafty.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.ZoneSchedulingActivity
import com.seachange.healthandsafty.adapter.IrregularScheduleTimeRecyclerViewAdapter
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.Logger

import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.helper.ScheduleSetUpHelper
import com.seachange.healthandsafty.helper.interfacelistener.DateDialogView
import com.seachange.healthandsafty.model.*
import com.seachange.healthandsafty.presenter.ScheduleRequestPresenter
import com.seachange.healthandsafty.utils.Util
import com.seachange.healthandsafty.view.ScheduleRequestView
import com.seachange.healthandsafty.viewmodel.ManageScheduleViewModel
import kotlinx.android.synthetic.main.fragment_item_list9.*
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class IrregularScheduleTimeFragment : BaseFragment(), DateDialogView, ScheduleRequestView {

    private val manageScheduleViewModel by activityViewModels<ManageScheduleViewModel>()

    private var zone:SiteZone? = null
    private lateinit var mAdapter: IrregularScheduleTimeRecyclerViewAdapter
    private var mDate: String ? = null
    private var mCaygoSite: CaygoSite? = null
    private var scheduleSetting: ScheduleSetting? = null
    private var currentSchedule: Schedule? = null
    private var mScheduleHelper: ScheduleSetUpHelper? = null
    private var mCurrentTimeRanges: ArrayList<ZoneCheckTimeRange> = ArrayList()
    private var mListener: OnSwipeClickListener? = null
    private var mPresenter: ScheduleRequestPresenter? = null
    private var mZoneScheduleSetting: ScheduleSetting? = null
    private var mIsEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
        mScheduleHelper = ScheduleSetUpHelper(mCtx)
        mPresenter = ScheduleRequestPresenter(mCtx, this)
        initListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.title = zone!!.zone_name

        irregular_scheduled_time_picker.text = Util().todayDayDateWithMonth()
        irregular_scheduled_time_picker.setOnClickListener {
            showDatePicker()
        }

        fab_irregular_scheduled_timer.setOnClickListener {
            (activity as ZoneSchedulingActivity).selectIrregularTimeSetUp(zone!!, irregular_scheduled_time_picker.text.toString())
        }

        schedule_calendar_icon.typeface = SCApplication.FontMaterial()
        irregular_scheduled_time_picker.text = mDate
        initButtons()
        processData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list9, container, false)
    }

    private fun updateButtonAndSpinner(showSpinner: Boolean) {
        if (showSpinner) {
            progress_add_schedule_irregular.visibility = View.VISIBLE
            schedule_save_button.text = ""
        } else {
            progress_add_schedule_irregular.visibility = View.GONE
            schedule_save_button.text = getString(R.string.save_settings)
        }
    }

    override fun onResume() {
        super.onResume()
        initZoneScheduleSetting()
    }

    private fun initButtons() {
        irregular_header_view.setOnClickListener {
            showDatePicker()
        }

        schedule_save_button.setOnClickListener {
            sendDataToServer()
            updateButtonAndSpinner(true)
        }
    }

    fun initZone(selectZone: SiteZone, date: String) {
        zone = selectZone
        mDate = date
    }

    private fun processData() {
        scheduleSetting = mCaygoSite!!.getScheduleSettingByZoneId(zone!!.zone_id)
        mZoneScheduleSetting = mScheduleHelper!!.getCurrentScheduleSettingByZoneId(zone!!.zone_id, true)
        var day = 0

        if (mZoneScheduleSetting != null && !mZoneScheduleSetting!!.isHasRegularSchedule)  {
            currentSchedule = mZoneScheduleSetting!!.getScheduleByDate(mDate)
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            try {
                val c = Calendar.getInstance()
                c.time = sdf.parse(mDate)
                day = c.get(Calendar.DAY_OF_WEEK)
                day -= 1
                if (day == 0) day = 7
            } catch (ex:ParseException) {
                ex.printStackTrace()
            }

            if (currentSchedule != null && currentSchedule!!.zoneCheckTimeRanges.size>0) {
                mCurrentTimeRanges = currentSchedule!!.zoneCheckTimeRanges
            } else {
                mCurrentTimeRanges = ArrayList()
            }
        } else {
            mCurrentTimeRanges = ArrayList()
        }

        if (mCurrentTimeRanges.size>0) {
            mAdapter = IrregularScheduleTimeRecyclerViewAdapter(currentSchedule!!.zoneCheckTimeRanges!!, mCtx!!, day, mListener)
            irregular_time_list.adapter = mAdapter
            showNoContentView(false)
        } else {
            showNoContentView(true)
        }
    }

    private fun initZoneScheduleSetting() {
        mZoneScheduleSetting = mScheduleHelper!!.getCurrentScheduleSettingByZoneId(zone!!.zone_id, true)
        if(mZoneScheduleSetting!=null) {
            schedule_save_button.isEnabled = mZoneScheduleSetting!!.isUnSync
        } else {
            schedule_save_button.isEnabled = false
        }
    }

    private fun initListener() {
        mListener = object : OnSwipeClickListener{
            override fun onZoneRemoveClicked(scheduleTime: ZoneCheckTimeRange) {
                mScheduleHelper!!.RemoveTimeRangeToExistingZoneSettingIrregular(zone, mDate, scheduleTime)
                processData()
                initZoneScheduleSetting()
            }

            override fun onZoneEditClicked(scheduleTime: ZoneCheckTimeRange) {
                (activity as ZoneSchedulingActivity).selectIrregularTimeRangeSetUp(zone!!, 0 , false, true, scheduleTime, mDate!!)
            }
        }
    }

    private fun sendDataToServer() {
        val gson = Gson()
        val settingRequest = ScheduleSettingRequest()
        settingRequest.setDataFromSetting(mZoneScheduleSetting)
        settingRequest.groupId = mCaygoSite!!.groupId
        Logger.info("saved schedules to send to server: " + gson.toJson(settingRequest))
        mPresenter!!.postScheduleRange(gson.toJson(settingRequest), mCaygoSite!!.site_id.toString(), mIsEdit)
    }

    private fun showSnackBar() {
        Handler().postDelayed({
            Snackbar.make(view!!.findViewById(R.id.irregular_snack_ref), getString(R.string.schedule_setting_saved), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }, 10)
    }

    private fun showNoContentView(show: Boolean ) {
        if (show) {
            empty_tv.visibility = View.VISIBLE
            irregular_time_list.visibility = View.GONE
        } else {
            empty_tv.visibility = View.GONE
            irregular_time_list.visibility = View.VISIBLE
        }
    }

    interface OnSwipeClickListener {
        fun onZoneRemoveClicked(scheduleTime: ZoneCheckTimeRange)
        fun onZoneEditClicked(scheduleTime: ZoneCheckTimeRange)
    }

    private fun showDatePicker() {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        try {
            val c = Calendar.getInstance()
            c.time = sdf.parse(mDate)
            val dateFragment = MyDatePickerFragment.newInstance(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            dateFragment.setmListener(this)
            if (activity != null) dateFragment.show(activity!!.supportFragmentManager, "date picker")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    override fun onDialogPositiveClicked(date: String?, today: Boolean, dayNumber: Int) {
        try {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val sdFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val formatedDate = sdf.parse(date)
            val tmp = sdFormat.format(formatedDate)

            mDate = tmp
            processData()
            irregular_scheduled_time_picker.text = tmp
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun onDialogNegativeClicked() {

    }

    override fun refreshSuccessfully() {
        Logger.info("schedule post successfully")
        updateButtonAndSpinner(false)
        mZoneScheduleSetting!!.isUnSync = false
        mScheduleHelper!!.updateCurrentScheduleSettingByZoneId(zone!!.zone_id, mZoneScheduleSetting, false)

        //remove current saved data, used latest data from server
        mScheduleHelper!!.resetSavedSchedules()
        mScheduleHelper!!.initSchedules()

        showSnackBar()
        initZoneScheduleSetting()

    }

    override fun refreshWithError(error: VolleyError?) {
        Logger.info("schedule post refresh error")

    }

    override fun removeWithError(error: VolleyError?) {

    }

    override fun addTimeRangeWithError(error: VolleyError?) {
        Logger.info("schedule add time range error")
        updateButtonAndSpinner(false)
    }

    override fun removeSuccessfully() {

    }

    override fun reloadPage() {

    }
}
