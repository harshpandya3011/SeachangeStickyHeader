package com.seachange.healthandsafty.view

import com.android.volley.VolleyError
import com.seachange.healthandsafty.model.ZoneFoundHazard

interface CheckItemView {
    fun getResponse(zoneFoundHazard: ZoneFoundHazard)
    fun errorReceived(error: VolleyError?)
}