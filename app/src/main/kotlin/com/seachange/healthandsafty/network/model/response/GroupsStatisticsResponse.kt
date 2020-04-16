package com.seachange.healthandsafty.network.model.response

data class GroupsStatisticsResponse(
        val groupId: Long,
        val name: String,
        val startDate: String,
        val endDate: String,
        val numOfDays: Int,
        val numOfSites: Int?,
        val numOfZones: Int?,
        val siteTourSummary: Summary?,
        val zoneCheckSummary: Summary?,
        val overallScore: Double?,
        val ragStatus: Int?,
        val sites: List<Site>?,
        val version: Int?,
        val eTag: String?
) {
    data class Site(
            val rank: Int,
            val id: Long,
            val name: String,
            val siteTourSummary: Summary?,
            val zoneCheckSummary: Summary?,
            val overallScore: Double?,
            val ragStatus: Int?
    )
}