package com.seachange.healthandsafty.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.fragment.ScheduleZonesFragment.OnZoneClickedListener

class ScheduleZonesAdapter (private val mValues: List<SiteZone>,
                            private val mCtx: Context,
                            private val mListener: OnZoneClickedListener):RecyclerView.Adapter<ScheduleZonesAdapter.ViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.zone_schedule_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mArrow.typeface = SCApplication.FontMaterial()
        holder.mZoneName.text = item.zone_name

        holder.mView.setOnClickListener {
            if (null != mListener) {
                mListener.onZoneClicked(item)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mZoneName: TextView = mView.findViewById(R.id.zone_schedule_title_content) as TextView
        val mArrow: TextView = mView.findViewById(R.id.zone_schedule_check_icon) as TextView

    }
}
