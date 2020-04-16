package com.seachange.healthandsafty.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.ZoneSchedulingActivity
import com.seachange.healthandsafty.adapter.IrregularScheduleRecyclerViewAdapter
import com.seachange.healthandsafty.fragment.dummy.DummyContent
import com.seachange.healthandsafty.fragment.dummy.DummyContent.DummyItem
import com.seachange.healthandsafty.model.SiteZone
import kotlinx.android.synthetic.main.fragment_item_list7.*

class IrregularScheduleFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private var zone:SiteZone? = null
    private lateinit var mAdapter: IrregularScheduleRecyclerViewAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.title = zone!!.zone_name

        listener = object : OnListFragmentInteractionListener {
            override fun onListFragmentInteraction(item: DummyItem?) {
//                (activity as ZoneSchedulingActivity).selectTimeRangeSetUp(zone!!)
            }
        }

        mAdapter = IrregularScheduleRecyclerViewAdapter(DummyContent.ITEMS, listener)
        irregular_list.adapter = mAdapter

        fab_scheduled_zone.setOnClickListener {
//            (activity as ZoneSchedulingActivity).selectTimeRangeSetUp(zone!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list7, container, false)
    }

    fun initZone(selectZone: SiteZone) {
        zone = selectZone
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: DummyItem?)
    }
}
