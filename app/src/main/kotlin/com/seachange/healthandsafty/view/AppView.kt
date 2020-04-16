package com.seachange.healthandsafty.view

import com.android.volley.VolleyError
import org.json.JSONObject

interface AppView {
    fun refreshSuccessfully(result: JSONObject?)
    fun refreshWithError(error: VolleyError?)
}