package com.seachange.healthandsafty.view

import com.android.volley.VolleyError
import org.json.JSONObject

interface ScheduleRequestView {
    fun refreshSuccessfully()
    fun refreshWithError(error: VolleyError?)
    fun removeWithError(error: VolleyError?)
    fun addTimeRangeWithError(error: VolleyError?)
    fun removeSuccessfully()
    fun reloadPage()
}