package com.seachange.healthandsafty.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.model.ZoneHazard
import kotlinx.android.synthetic.main.found_hazard_item.view.*

class ZoneCheckAdapter(
        private val mValues: List<ZoneHazard>)
    : RecyclerView.Adapter<ZoneCheckAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.found_hazard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        val hazardType = item.hazardTypeV2 ?: item.hazardType
        holder.mName.text = hazardType?.type_name
        holder.mTitle.text = hazardType?.category

        holder.mTime.text = item.getFoundDateString()

    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mName: TextView = mView.check_hazard_name
        val mTitle: TextView = mView.check_hazard_type
        val mTime: TextView = mView.check_hazard_time

    }
}