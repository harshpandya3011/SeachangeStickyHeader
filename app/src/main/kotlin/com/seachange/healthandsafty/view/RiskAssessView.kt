package com.seachange.healthandsafty.view

import com.android.volley.VolleyError
import com.seachange.healthandsafty.model.RiskAssement
import org.json.JSONObject

interface RiskAssessView {
    fun receivedResponse(arrayList: ArrayList<RiskAssement>)
    fun errorReceived(result: VolleyError)
}