package com.seachange.healthandsafty.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.ScheduleZoneDayFragment.OnFragmentInteractionListener
import com.seachange.healthandsafty.model.ZoneCheckTimeRange
import com.seachange.healthandsafty.utils.DateUtil
import kotlinx.android.synthetic.main.fragment_item8.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ScheduleZoneDayRecyclerViewAdapter(
        private val mValues: List<ZoneCheckTimeRange>,
        private val mCtx: Context,
        private val mCurrentTab: Int,
        private val mListener: OnFragmentInteractionListener?)
    : RecyclerView.Adapter<ScheduleZoneDayRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item8, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mTVRemove.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                AppCompatResources.getDrawable(mCtx, R.drawable.ic_delete_white_24dp),
                null,
                null
        )

        holder.mTvEdit.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                AppCompatResources.getDrawable(mCtx, R.drawable.ic_edit_black_24dp),
                null,
                null
        )

        var timeString = getTimeString(item.startTime) + " - " + getTimeString(item.endTime)
        if (item.isEndTimeIsNextDay) {
            timeString =  timeString + "(" + DateUtil().getDaySubString(mCurrentTab) + ")"
        }

        holder.mIdView.text = timeString

        if (item.intervalInMinutes>59) {
            holder.mContentView.text = getHourFrequency(item.intervalInMinutes).toString() + "h"
        } else {
            holder.mContentView.text = item.intervalInMinutes.toString() + "m"
        }

        holder.mTVRemove.setOnClickListener {
            mListener?.onZoneRemoveClicked(item)
        }

        holder.mTvEdit.setOnClickListener {
            mListener?.onZoneEditClicked(item)
        }

    }

    private fun getTimeString(timeString: String):String {
        var time = ""
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val mFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            val date = format.parse(timeString)
            time = mFormat.format(date)
        }catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return time
    }

    private fun getHourFrequency(minutes: Int):Int {
        return minutes / 60
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_time
        val mContentView: TextView = mView.content
        val mTVRemove: TextView =  mView.tv_remove_schedule
        val mTvEdit: TextView = mView.tv_edit_schedule
    }
}
