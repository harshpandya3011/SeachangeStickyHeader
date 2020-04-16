package com.seachange.healthandsafty.view

import com.seachange.healthandsafty.model.HazardBP

interface HazardBPView {
    fun hazardBPReceived(all: ArrayList<HazardBP>, hazards: ArrayList<HazardBP>, bps: ArrayList<HazardBP>)
}