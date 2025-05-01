package com.overpowerman.api.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.config.StreamsBuilderFactoryBean

@Configuration
class KafkaStreamsConfig {
    @Value("\${game-app.kafka-state-dir}")
    lateinit var kafkaStateDir: String

    @Value("\${game-app.hostname}")
    lateinit var applicationServer: String

    @Bean
    fun gameScoreStreamsConfiguration(): KafkaStreamsConfiguration {
        val config = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "gamescore-app",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:29092,localhost:39092,localhost:49092",
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String()::class.java,
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String()::class.java,
            StreamsConfig.NUM_STREAM_THREADS_CONFIG to 3,
            StreamsConfig.STATE_DIR_CONFIG to kafkaStateDir,
            StreamsConfig.APPLICATION_SERVER_CONFIG to applicationServer,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        )

        return KafkaStreamsConfiguration(config)
    }

    @Bean
    fun gameScoreStreamsBuilder(@Qualifier("gameScoreStreamsConfiguration") gameScoreStreamsConfiguration: KafkaStreamsConfiguration): StreamsBuilderFactoryBean {
        return StreamsBuilderFactoryBean(gameScoreStreamsConfiguration)
    }
}
