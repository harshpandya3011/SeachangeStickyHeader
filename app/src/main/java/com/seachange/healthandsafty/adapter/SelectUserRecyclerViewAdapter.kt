package com.seachange.healthandsafty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.model.UserData
import kotlinx.android.synthetic.main.fragment_item.view.*

class SelectUserRecyclerViewAdapter(
        items: List<UserData>?,
        private val itemClickedListener: (item: UserData) -> Unit
) : RecyclerView.Adapter<SelectUserRecyclerViewAdapter.ViewHolder>() {

    var items = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items?.get(position), itemClickedListener)
    }

    override fun getItemCount() = items?.count() ?: 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: UserData?, itemClickedListener: (item: UserData) -> Unit) {

            itemView.tv_user_full_name.text = item?.fullName
            itemView.tv_user_initials.text = item?.initials
                    ?: listOfNotNull(item?.firstName?.firstOrNull(), item?.lastName?.firstOrNull()).joinToString(separator = "")

            itemView.tv_user_role.text = item?.userRoleLabel?.let { itemView.context.getString(it) }
            if (item != null) {
                itemView.setOnClickListener {
                    itemClickedListener(item)
                }
            }
        }
    }
}
