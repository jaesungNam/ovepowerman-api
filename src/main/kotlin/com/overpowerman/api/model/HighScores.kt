package com.overpowerman.api.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.util.*

data class HighScores(
    val highScores: TreeSet<Enriched> = TreeSet()
) {
    fun add(enriched: Enriched): HighScores {
        highScores.add(enriched);

        if(highScores.size > 100) {
            highScores.remove(highScores.last)
        }

        return this
    }

    override fun toString(): String {
        return highScores.toString()
    }

}
