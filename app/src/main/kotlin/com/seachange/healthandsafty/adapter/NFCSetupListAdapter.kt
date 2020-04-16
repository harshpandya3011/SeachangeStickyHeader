package com.seachange.healthandsafty.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.model.SiteZone
import com.seachange.healthandsafty.nfc.ui.NFCSetUpActivity.OnSelectedZoneListener
import kotlinx.android.synthetic.main.nfc_setup_list_item.view.*
import kotlinx.android.synthetic.main.nfc_setup_list_item.view.a_tag
import kotlinx.android.synthetic.main.nfc_setup_list_item.view.a_tag_icon
import kotlinx.android.synthetic.main.nfc_setup_list_item.view.b_tag
import kotlinx.android.synthetic.main.nfc_setup_list_item.view.b_tag_icon

class NFCSetupListAdapter (
        private val mValues: List<SiteZone>,
        private val mCtx: Context,
        private val mListener: OnSelectedZoneListener)
    : RecyclerView.Adapter<NFCSetupListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.nfc_setup_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        holder.mATagIcon.typeface = SCApplication.FontMaterial()
        holder.mBTagIcon.typeface = SCApplication.FontMaterial()
        holder.mCheckIcon.typeface = SCApplication.FontMaterial()
        holder.mTickIcon.typeface = SCApplication.FontMaterial()
        holder.mCloudOff.typeface = SCApplication.FontMaterial()

        holder.mTitle.text = item.zone_name

        if (item.isTagSetup) {
            holder.mTickIcon.visibility = View.VISIBLE
            holder.mCloudOffImage.setImageResource(R.drawable.ic_cloud_done)
        } else if(item.isNfcPointASet && item.isNfcPointBSet) {
            holder.mTickIcon.visibility = View.VISIBLE
            holder.mCloudOff.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
            holder.mCloudOffImage.setImageResource(R.drawable.ic_cloud_unsync)
        } else {
            holder.mTickIcon.visibility = View.GONE
            holder.mCloudOff.setTextColor(ContextCompat.getColor(mCtx, R.color.alertContent))
            holder.mCloudOffImage.setImageResource(R.drawable.ic_cloud_unsync_unset)
        }

        if(item.isNfcPointASet || item.isTagSetup) {
            holder.mATag.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
            holder.mATagIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
        }

        if(item.isNfcPointBSet || item.isTagSetup) {
            holder.mBTag.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
            holder.mBTagIcon.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
        }

        holder.mView.setOnClickListener{
            mListener.onSelectUserInteraction(item)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitle: TextView = mView.nfc_setup_zone_title_content
        val mATag: TextView = mView.a_tag
        val mATagIcon: TextView = mView.a_tag_icon
        val mBTag: TextView = mView.b_tag
        val mBTagIcon:TextView = mView.b_tag_icon
        val mCheckIcon:TextView = mView.nfc_setup_check_icon
        val mTickIcon:TextView = mView.nfc_setup_tick_icon
        val mCloudOff:TextView = mView.nfc_setup_cloud
        val mCloudOffImage:ImageView = mView.nfc_setup_cloud_image
    }
}