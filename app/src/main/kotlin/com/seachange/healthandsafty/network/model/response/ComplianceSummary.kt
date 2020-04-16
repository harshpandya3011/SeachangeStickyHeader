package com.seachange.healthandsafty.network.model.response

data class ComplianceSummary(
        val numScheduled: Int?,
        val numCompleted: Int?,
        val compliance: Double?
)