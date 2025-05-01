package com.overpowerman.api.model

data class Enriched(
    var playerId: Long,
    var productId: Long,
    var playerName: String,
    var gameName: String,
    var score: Double,
): Comparable<Enriched> {
    companion object {
        fun fromScoreWithPlayerProduct(scoreWithPlayer: ScoreWithPlayer, product: Product): Enriched {
            return Enriched(
                scoreWithPlayer.player.id,
                product.id,
                scoreWithPlayer.player.name,
                product.name,
                scoreWithPlayer.scoreEvent.score,
            )
        }
    }

    override fun compareTo(other: Enriched): Int {
        return other.score.compareTo(score)
    }
}
