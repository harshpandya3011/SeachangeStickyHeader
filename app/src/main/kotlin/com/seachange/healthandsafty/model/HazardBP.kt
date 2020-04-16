package com.seachange.healthandsafty.model

class HazardBP {


    var title: String? = null
    var subTitle: String? = null
    var isHazard: Boolean = true

    constructor() {

    }

    constructor(mTitle: String, mSubTitle: String) {
        this.title = mTitle
        subTitle = mSubTitle
    }

    constructor(mTitle: String, mSubTitle: String, hazard: Boolean) {
        this.title = mTitle
        subTitle = mSubTitle
        this.isHazard = hazard
    }
}