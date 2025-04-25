package com.overpowerman.api.kafkastreams

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.apache.kafka.streams.processor.api.Record
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.stereotype.Component

@Component
class HelloWorldTopology {

    @Bean
    fun usersTopic() = NewTopic("users", 3, 1)

    @Bean
    fun helloWorld(@Qualifier("kafkaStreamsBuilder") kafkaStreamsBuilder: StreamsBuilder): KStream<Void, String> {
        var stream: KStream<Void, String> = kafkaStreamsBuilder.stream("users")

        stream.foreach { _, value ->
            println("(DSL) Hello, " + value)
        }

        return stream
    }
}
