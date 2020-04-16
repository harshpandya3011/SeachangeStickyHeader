package com.seachange.healthandsafty.network.model.response

data class Summary(
        val numOfHazards: Int?,
        val complianceSummary: ComplianceSummary?
)