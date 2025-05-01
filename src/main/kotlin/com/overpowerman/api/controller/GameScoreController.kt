package com.overpowerman.api.controller

import com.overpowerman.api.model.HighScores
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyQueryMetadata
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/leader-boards")
class GameScoreController(
    private val gameScoreStreamsBuilder: StreamsBuilderFactoryBean
) {
    @GetMapping("/{productId}")
    fun getHighScore(@PathVariable productId: String): HighScores? {
        val kafkaStreams = gameScoreStreamsBuilder.kafkaStreams

        var metadata: KeyQueryMetadata? = kafkaStreams?.queryMetadataForKey(
            "leader-boards", productId, Serdes.String().serializer()
        )

        var activeHost = metadata?.let { it.activeHost() }

        val store = kafkaStreams?.store(
            StoreQueryParameters.fromNameAndType(
                "leader-boards",
                QueryableStoreTypes.keyValueStore<String, HighScores>()
            )
        )

        val resp = store?.get(productId)

        return resp
    }
}
