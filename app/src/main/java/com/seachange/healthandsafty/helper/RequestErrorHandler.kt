package com.seachange.healthandsafty.helper

import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.seachange.healthandsafty.activity.BaseActivity
import com.seachange.healthandsafty.helper.View.RequestErrorHandlerView
import com.seachange.healthandsafty.helper.interfacelistener.DialogListener

class RequestErrorHandler{

    var dialogBuilder:DialogBuilder ?= null
    private var mView: RequestErrorHandlerView ?= null
    private var activity:BaseActivity ?= null
    private var cancelButtonEnabled:Boolean?= null
    private var loginRequest:Boolean ? =null
    private var dialogShowing: Boolean = false

    constructor() {

    }

    constructor(mView: RequestErrorHandlerView, activity:BaseActivity, cancelButtonEnabled:Boolean, loginRequest:Boolean) {
        this.mView = mView
        this.activity = activity
        this.cancelButtonEnabled = cancelButtonEnabled
        this.loginRequest = loginRequest
    }

    fun onErrorResponse(error: VolleyError?) {

        var statusCode = 0
        if (error !=null) {
            if (error.networkResponse!=null) {
                statusCode = error.networkResponse.statusCode
            }
        }
        when (error) {
            null -> showErrorDialog(0, error)
            is NoConnectionError -> {
                activity!!.showNoConnectionDialog()
            }
            is TimeoutError -> {
                showTimeOutErrorDialog(statusCode)
                Logger.info("STATUS CODE: " + "time out called")

            }
            is AuthFailureError -> {
                activity!!.viewLogin()
            }
            else -> {
                showErrorDialog(statusCode, error)
            }
        }
    }

    private fun showErrorDialog(statusCode:Int, error: VolleyError?){

        val dialogListener = object : DialogListener {
            override fun onDialogPositiveClicked() {
                dialogShowing = false
                 mView!!.errorDialogPositiveClicked()
            }

            override fun onDialogNegativeClicked() {
                dialogShowing = false

            }

            override fun onDialogResetPasswordClicked() {
                dialogShowing = false
            }
        }

        if (dialogShowing) return
        dialogBuilder = DialogBuilder(statusCode, loginRequest!!, cancelButtonEnabled!!)
        dialogBuilder!!.showDialog(dialogListener, activity!!, error)
        dialogShowing = true
    }

    private fun showTimeOutErrorDialog(statusCode:Int){

        val dialogListener = object : DialogListener {
            override fun onDialogPositiveClicked() {
                dialogShowing = false
                mView!!.errorDialogPositiveClicked()
            }

            override fun onDialogNegativeClicked() {
                dialogShowing = false

            }

            override fun onDialogResetPasswordClicked() {
                dialogShowing = false
            }
        }

        if (dialogShowing) return
        dialogBuilder = DialogBuilder(statusCode, loginRequest!!, cancelButtonEnabled!!)
        dialogBuilder!!.showNoConnectionDialog(dialogListener, activity!!)
        dialogShowing = true
    }
}