package com.seachange.healthandsafty.presenter

import com.seachange.healthandsafty.model.GalleryImage
import com.seachange.healthandsafty.view.GalleryView

class GalleryPresenter(val mView: GalleryView) {

    fun galleryImageTapped(imageList: ArrayList<GalleryImage>, image: GalleryImage) {

        for (i in 0 until imageList.size) {
            if (imageList[i] == image) {
                if (imageList[i].selected) {
                    imageList[i].selected = false

                    updateImagePosition(imageList, imageList[i].position)
                    imageList[i].position = null

                } else {
                    imageList[i].position = 1 + getImageSelected(imageList)
                    imageList[i].selected = true
                }
            }
        }

        mView.imageArrayUpdated(imageList)
        countImageSelected(imageList)
    }

    fun countImageSelected(imageList: ArrayList<GalleryImage>) {
        var counter = 0
        for (tmp in imageList) {
            if (tmp.selected) {
                counter += 1
            }
        }
        mView.imagesCounter(counter)
    }

     fun getImageSelected(imageList: ArrayList<GalleryImage>): Int {
        var counter = 0
        for (tmp in imageList) {
            if (tmp.selected) {
                counter += 1
            }
        }
        return counter
    }

    private fun updateImagePosition(imageList: ArrayList<GalleryImage>, pos: Int) {

        if (pos<imageList.size) {
            for (i in 0 until imageList.size) {
                if (imageList[i].selected && imageList[i].position > pos) {
                    imageList[i].position -= 1
                }
            }
        }
    }
}