package com.seachange.healthandsafty.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.pavlospt.CircleView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.fragment.RAHazardsBPFragment.OnListFragmentInteractionListener
import com.seachange.healthandsafty.model.RAHazards
import com.seachange.healthandsafty.model.RAHazardsRisk
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_ra_hazard_bp_lin.view.*
import kotlinx.android.synthetic.main.fragment_item5.view.*
import kotlinx.android.synthetic.main.ra_hazard_risks_layout.view.*


class RAHazardBPRecyclerViewAdapter(
        private val mCtx: Context,
        private val mValues: List<RAHazards>,
        private val mListener: OnListFragmentInteractionListener?,
        private val isHazard: Boolean)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val TYPE_HAZARD = 0
    private val TYPE_BP = 1

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as RAHazards
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HAZARD) {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.frag_ra_hazard_bp_lin, parent, false)
            HazardViewHolder(view)
        } else {

            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_item5, parent, false)
            BPViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = mValues[position]

        if (isHazard){
            val mViewHolder = holder as HazardViewHolder
            mViewHolder.mIconView.typeface = SCApplication.FontMaterial()
            mViewHolder.mTitle.text =  item.description
            mViewHolder.mSubTitle.text =  item.reference + " | " + item.publishStatus!!.label + " | " + item.status!!.label
            Picasso.get().load(item.primaryImageUrl).into(mViewHolder.mImage)

            mViewHolder.mLevelView.setShowSubtitle(false)
            mViewHolder.mLevelView.titleText =  item.riskRating!!.abbreviation
            mViewHolder.mLevelView.fillColor = Integer.parseInt(item.riskRating!!.color!!.substring(1), 16) + -0x1000000

            mViewHolder.mControl.setShowSubtitle(false)
            mViewHolder.mControl.titleText =  item.numOfNewControls.toString()

            for (i in 0..item.risks!!.size-1) {
                mViewHolder.mLin.addView(getRiskView(item.risks!![i]))
            }
        } else {
            val mViewHolder = holder as BPViewHolder

            val item = mValues[position]
            mViewHolder.mIconView.typeface = SCApplication.FontMaterial()

            with(mViewHolder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }
    }

    private fun getRiskView(item: RAHazardsRisk):View {
        val v = LayoutInflater.from(mCtx).inflate(R.layout.ra_hazard_risks_layout, null)
        val mIcon: ImageView = v.risk_hazard_item_image
        val mTitle: TextView = v.risk_hazard_item_title
        mTitle.text = item.label
        Picasso.get().load(item.iconUrl).into(mIcon)

        return v
    }

    override fun getItemViewType(position: Int): Int {
        if(isHazard) return TYPE_HAZARD
        else return TYPE_BP
    }

    override fun getItemCount(): Int = mValues.size

    inner class HazardViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIconView: TextView = mView.ra_alert
        val mTitle: TextView = mView.ra_hazard_title
        val mSubTitle: TextView = mView.ra_hazard_sub_title_label
        val mImage: ImageView = mView.ra_hazard_main_image
        val mLin: LinearLayout = mView.ra_main_hazard_risk_lin
        val mLevelView: CircleView = mView.ra_hazard_level_back
        val mControl: CircleView = mView.ra_hazard_control

    }

    inner class BPViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIconView: TextView = mView.ra_badge_checked
    }
}
