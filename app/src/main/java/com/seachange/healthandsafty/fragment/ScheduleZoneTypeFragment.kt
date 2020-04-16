package com.seachange.healthandsafty.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.ZoneSchedulingActivity
import com.seachange.healthandsafty.model.SiteZone
import kotlinx.android.synthetic.main.fragment_scheduled_zones2.*


class ScheduleZoneTypeFragment : Fragment() {

    private var zone:SiteZone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initButtons() {
        regular_schedule_option.setOnClickListener {
            if (activity != null) {
                (activity as ZoneSchedulingActivity).selectZoneTime(zone!!, false)
            }
        }

        irregular_schedule_option.setOnClickListener {
            if (activity != null) {
                (activity as ZoneSchedulingActivity).irregularCalendarView(zone!!)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scheduled_zones2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.title = "Schedule Type"
        initButtons()
    }

    fun initZone(selectZone: SiteZone) {
        zone = selectZone
    }
}
