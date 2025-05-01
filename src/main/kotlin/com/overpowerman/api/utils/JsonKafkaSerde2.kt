package com.overpowerman.api.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

class JsonKafkaSerde2<T>(
    private val targetClass: Class<T>
): Serde<T> {
    override fun serializer(): Serializer<T> {
        return JsonKafkaSerializer(targetClass)
    }

    override fun deserializer(): Deserializer<T> {
        return JsonKafkaDeserializer(targetClass)
    }

    class JsonKafkaDeserializer<T>(
        private val targetClass: Class<T>
    ) : Deserializer<T> {

        private val objectMapper: ObjectMapper = ObjectMapper().registerModules(
            KotlinModule.Builder().build()
        )

        override fun deserialize(topic: String?, data: ByteArray?): T? {
            return data?.let { objectMapper.readValue(it, targetClass) }
        }
    }

    class JsonKafkaSerializer<T>(
        private val targetClass: Class<T>
    ) : Serializer<T>{
        private val objectMapper: ObjectMapper = ObjectMapper().registerModules(KotlinModule.Builder().build());

        override fun serialize(topic: String?, tweet: T?): ByteArray? {
            return tweet?.let { objectMapper.writeValueAsBytes(tweet) }
        }

    }
}
