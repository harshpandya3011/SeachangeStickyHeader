package com.seachange.healthandsafty.nfconboard

import android.content.Intent
import android.os.Bundle
import com.android.volley.VolleyError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.BaseActivity
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.network.JsonCallBack
import com.seachange.healthandsafty.network.VolleyJsonHelper
import com.seachange.healthandsafty.utils.UtilStrings
import kotlinx.android.synthetic.main.activity_nfcon_boarding_welcome.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class NFCOnBoardingWelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcon_boarding_welcome)
        initView()
        initButton()
        initData()
    }

    private fun initView() {
        welcome_clock_icon.typeface = SCApplication.FontMaterial()
    }

    private fun initButton() {
        welcome_start_button.setOnClickListener {
            val intent = Intent(this@NFCOnBoardingWelcomeActivity, NFCOnBoardTagActivity::class.java)
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
        }

        nfc_logout_hazard.setOnClickListener {
            userLogOut()
        }
    }

    private fun initData() {
        val name = PreferenceHelper.getInstance(mCtx).userName
        if(name == null) {
            getUserData(PreferenceHelper.getInstance(mCtx).requestToken())
        } else {
            showUserName(name)
        }
    }

    private fun showUserName(name: String) {
        val splitStr = name.split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        nfc_welcome_user.text = "Hi " + splitStr.get(0) + ","
    }

    private fun getUserData(token: String) {
        val callBack = object : JsonCallBack {
            override fun callbackJSONObject(result: JSONObject) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        val statusCode = result.getInt(UtilStrings.STATUS_CODE)
                        if (statusCode == 200) {
                            val userName : String = result.getJSONObject("response").getString("name")
                            PreferenceHelper.getInstance(mCtx).saveUserName(userName)
                            showUserName(userName)
                        }
                    } catch (e: JSONException) {
                        Logger.info("User TAG: $e")
                    }
                }
            }

            override fun callbackJsonArray(result: JSONArray) {

            }

            override fun callbackErrorCalled(result: VolleyError) {
                Logger.info(result.toString())
            }
        }
        VolleyJsonHelper(UtilStrings.GET_USER_API + "user" , "User TAG", callBack, mCtx).getJsonObjectFromVolleyHelper(token, null)
    }
}
