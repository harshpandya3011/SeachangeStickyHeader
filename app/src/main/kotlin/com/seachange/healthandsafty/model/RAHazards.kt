package com.seachange.healthandsafty.model

class RAHazards {

    var riskAssessmentId: String? = null
    var description: String? = null
    var reference: String? = null
    var primaryImageUrl: String? = null
    var riskRating: RAHazardsRiskRating? = null
    var status: RAHazardsStatus ?= null
    var publishStatus: RAHazardsPublishStatus ?= null
    var risks: ArrayList<RAHazardsRisk>? = null
    var numOfNewControls: Int? = null
    var id: String?= null
}


class RAHazardsRiskRating {
    var label: String? = null
    var abbreviation: String? = null
    var color: String? = null
}

class RAHazardsStatus {
    var code: Int? = null
    var label: String? = null
}

class RAHazardsPublishStatus {
    var code: Int? = null
    var label: String? = null
}


class RAHazardsRisk  {
    var id: String? = null
    var label: String? = null
    var iconUrl: String? = null
}