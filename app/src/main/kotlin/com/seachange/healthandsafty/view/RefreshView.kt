package com.seachange.healthandsafty.view

import com.android.volley.VolleyError
import org.json.JSONObject

interface RefreshView {
    fun tokenRefreshSuccessfully(result: JSONObject?)
    fun tokenRefreshWithError(error: VolleyError?)
    fun onRefreshingInProgress()
}
