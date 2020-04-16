package com.seachange.healthandsafty.activity.camera

interface CameraPresenterView {
    fun imageUploaded(response: String)
    fun imageUploadError(response: String)
}