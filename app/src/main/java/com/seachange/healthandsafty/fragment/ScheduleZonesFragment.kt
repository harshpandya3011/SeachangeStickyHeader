package com.seachange.healthandsafty.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.ZoneSchedulingActivity
import com.seachange.healthandsafty.adapter.ScheduleZonesAdapter
import com.seachange.healthandsafty.model.SiteZone
import kotlinx.android.synthetic.main.fragment_scheduled_zones.*


class ScheduleZonesFragment : Fragment() {

    private lateinit var mAdapter: ScheduleZonesAdapter
    private lateinit var mListener: OnZoneClickedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            (activity as AppCompatActivity).supportActionBar!!.title = "Select Zone To Schedule"

            mListener = object : OnZoneClickedListener {
                override fun onZoneClicked(item: SiteZone?) {
                    if (activity != null) {
                        (activity as ZoneSchedulingActivity).selectScheduleType(item!!)
                    }
                }
            }

        val list = ArrayList<SiteZone>()
        val tmp = SiteZone()
        tmp.zone_name ="Forecourt"

        val tmp1 = SiteZone()
        tmp1.zone_name ="Shop Floor"

        val tmp2 = SiteZone()
        tmp2.zone_name ="Store Area"
        list.add(tmp)
        list.add(tmp1)
        list.add(tmp2)

        mAdapter = ScheduleZonesAdapter(list, activity!!.applicationContext, mListener)
        schedule_zones_recyclyerview.adapter = mAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scheduled_zones, container, false)
    }

    interface OnZoneClickedListener {
        fun onZoneClicked(item: SiteZone?)
    }
}
