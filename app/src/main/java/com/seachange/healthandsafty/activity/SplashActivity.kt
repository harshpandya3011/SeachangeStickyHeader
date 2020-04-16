package com.seachange.healthandsafty.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.helper.UserDateHelper
import com.seachange.healthandsafty.presenter.SplashPresenter
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.RefreshView
import com.seachange.healthandsafty.view.SplashView
import com.seachange.healthandsafty.viewmodel.ManageUsersViewModel
import org.json.JSONObject
import android.os.CountDownTimer
import com.microsoft.appcenter.analytics.Analytics


class SplashActivity : BaseActivity(), SplashView, RefreshView {

    private val manageUsersViewModel by viewModels<ManageUsersViewModel>()
    private var mPresenter: SplashPresenter? = null
    private var screenLive = true
    private var mCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setChildActivity(this)
        supportActionBar!!.hide()
        loadDB = false
        mPresenter = SplashPresenter(this, mCtx)
        screenLive = true
        if (checkTokenShowLogin()) {
            screenLive = false
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            intent.putExtra(UtilStrings.MANAGER_HOME, true)
            startActivity(intent)
            Logger.info("splash screen, no token found")
        } else {
            if (PreferenceHelper.getInstance(mCtx).refreshToken == null) {
                screenLive = false
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                intent.putExtra(UtilStrings.MANAGER_HOME, true)
                startActivity(intent)
                Logger.info("splash screen, no refresh token found")
            } else {
                if (connected) {
                    mApplication.setCurrentConnectivity(true)
                    mApplication.checkRefreshToken(this)
                } else {
                    screenLive = false
                    val intent = Intent(this@SplashActivity, SelectUserActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra(UtilStrings.FROM_SPLASH, true)
                    intent.putExtra(UtilStrings.SPLASH_WITH_DATA, true)
                    startActivity(intent)
                }
            }
        }

        manageUsersViewModel.userResponsesLiveData.observe(this) {
            fetchUsersResponseOrErrorReceived()
        }
        manageUsersViewModel.errorFetchUsersLiveData.observe(this) {
            fetchUsersResponseOrErrorReceived()
        }
        initTimer()
    }

    private fun initTimer(){
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (screenLive) {
                    Logger.info("APP START WITH TIMER TICK TocK")
                    mCounter ++
                }
            }

            override fun onFinish() {
                fetchUsersResponseOrErrorReceived()
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        if (UserDateHelper(mCtx).userLoggedIn) {
            UserDateHelper(mCtx).resetUserLoggedInDetails()
        }
    }

    fun checkTokenShowLogin(): Boolean {
        return PreferenceHelper.getInstance(mCtx).token == null
    }

    override fun receivedResponse(result: JSONObject) {
        manageUsersViewModel.fetchUsers()
    }

    private fun fetchUsersResponseOrErrorReceived() {
        if (screenLive) {
            screenLive = false
            Analytics.trackEvent("APP START TIME $mCounter")
            Logger.info("APP START TIME $mCounter")
            val intent = Intent(this@SplashActivity, SelectUserActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra(UtilStrings.FROM_SPLASH, true)
            intent.putExtra(UtilStrings.SPLASH_WITH_DATA, true)
            startActivity(intent)
        }
    }

    override fun errorReceived(result: VolleyError) {
        var statusCode = 0
        if (result.networkResponse != null) {
            statusCode = result.networkResponse.statusCode
        }
        if (!screenLive) return
        if (statusCode == 401) {
            screenLive = false
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
        } else {
            Analytics.trackEvent("APP START TIME WITH ERROR $mCounter")
            // request failed, not load login
            screenLive = false
            val intent = Intent(this@SplashActivity, SelectUserActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra(UtilStrings.FROM_SPLASH, true)
            intent.putExtra(UtilStrings.SPLASH_WITH_DATA, false)
            intent.putExtra(UtilStrings.SPLASH_STATUS_CODE, statusCode)
            startActivity(intent)
        }
    }

    override fun onRefreshingInProgress() {
        if (!screenLive) return
        screenLive = false
        val intent = Intent(this@SplashActivity, SelectUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtra(UtilStrings.FROM_SPLASH, true)
        intent.putExtra(UtilStrings.SPLASH_WITH_DATA, true)
        startActivity(intent)
    }


    override fun tokenRefreshSuccessfully(result: JSONObject?) {
        if (!screenLive) return
        if (mPresenter != null) {
            mPresenter!!.fetchSiteDataFromServer()
            mPresenter = null
        }
    }

    override fun tokenRefreshWithError(error: VolleyError?) {
        if (!screenLive) return
        screenLive = false
        if (error is TimeoutError) {
            val intent = Intent(this@SplashActivity, SelectUserActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra(UtilStrings.FROM_SPLASH, true)
            intent.putExtra(UtilStrings.SPLASH_WITH_DATA, true)
            startActivity(intent)
        } else {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
