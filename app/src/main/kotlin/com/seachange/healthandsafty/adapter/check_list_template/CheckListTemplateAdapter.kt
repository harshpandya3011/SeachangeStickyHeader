package com.seachange.healthandsafty.adapter.check_list_template

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.model.check_list_type.CheckListTypeData
import kotlinx.android.synthetic.main.item_check_list_type.view.*
import kotlinx.android.synthetic.main.item_health_category.view.txtCategoryName


class CheckListTemplateAdapter(
        private var checkListType: ArrayList<CheckListTypeData>,
        var onItemSelected: OnItemSelected,
        var context: Context)
    : RecyclerView.Adapter<CheckListTemplateAdapter.ViewHolder>() {

    fun setCheckList(checkListType: ArrayList<CheckListTypeData>) {
        this.checkListType = checkListType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_check_list_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val parentObj = checkListType[position]
        viewHolder.txtCategoryName.text = parentObj.title
        if (checkListType[position].selected == true) {
            viewHolder.imgDone.setColorFilter(ContextCompat.getColor(context, R.color.sysBlack))
            viewHolder.relMainLayout.setBackgroundResource(R.color.appTabGrey)
        } else {
            viewHolder.imgDone.setColorFilter(ContextCompat.getColor(context, R.color.iconGrey))
            viewHolder.relMainLayout.setBackgroundResource(R.color.sysWhite)
        }
        viewHolder.imgDone.setOnClickListener {
            onItemSelected.onItemSelected(it, position, checkListType)
        }

        viewHolder.relMainLayout.setOnClickListener {
            onItemSelected.onItemSelected(it, position, checkListType)
        }

    }

    override fun getItemCount(): Int = checkListType.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val txtCategoryName: TextView = mView.txtCategoryName
        val relMainLayout: RelativeLayout = mView.relMainLayout
        val imgDone: ImageView = mView.imgDone
    }

    interface OnItemSelected {
        fun onItemSelected(view: View, position: Int, checkListType: ArrayList<CheckListTypeData>)
    }
}