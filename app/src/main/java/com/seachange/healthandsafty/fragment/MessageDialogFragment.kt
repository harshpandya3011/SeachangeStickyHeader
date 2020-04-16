package com.seachange.healthandsafty.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * Created by kevinsong on 08/01/2018.
 */


class MessageDialogFragment : DialogFragment() {

    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mListener: MessageDialogListener? = null

    interface MessageDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)

    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(mMessage)
                .setTitle(mTitle)

        builder.setPositiveButton("OK") { dialog, id ->
            if (mListener != null) {
                mListener!!.onDialogPositiveClick(this@MessageDialogFragment)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, id ->
            if (mListener != null) {
                mListener!!.onDialogNegativeClick(this@MessageDialogFragment)
            }
        }

        return builder.create()
    }

    companion object {

        fun newInstance(title: String, message: String, listener: MessageDialogListener): MessageDialogFragment {
            val fragment = MessageDialogFragment()
            fragment.mTitle = title
            fragment.mMessage = message
            fragment.mListener = listener
            return fragment
        }
    }
}
