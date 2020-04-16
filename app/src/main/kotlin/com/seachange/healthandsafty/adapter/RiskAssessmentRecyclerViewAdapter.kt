package com.seachange.healthandsafty.adapter

import androidx.recyclerview.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.seachange.healthandsafty.R


import com.seachange.healthandsafty.fragment.RiskAssessmentFragment.OnListFragmentInteractionListener
import com.seachange.healthandsafty.model.RiskAssement
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_risk_assessment.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RiskAssessmentRecyclerViewAdapter(
        private val mValues: List<RiskAssement>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<RiskAssessmentRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as RiskAssement
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_risk_assessment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mTitle.text =  item.site!!.name
        holder.mProgress.text = item.status!!.label
        holder.mSite.text = "Full Site"
        Picasso.get().load(item.site!!.imageUrl).into(holder.mIcon)

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val sdf2 = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        var formatedDate = ""
        val today = DateUtils.isToday(calendar.timeInMillis)

        try {
            val date = dateFormat.parse(item.dateScheduled)
            if(today) {
                formatedDate = "Today, " + sdf.format(date)
            } else {
                formatedDate = sdf2.format(date)
            }
            calendar.time = date
        }catch (e: ParseException){
            e.printStackTrace()
        }

        holder.mDate.text = formatedDate
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitle: TextView = mView.ra_title_content
        val mDate: TextView = mView.ra_date
        val mProgress: TextView = mView.ra_status
        val mSite: TextView = mView.ra_site
        val mIcon: ImageView = mView.ra_icon

//        val mContentView: TextView = mView.content

    }
}
