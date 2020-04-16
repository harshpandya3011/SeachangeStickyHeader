package com.seachange.healthandsafty.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.model.UserData
import kotlinx.android.synthetic.main.item_manage_user.view.*

class ManageUsersRecyclerAdapter(private val clickListener: (clickedView: View, item: UserData, adapterPosition: Int) -> Unit) : ListAdapter<UserData, ManageUsersRecyclerAdapter.ManageUsersViewHolder>(USERS_DIFF_CALLBACK) {

    companion object {

        const val ARG_FULL_NAME = "ARG_FULL_NAME"
        const val ARG_IS_PASS_CODE_ONLY = "ARG_IS_PASS_CODE_ONLY"
        const val ARG_INITIALS = "ARG_INITIALS"
        const val ARG_USER_ROLE_LABEL = "ARG_USER_ROLE_LABEL"
        const val ARG_TEMPORARY_PASS_CODE = "ARG_TEMPORARY_PASS_CODE"

        val USERS_DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserData>() {
            override fun areItemsTheSame(oldItem: UserData, newItem: UserData) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
                return oldItem.fullName == newItem.fullName
                        && oldItem.initials == newItem.initials
                        && oldItem.isPasscodeOnly == newItem.isPasscodeOnly
                        && oldItem.userRole == newItem.userRole
                        && oldItem.temporaryPasscode == newItem.temporaryPasscode
            }

            override fun getChangePayload(oldItem: UserData, newItem: UserData): Any? {
                val diffBundle = Bundle()
                if (oldItem.fullName != newItem.fullName) {
                    diffBundle.putString(ARG_FULL_NAME, newItem.fullName)
                }
                if (oldItem.initials != newItem.initials) {
                    diffBundle.putString(ARG_INITIALS, newItem.initials)
                }
                if (oldItem.isPasscodeOnly != newItem.isPasscodeOnly && newItem.isPasscodeOnly != null) {
                    diffBundle.putBoolean(ARG_IS_PASS_CODE_ONLY, newItem.isPasscodeOnly)
                }
                if (oldItem.userRole != newItem.userRole && newItem.userRole != null) {
                    diffBundle.putInt(ARG_USER_ROLE_LABEL, newItem.userRoleLabel
                            ?: R.string.not_assigned)
                }
                if (oldItem.temporaryPasscode != newItem.temporaryPasscode && newItem.temporaryPasscode != null) {
                    diffBundle.putString(ARG_TEMPORARY_PASS_CODE, newItem.temporaryPasscode)
                }
                return if (diffBundle.isEmpty) null else diffBundle
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageUsersViewHolder {
        return ManageUsersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_manage_user, parent, false))
    }

    override fun onBindViewHolder(holder: ManageUsersViewHolder, position: Int) {
        holder.bind(getItem(position)) { clickedView, adapterPosition ->
            clickListener(clickedView, getItem(adapterPosition), adapterPosition)
        }
    }

    override fun onBindViewHolder(holder: ManageUsersViewHolder, position: Int, payloads: MutableList<Any>) {
        val bundle = payloads.firstOrNull()?.takeIf { it is Bundle && !it.isEmpty } as Bundle?

        if (bundle != null) {
            holder.bind(getItem(position), bundle) { clickedView, adapterPosition ->
                clickListener(clickedView, getItem(adapterPosition), adapterPosition)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class ManageUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.tv_remove_user.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(itemView.context, R.drawable.ic_delete_white_24dp),
                    null,
                    null
            )
            itemView.tv_edit_user.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(itemView.context, R.drawable.ic_edit_black_24dp),
                    null,
                    null
            )
            itemView.tv_reset_passcode.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    AppCompatResources.getDrawable(itemView.context, R.drawable.ic_lock_white_24dp),
                    null,
                    null
            )
        }

        fun bind(item: UserData?, clickListener: (clickedView: View, adapterPosition: Int) -> Unit) {
            itemView.tv_user_initials.text = item?.initials
            itemView.tv_user_full_name.text = item?.fullName
            itemView.tv_user_role_temp_pass_code.text = listOfNotNull(
                    item?.userRoleLabel?.let {
                        itemView.context.getString(it)
                    }, item?.temporaryPasscode?.let {
                itemView.context.getString(R.string.temp_value, it)
            }
            ).joinToString("  |  ")

            itemView.iv_user_type.setImageResource(
                    if (item?.isPasscodeOnly == true) {
                        R.drawable.ic_phone_android_black_24dp
                    } else {
                        R.drawable.ic_devices_black_24dp
                    }
            )

            val viewClickListener: (View) -> Unit = {
                clickListener(it, adapterPosition)
            }
            itemView.tv_remove_user.setOnClickListener(viewClickListener)
            itemView.tv_edit_user.setOnClickListener(viewClickListener)
            itemView.tv_reset_passcode.setOnClickListener(viewClickListener)
        }

        fun bind(item: UserData?, bundle: Bundle, clickListener: (clickedView: View, adapterPosition: Int) -> Unit) {
            if (bundle.containsKey(ARG_INITIALS)) {
                itemView.tv_user_initials.text = bundle.getString(ARG_INITIALS)
            }
            if (bundle.containsKey(ARG_FULL_NAME)) {
                itemView.tv_user_full_name.text = bundle.getString(ARG_FULL_NAME)
            }
            //TODO temp pass code bind


            itemView.tv_user_role_temp_pass_code.text = listOfNotNull(if (bundle.containsKey(ARG_USER_ROLE_LABEL)) {
                itemView.context.getString(bundle.getInt(ARG_USER_ROLE_LABEL, R.string.not_assigned))
            } else {
                item?.userRoleLabel?.let {
                    itemView.context.getString(it)
                }
            },
                    if (bundle.containsKey(ARG_TEMPORARY_PASS_CODE)) {
                        itemView.context.getString(R.string.temp_value, bundle.getString(ARG_TEMPORARY_PASS_CODE))
                    } else {
                        item?.temporaryPasscode?.let {
                            itemView.context.getString(R.string.temp_value, it)
                        }
                    }
            ).joinToString("  |  ")

            itemView.iv_user_type.setImageResource(
                    if (bundle.getBoolean(ARG_IS_PASS_CODE_ONLY)) {
                        R.drawable.ic_phone_android_black_24dp
                    } else {
                        R.drawable.ic_devices_black_24dp
                    }
            )

            val viewClickListener: (View) -> Unit = {
                clickListener(it, adapterPosition)
            }
            itemView.tv_remove_user.setOnClickListener(viewClickListener)
            itemView.tv_edit_user.setOnClickListener(viewClickListener)
            itemView.tv_reset_passcode.setOnClickListener(viewClickListener)
        }
    }
}