package com.overpowerman.api.model

data class ScoreEvent(
    var score: Double,
    var productId: Long,
    var playerId: Long,
)
