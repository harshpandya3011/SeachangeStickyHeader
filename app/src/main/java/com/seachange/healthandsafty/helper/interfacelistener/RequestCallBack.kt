package com.seachange.healthandsafty.helper.interfacelistener

import com.android.volley.VolleyError

interface RequestCallBack {
    fun onSucceed()
    fun onError(error: VolleyError?)
}
