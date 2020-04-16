package com.seachange.healthandsafty.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.seachange.healthandsafty.R

import com.seachange.healthandsafty.fragment.JSAMainFragment.OnListFragmentInteractionListener
import android.content.Context
import androidx.core.content.ContextCompat
import com.nostra13.universalimageloader.core.ImageLoader
import com.seachange.healthandsafty.model.JSAHazards


class JSARecyclerViewAdapter(private val mCtx: Context, private val mValues: List<JSAHazards>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<JSARecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues.get(position)
        holder.mIdView.text = holder.mItem!!.number.toString()
        holder.mTitle.text = holder.mItem!!.title
        holder.mContentView.text = holder.mItem!!.control

        val screenWidth = mCtx.resources.displayMetrics.widthPixels
        val height = (screenWidth-16) * 3/4 * 3/4
        holder.mMainImage.layoutParams.height = height

        ImageLoader.getInstance().displayImage(holder.mItem!!.contentImage +"&widthRatio=1.33", holder.mMainImage)
        ImageLoader.getInstance().displayImage(holder.mItem!!.riskImage, holder.mRiskImage)

        val riskName:String = holder.mItem!!.level.substring(0,1)
        holder.mLevel.text = riskName

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
//                    mListener!!.onListFragmentInteraction(holder.mItem)
        when (riskName) {
            "E" -> {
                holder.mLevel.setTextColor(ContextCompat.getColor(mCtx, R.color.sysWhite))
                holder.mLevel.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.sysBlack))
            }
            "H" -> {
                holder.mLevel.setTextColor(ContextCompat.getColor(mCtx, R.color.sysWhite))
                holder.mLevel.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.jsaRed))
            }
            "M" -> {
                holder.mLevel.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                holder.mLevel.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.jsaYellow))
            }
            "L" -> {
                holder.mLevel.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                holder.mLevel.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.jsaGreen))
            }
        }

        holder.mView.setOnClickListener {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
//                    mListener!!.onListFragmentInteraction(holder.mItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.findViewById<TextView>(R.id.jsa_content_id) as TextView
        val mTitle: TextView = mView.findViewById<TextView>(R.id.jsa_content_title) as TextView
        val mLevel: TextView = mView.findViewById<TextView>(R.id.jsa_content_level) as TextView
        val mContentView: TextView = mView.findViewById<TextView>(R.id.content) as TextView
        val mMainImage: ImageView = mView.findViewById<ImageView>(R.id.hazard_main_image) as ImageView
        val mRiskImage: ImageView = mView.findViewById<ImageView>(R.id.jsa_rick_image) as ImageView

        var mItem: JSAHazards? = null

    }
}
