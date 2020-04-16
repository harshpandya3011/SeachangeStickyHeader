package com.seachange.healthandsafty.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.ManageScheduleMainFragment.OnFragmentInteractionListener

import com.seachange.healthandsafty.model.SiteZone
import kotlinx.android.synthetic.main.schedule_main_item.view.*

class ScheduleZoneRecyclerViewAdapter(
        private val mValues: List<SiteZone>,
        private val mCtx: Context,
        private val scheduled: Boolean,
        private val mListener: OnFragmentInteractionListener?)
    : RecyclerView.Adapter<ScheduleZoneRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.schedule_main_item, parent, false)
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

        holder.mTvEditOrAdd.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                AppCompatResources.getDrawable(mCtx, R.drawable.ic_edit_black_24dp),
                null,
                null
        )

        if(scheduled) {
            holder.mTVRemove.visibility = View.VISIBLE
            holder.mTvEditOrAdd.text = mCtx.getString(R.string.edit_tv)
            //test data
            //set regular and irregular schedules
            if (item.hasRegularSchedule !=null && item.hasRegularSchedule) {
                holder.mIdView.text = item.zone_name
                holder.mContentView.text = mCtx.getString(R.string.regular_schedule)
            } else {
                holder.mIdView.text = item.zone_name
                holder.mContentView.text = mCtx.getString(R.string.irregular_schedule)
            }
        } else {
            //not scheduled, hide remove item.
            //reset edit text
            //set not scheduled text
            holder.mTVRemove.visibility = View.GONE
            holder.mTvEditOrAdd.text = mCtx.getString(R.string.add_tv)
            holder.mContentView.text = mCtx.getString(R.string.not_scheduled)
        }

        holder.mIdView.text = item.zone_name
        holder.mTVRemove.setOnClickListener {
            mListener?.onZoneRemoveClicked(item)
        }

        if (scheduled) {
            holder.mTvEditOrAdd.setOnClickListener {
                mListener?.onZoneEditClicked(item)
            }
        } else {
            holder.mTvEditOrAdd.setOnClickListener {
                mListener?.onZoneAddClicked(item)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.scheduled_zone_title_content
        val mContentView: TextView = mView.scheduled_zone_title_sub_content
        val mTVRemove: TextView =  mView.tv_remove_schedule
        val mTvEditOrAdd: TextView = mView.tv_edit_schedule
    }
}
