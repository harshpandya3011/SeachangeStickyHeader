package com.seachange.healthandsafty.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.LayoutInflater.*
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication

import com.seachange.healthandsafty.fragment.RAHazardsFragment.OnListFragmentInteractionListener
import com.seachange.healthandsafty.model.RiskCategory

import kotlinx.android.synthetic.main.fragment_item4.view.*


class RAHazardsRecyclerViewAdapter(
        private val mCtx: Context,
        private val mValues: List<RiskCategory>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<RAHazardsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = from(parent.context)
                .inflate(R.layout.fragment_item4, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.title
        holder.mContentView.text = item.subTitle
        holder.mCheckView.typeface = SCApplication.FontMaterial()

        if (item.selected) {
            holder.mView.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.buttonDisableGrey))
            holder.mCheckView.visibility = View.VISIBLE
        } else {
            holder.mView.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.sysWhite))
            holder.mCheckView.visibility = View.GONE
        }

        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(item, position)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mCheckView: TextView = mView.ra_check_icon

        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
