package com.overpowerman.api.kafkastreams

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.overpowerman.api.model.Tweet
import com.overpowerman.api.utils.JsonSerdes
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Printed
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class TweetStreamComponent {
    @Bean
    fun tweetsTopic() = NewTopic("tweets", 3, 1)

    @Bean
    fun tweetStream(@Qualifier("tweetStreamsBuilder") kafkaStreamsBuilder: StreamsBuilder): KStream<ByteArray, Tweet> {
        var stream: KStream<ByteArray, Tweet> = kafkaStreamsBuilder.stream("tweets",
            Consumed.with(Serdes.ByteArray(), JsonSerdes(Tweet::class.java))
        ).filter { _, tweet ->
            !tweet.retweet
        }
        stream.print(Printed.toSysOut<ByteArray, Tweet>().withLabel("tweets-stream"))

        return stream
    }
}
