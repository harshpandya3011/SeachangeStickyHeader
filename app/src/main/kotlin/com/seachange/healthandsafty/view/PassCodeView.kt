package com.seachange.healthandsafty.view

interface PassCodeView {
    fun onValidSuccessful()
    fun onValidFailed()
    fun onTempPassCode()
}