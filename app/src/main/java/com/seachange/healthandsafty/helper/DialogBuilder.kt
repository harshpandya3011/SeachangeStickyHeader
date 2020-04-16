package com.seachange.healthandsafty.helper

import android.content.Context
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.interfacelistener.DialogListener

class DialogBuilder {

    var message: String ?= null
    var errorCode: Int ? = null
    var title: String ?= null
    var login:Boolean = false
    var cancelEnabled:Boolean = true
    var dialog:MaterialDialog ?=null

    constructor() {

    }

    constructor(code: Int) {
        errorCode = code
    }

    constructor(code: Int, loginReq:Boolean) {
        errorCode = code
        login = loginReq
    }

    constructor(code: Int, loginReq:Boolean, cancel:Boolean) {
        errorCode = code
        login = loginReq
        cancelEnabled = cancel
    }

    fun resetDialog() {
        if (dialog!=null && dialog!!.isShowing) dialog!!.dismiss()
    }

    fun showDialog(listener: DialogListener, mCtx: Context, error: VolleyError?) {

        if(error != null) {
            if (error is TimeoutError) {
                showNoConnectionDialog(listener, mCtx)
            }else {
                showNormalDialog(listener,mCtx)
            }
        } else {
            showNormalDialog(listener,mCtx)
        }
    }

    private fun showNormalDialog(listener: DialogListener, mCtx: Context) {
        var negButton = "Close"
        if (errorCode == 400 && login) {
            negButton = "Reset Password"
        }

        val builder = MaterialDialog.Builder(mCtx)
                .content(getErrorMessage())
                .titleColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .contentColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .positiveText("Try Again")
                .positiveColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .negativeColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .onPositive { dialog, which ->
                    listener.onDialogPositiveClicked()

                }

        if (cancelEnabled) {
            builder.negativeText(negButton)
                    .onNegative { dialog, which ->

                        if (errorCode == 400 && login) {
                            listener.onDialogResetPasswordClicked()
                        } else {
                            listener.onDialogNegativeClicked()
                        }
                    }
        }
        if (dialog!=null && dialog!!.isShowing) dialog!!.dismiss()
        dialog = builder.build()
        dialog!!.show()
    }

    fun showNoConnectionDialog(listener: DialogListener, mCtx: Context) {

        val title = mCtx.resources.getString(R.string.fa_time) + " Action Timed Out \n";
        val builder = MaterialDialog.Builder(mCtx)
                .title(title)
                .content("This is likely due to low connection strength. Please check your connection and try again.")
                .titleColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .contentColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .positiveText("Close")
                .typeface(SCApplication.FontMaterial(), null)
                .positiveColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .negativeColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
                .onPositive { dialog, which ->
                    listener.onDialogPositiveClicked()

                }


        if (dialog!=null && dialog!!.isShowing) dialog!!.dismiss()
        dialog = builder.build()
        dialog!!.show()
    }

    private fun getErrorMessage():String {

        val tmp:String

        when (errorCode) {

            500 -> {
                tmp = "Sorry, there was a error on our server. Please try again.\n" +
                        "If the problem persists please email us at customerservice@seachange-intl.com."
            }

            503 -> {
                tmp = "Sorry, the service is currently unavailable. Please try again.\n" +
                        "If the problem persists please email us at customerservice@seachange-intl.com."
            }

            403 -> {
                tmp = "Sorry, you do not have permissions to access this information. Please try a different request or contact your system administrator to change your permissions."
            }

            400 -> {
                if (login) {
                    tmp = "Sorry, we didn't recognise this email and password combination. Please check the details and try again."
                } else {
                    tmp = "Sorry, there was a issue on our server. Please try again.\n" +
                            "If the problem persists please email us at customerservice@seachange-intl.com."
                }
            }

            else -> {
                tmp = "Sorry, there was a issue on our server. Please try again.\n" +
                        "If the problem persists please email us at customerservice@seachange-intl.com."
            }
        }

        return tmp
    }
}
