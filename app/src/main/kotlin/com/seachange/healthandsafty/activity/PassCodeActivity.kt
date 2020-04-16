package com.seachange.healthandsafty.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.afollestad.materialdialogs.MaterialDialog
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.UserDateHelper
import com.seachange.healthandsafty.model.UserData
import com.seachange.healthandsafty.presenter.PassCodePresenter
import com.seachange.healthandsafty.utills.hideSoftInput
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.PassCodeView
import com.seachange.healthandsafty.viewmodel.ManageUsersViewModel
import kotlinx.android.synthetic.main.activity_pass_code.*
import org.parceler.Parcels
import java.net.HttpURLConnection

class PassCodeActivity : BaseActivity(), PassCodeView {

    private val manageUsersViewModel by viewModels<ManageUsersViewModel>()

    private lateinit var user: UserData
    private lateinit var presenter: PassCodePresenter
    private var newPassCode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code)
        supportActionBar?.hide()

        manageUsersViewModel.progressResetPasscodeLiveData.observe(this) {
            progress_reset_pass_code.visibility = if (it == true) View.VISIBLE else View.GONE
        }
        initViewModel()

        loadDB = false
        passcode_close.text = mCtx.getString(R.string.fa_close_circle)
        passcode_close.typeface = SCApplication.FontMaterial()
        pinView.setAnimationEnable(true)
        user = Parcels.unwrap<UserData>(intent.getParcelableExtra(UtilStrings.OBJECT_USER))
        presenter = PassCodePresenter(this)

        newPassCode = intent.getBooleanExtra(UtilStrings.NEW_PASSCODE, false)

        if (newPassCode) {
            passcode_top.visibility = View.INVISIBLE
            passcode_logo.visibility = View.INVISIBLE
        }

        initView()
        if (UserDateHelper(mCtx).userLoggedIn) {
            UserDateHelper(mCtx).resetUserLoggedInDetails()
        }
    }

    private fun initViewModel(){
        manageUsersViewModel.errorLiveData.observe(this) {
            if (it == null) return@observe
            pinView.isEnabled = true
            when (it) {
                is NoConnectionError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.no_connection_error_title)
                            .setMessage(R.string.no_connection_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_cloud_off_black_24dp)
                            .show()
                }
                is ServerError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.server_error_title)
                            .setMessage(R.string.server_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_mood_bad_black_24dp)
                            .show()
                }
                is TimeoutError -> {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.timeout_error_title)
                            .setMessage(R.string.timeout_error)
                            .setPositiveButton(R.string.close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setIcon(R.drawable.ic_access_time_black_24dp)
                            .show()
                }
                is AuthFailureError -> {
                    if (it.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                        AlertDialog.Builder(this)
                                .setTitle(R.string.permission_denied_error_title)
                                .setMessage(R.string.permission_denied_error)
                                .setPositiveButton(R.string.close) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setIcon(R.drawable.ic_block_black_24dp)
                                .show()
                    }
                }
            }
        }

        manageUsersViewModel.resetPassCodeSuccessLiveData.observe(this, Observer {
            Logger.info("information 123")
            if (it == null) return@Observer

            user = it
            updatePassCodePopup()
        })
    }

    private fun initView() {
        passcode_close.setOnClickListener {
            this.finish()
            pinView.hideSoftInput()
        }

        pinView.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.count() == 4) {
                    if (!newPassCode) {
                        Handler().postDelayed({
                            presenter.validatePassCode(user.hashedPasscode, s.toString(), user, connected)
                        }, 100)
                    }
                }
            }
        })

        pinView.setOnEditorActionListener { v, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                val passCode = pinView.text?.toString()
                val tenantId = user.tenantId
                if (newPassCode && passCode != null && tenantId != null) {
                    v.isEnabled = false
                    manageUsersViewModel.resetPasscode(user, tenantId, passCode)
                }
            }
            false
        }

        pinView.requestFocus()
        passcode_title.text = getString(
                if (newPassCode) {
                    R.string.value_enter_new_pass_code
                } else {
                    R.string.enter_your_pass_code_value
                },
                user.firstName.orEmpty()
        )

        progress_reset_pass_code.visibility = if (manageUsersViewModel.progressResetPasscodeLiveData.value == true) View.VISIBLE else View.GONE
    }

    override fun onTempPassCode() {
        newPassCodePopup()
    }

    private fun newPassCodePopup() {
        MaterialDialog.Builder(this)
                .content(R.string.msg_entered_temp_passcode)
                .title(R.string.new_passcode)
                .titleColorRes(R.color.alertTitle)
                .contentColorRes(R.color.alertContent)
                .positiveText(R.string.proceed)
                .positiveColorRes(R.color.colorDefaultYellow)
                .onPositive { dialog, which ->
                    dialog.dismiss()
                    enterUpdatePassCodeScreen()
                }
                .show()
    }

    private fun updatePassCodePopup() {
        MaterialDialog.Builder(this)
                .content(R.string.successfully_set_up_passcode)
                .title(R.string.well_done)
                .titleColorRes(R.color.alertTitle)
                .contentColorRes(R.color.alertContent)
                .positiveText(R.string.proceed)
                .positiveColorRes(R.color.colorDefaultYellow)
                .onPositive { dialog, which ->
                    dialog.dismiss()
                    onValidSuccessful()
                }
                .show()
    }

    private fun enterUpdatePassCodeScreen() {
        val intent = Intent(mCtx, PassCodeActivity::class.java)
        intent.putExtra(UtilStrings.MANAGER_HOME,  user.userRole == UserData.USER_ROLE_MANAGER)
        intent.putExtra(UtilStrings.NEW_PASSCODE, true)
        intent.putExtra(UtilStrings.OBJECT_USER, Parcels.wrap<UserData>(user))
        startActivity(intent)
        this.finish()
    }

    override fun onValidSuccessful() {
        //save logged in details, will be used for requests...
        UserDateHelper(mCtx).saveUserLoggedInDetail(user.id.toString(), user.hashedPasscode)
        val intent = Intent(this, CaygoHomeActivity::class.java)
        intent.putExtra(UtilStrings.OBJECT_USER, Parcels.wrap<UserData>(user))
        intent.putExtra(UtilStrings.MANAGER_HOME,  user.userRole == UserData.USER_ROLE_MANAGER)
        startActivity(intent)
        pinView.hideSoftInput()
    }

    override fun onValidFailed() {
        Snackbar.make(
                passcode_parent_view,
                getString(R.string.please_try_again_value, user.firstName.orEmpty()),
                Snackbar.LENGTH_LONG
        ).show()
        YoYo.with(Techniques.Shake)
                .duration(1000)
                .repeat(0)
                .onEnd {
                    (pinView.text?.clear())
                }
                .playOn(findViewById(R.id.pinView))
    }

    override fun onStop() {
        super.onStop()
        pinView.hideSoftInput()
    }
}
