package com.seachange.healthandsafty.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.fragment.IrregularScheduleFragment.OnListFragmentInteractionListener
import com.seachange.healthandsafty.fragment.dummy.DummyContent.DummyItem

import kotlinx.android.synthetic.main.fragment_item7.view.*

class IrregularScheduleRecyclerViewAdapter(
        private val mValues: List<DummyItem>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<IrregularScheduleRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyItem
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item7, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        if (position ==0) {
            holder.mIdView.text = "08:00 - 14:00"
            holder.mContentView.text = "30m"
        } else if(position ==1) {
            holder.mIdView.text = "14:00 - 18:00"
            holder.mContentView.text = "1h"
        } else if (position == 2) {
            holder.mIdView.text = "20:00 - 03:00(Tue)"
            holder.mContentView.text = "30m"
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

//    override fun getItemCount(): Int = mValues.size
override fun getItemCount(): Int = 3

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content
    }
}
