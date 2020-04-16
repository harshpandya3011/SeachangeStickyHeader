package com.seachange.healthandsafty.nfc.view

import com.android.volley.VolleyError

interface NFCSyncView {
     fun requestSucceed()
     fun requestError(result: VolleyError?)
}