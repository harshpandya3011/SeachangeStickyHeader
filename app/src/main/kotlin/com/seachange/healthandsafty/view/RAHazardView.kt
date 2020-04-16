package com.seachange.healthandsafty.view

import com.android.volley.VolleyError
import com.seachange.healthandsafty.model.RAHazards

interface RAHazardView {
    fun receivedResponse(result: ArrayList<RAHazards>)
    fun errorReceived(result: VolleyError)
}