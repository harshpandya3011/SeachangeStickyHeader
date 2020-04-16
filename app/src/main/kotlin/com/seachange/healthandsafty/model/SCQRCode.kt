package com.seachange.healthandsafty.model

class SCQRCode {

    var siteId: String? = null
    var zoneId: String? = null
    var point: String? = null
    var pointQRId: String? = null

    constructor() {

    }

    fun getCodePoint():String {
        return point!!
    }
}