package com.seachange.healthandsafty.view

import com.seachange.healthandsafty.model.GalleryImage

interface GalleryView {
    fun imageArrayUpdated(result: ArrayList<GalleryImage>)
    fun imagesCounter(result: Int)
}