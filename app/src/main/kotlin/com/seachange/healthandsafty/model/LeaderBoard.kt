package com.seachange.healthandsafty.model

data class LeaderBoard(
        val name: String,
        val score: Double?,
        val statusRange: Int?,
        val checkCompliance: Double?,
        val tourCompliance: Double? = null,
        val tourHazard: Int? = null
) {
    companion object {
        const val AMBER = 1
        const val GREEN = 2
    }
}