package com.seachange.healthandsafty.view

import com.seachange.healthandsafty.model.RiskCategory

interface HazardCategoryView {
    fun catetoriesReceived(mCategories: ArrayList<RiskCategory>)
    fun postHazardSuccessfully()
}