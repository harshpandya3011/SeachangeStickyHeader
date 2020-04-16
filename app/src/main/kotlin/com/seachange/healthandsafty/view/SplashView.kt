package com.seachange.healthandsafty.view

import com.android.volley.VolleyError
import org.json.JSONObject

interface SplashView {
    fun receivedResponse(result:JSONObject)
    fun errorReceived(result: VolleyError)
}