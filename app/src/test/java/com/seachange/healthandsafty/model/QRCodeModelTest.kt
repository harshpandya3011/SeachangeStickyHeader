package com.seachange.healthandsafty.model

import org.junit.Assert
import org.junit.Test

class QRCodeModelTest {

    @Test
    @Throws(Exception::class)
    fun checkTimeTest() {
        val qrCode = SCQRCode()
        qrCode.point = "a"
        Assert.assertEquals("a", qrCode.getCodePoint())
    }

    @Test
    @Throws(Exception::class)
    fun checkSiteId() {
        val qrCode = SCQRCode()
        qrCode.siteId = "aadfdsafdsf"
        Assert.assertEquals("aadfdsafdsf", qrCode.siteId)
    }

    @Test
    @Throws(Exception::class)
    fun checkZoneId() {
        val qrCode = SCQRCode()
        qrCode.zoneId = "a234gfag4tsdgdfsg"
        Assert.assertEquals("a234gfag4tsdgdfsg", qrCode.zoneId)
    }

    @Test
    @Throws(Exception::class)
    fun checkPoint() {
        val qrCode = SCQRCode()
        qrCode.pointQRId = "asdf"
        Assert.assertEquals("asdf", qrCode.pointQRId)
    }

}