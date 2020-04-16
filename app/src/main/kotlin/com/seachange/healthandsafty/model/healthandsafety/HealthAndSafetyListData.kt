package com.seachange.healthandsafty.model.healthandsafety

import com.seachange.healthandsafty.offline.model.ModelConstants
import com.seachange.healthandsafty.viewmodel.HealthAndSafetyViewModel

class HealthAndSafetyListData(
        val id: Long? = -1,
        val title: String? = null,
//        val questionList: ArrayList<HealthAndSafetyQuestions> = ArrayList(),
        val progress: Float? = 0f,
        var isExpand: Boolean? = false,
        var status: Int = ModelConstants.NOT_APPLICABLE,
        val sectionType: HealthAndSafetyViewModel.Companion.SectionList?,
        val isHeader: Boolean? = false,
        val parentId: Int? = 0,
        val childCount: Int? = 0
//        var childAdapter: HealthChildAdapter? = null
) {

    /*class HealthAndSafetyQuestions(
            val id: Long? = -1,
            val title: String? = null,
            var status: Int = ModelConstants.NOT_APPLICABLE
    )*/
}