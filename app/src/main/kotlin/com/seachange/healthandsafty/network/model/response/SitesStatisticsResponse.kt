package com.seachange.healthandsafty.network.model.response

data class SitesStatisticsResponse(
        val siteId: Long,
        val name: String,
        val startDate: String,
        val endDate: String,
        val numOfDays: Int,
        val siteTourSummary: Summary?,
        val zoneCheckSummary: Summary?,
        val overallScore: Double?,
        val ragStatus: Int?,
        val zones: List<Zone>?,
        val version: Int?,
        val eTag: String?
) {
    data class Zone(
            val rank: Int,
            val id: Long,
            val name: String,
            val complianceSummary: ComplianceSummary?,
            val numOfSiteTourHazardsIdentified: Int?,
            val numOfZoneCheckHazardsIdentified: Int?,
            val overallScore: Double?,
            val ragStatus: Int?
    )
}