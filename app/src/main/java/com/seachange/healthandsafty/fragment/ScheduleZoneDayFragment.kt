package com.seachange.healthandsafty.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.ScheduleZoneDayRecyclerViewAdapter
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.helper.ScheduleSetUpHelper
import com.seachange.healthandsafty.model.*
import kotlinx.android.synthetic.main.fragment_item_list8.*
import org.parceler.Parcels
import java.util.ArrayList

class ScheduleZoneDayFragment : Fragment() {

    private lateinit var mAdapter:ScheduleZoneDayRecyclerViewAdapter
    private var mListener: OnFragmentInteractionListener? = null
    private var mTAbNumber: Int? = 1
    private var zone: SiteZone? = null
    private var mCaygoSite:CaygoSite? = null
    private var mCtx: Context? = null
    private var scheduleSetting: ScheduleSetting? = null
    private var currentSchedule: Schedule? = null
    private var mScheduleHelper: ScheduleSetUpHelper? = null
    private var mCurrentTimeRanges: ArrayList<ZoneCheckTimeRange> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mTAbNumber = arguments!!.getInt(ARG_TAB)
            zone = Parcels.unwrap(arguments!!.getParcelable(ARG_ZONE))
        }
        mCtx = activity!!.applicationContext
        mCaygoSite = PreferenceHelper.getInstance(mCtx).siteData
        initListener()
        mScheduleHelper = ScheduleSetUpHelper(mCtx)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initNoContentView()
        processData()
    }

    private fun processData() {
        scheduleSetting = mCaygoSite!!.getScheduleSettingByZoneId(zone!!.zone_id)
        val savedScheduleSetting = mScheduleHelper!!.getCurrentScheduleSettingByZoneId(zone!!.zone_id, true)
        val gson = Gson()
        Logger.info("saved schedules: " + gson.toJson(savedScheduleSetting))
        mCurrentTimeRanges = ArrayList()

//        if (scheduleSetting != null && scheduleSetting!!.isHasRegularSchedule) {
//            currentSchedule = scheduleSetting!!.getScheduleByDay(mTAbNumber!!)
//            if (currentSchedule != null && currentSchedule!!.zoneCheckTimeRanges.size > 0) {
//                mCurrentTimeRanges.addAll(currentSchedule!!.zoneCheckTimeRanges!!)
//            }
//        }

        if (savedScheduleSetting != null && savedScheduleSetting.isHasRegularSchedule)  {
            currentSchedule = savedScheduleSetting.getScheduleByDay(mTAbNumber!!)
            if (currentSchedule != null && currentSchedule!!.zoneCheckTimeRanges.size>0) {
                mCurrentTimeRanges.addAll(currentSchedule!!.zoneCheckTimeRanges!!)
            }
        }

        if(mCurrentTimeRanges.size>0) {
            mAdapter = ScheduleZoneDayRecyclerViewAdapter(mCurrentTimeRanges, mCtx!!, mTAbNumber!!, mListener)
            zone_day_list.adapter = mAdapter
        }else  {
            showNoContentView()
        }
    }

    private fun initNoContentView() {
        var tabDay = "Mondays"
        when (mTAbNumber) {
            2 -> {
                tabDay = "Tuesdays"
            }
            3 -> {
                tabDay = "Wednesdays"
            }
            4 -> {
                tabDay = "Thursdays"
            }
            5 -> {
                tabDay = "Fridays"
            }
            6 -> {
                tabDay = "Saturdays"
            }
            7 -> {
                tabDay = "Sundays"
            }
        }

        val defaultText = "Please tap on the button below to add a time range for $tabDay."
        tab_no_content_tv.text = defaultText
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list8, container, false)
    }

    private fun initListener() {
        mListener = object : OnFragmentInteractionListener{
            override fun onZoneRemoveClicked(scheduleTime: ZoneCheckTimeRange) {
                mScheduleHelper!!.RemoveTimeRangeToExistingZoneSettingRegular(zone, mTAbNumber!!, scheduleTime)
                processData()
                (parentFragment as ScheduleZoneTabFragment).updateButtonFromChildFragment()
            }

            override fun onZoneEditClicked(scheduleTime: ZoneCheckTimeRange) {
                (parentFragment as ScheduleZoneTabFragment).editCurrentTimeRange(scheduleTime)
            }
        }
    }

    private fun showNoContentView() {
        tab_no_content.visibility = View.VISIBLE
    }

    interface OnFragmentInteractionListener {
        fun onZoneRemoveClicked(scheduleTime: ZoneCheckTimeRange)
        fun onZoneEditClicked(scheduleTime: ZoneCheckTimeRange)
    }

    companion object {
        private val ARG_TAB = "tab-count"
        private val ARG_ZONE = "current-zone"

        fun newInstance(tab: Int, siteZone: SiteZone): ScheduleZoneDayFragment {
            val fragment = ScheduleZoneDayFragment()
            val args = Bundle()
            args.putInt(ARG_TAB, tab)
            args.putParcelable(ARG_ZONE, Parcels.wrap(siteZone))
            fragment.arguments = args
            return fragment
        }
    }
}
