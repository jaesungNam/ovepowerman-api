package com.overpowerman.api.kafkastreams

import com.overpowerman.api.model.*
import com.overpowerman.api.utils.JsonKafkaSerde
import com.overpowerman.api.utils.JsonKafkaSerde2
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class GameScoreTopology {
    @Bean fun scoreEventsTopic() = NewTopic("score-events", 6, 1)
    @Bean fun playersTopic() = NewTopic("players", 6, 1)
    @Bean fun productsTopic() = NewTopic("products", 3, 1)

    @Bean
    fun gameScoreStream(@Qualifier("gameScoreStreamsBuilder") kafkaStreamsBuilder: StreamsBuilder): KTable<String, HighScores> {
        var scoreEvents =
            kafkaStreamsBuilder.stream("score-events", Consumed.with(Serdes.ByteArray(), JsonKafkaSerde(ScoreEvent::class.java)))
                .selectKey { k, v -> v.playerId.toString() }
        var players = kafkaStreamsBuilder.table("players", Consumed.with(Serdes.String(), JsonKafkaSerde(Player::class.java)))

        var scorePlayJoiner =  ValueJoiner<ScoreEvent, Player, ScoreWithPlayer> { scoreEvent, player -> ScoreWithPlayer(scoreEvent, player) }

        var playerJoinParams: Joined<String, ScoreEvent, Player> = Joined.with(
            Serdes.String(),
            JsonKafkaSerde(ScoreEvent::class.java),
            JsonKafkaSerde(Player::class.java)
        )

        var withPlayers: KStream<String, ScoreWithPlayer> = scoreEvents.join(
            players,
            scorePlayJoiner,
            playerJoinParams
        )

        var products = kafkaStreamsBuilder.globalTable("products", Consumed.with(Serdes.String(), JsonKafkaSerde(Product::class.java)))
        var productJoiner = ValueJoiner<ScoreWithPlayer, Product, Enriched> { scoreWithPlayer, product -> Enriched.fromScoreWithPlayerProduct(scoreWithPlayer, product) }
        var keyValueMapper = KeyValueMapper<String, ScoreWithPlayer, String> { leftKey, scoreWithPlayer ->
            scoreWithPlayer.scoreEvent.productId.toString()
        }
        var withProducts: KStream<String, Enriched> = withPlayers.join(products, keyValueMapper, productJoiner)

        var grouped: KGroupedStream<String, Enriched> = withProducts.groupBy(
            { key, value -> value.productId.toString() },
            Grouped.with(Serdes.String(), JsonKafkaSerde(Enriched::class.java))
        )

        var highScoresInitializer = Initializer { HighScores() }

        val highScoresAdder = Aggregator<String, Enriched, HighScores> { key, value, aggregate ->
            aggregate.add(value)
        }

        var highScores: KTable<String, HighScores> = grouped.aggregate(
            highScoresInitializer,
            highScoresAdder,
            Materialized.`as`<String, HighScores, KeyValueStore<Bytes, ByteArray>>("leader-boards")
                .withKeySerde(Serdes.String())
                .withValueSerde(JsonKafkaSerde2(HighScores::class.java))
        )

        return highScores
    }
}
