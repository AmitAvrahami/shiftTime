package com.example.shiftime.logic.data

data class ScoringWeights(
    val shiftCountWeight: Double,
    val workHoursWeight: Double,
    val restPenaltyWeight: Double,
    val consecutiveNightPenalty: Double,
    val maxShiftDistanceWeight: Double,
)