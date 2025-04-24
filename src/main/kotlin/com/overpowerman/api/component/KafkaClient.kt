package com.overpowerman.api.component

import CurrencyRate
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.stereotype.Component


@Component
class KafkaClient {

    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, String>? = null

    @PostConstruct
    fun printKafkaTemplateConfig() {
        val factory = kafkaTemplate!!.producerFactory
        val config = factory.configurationProperties
        config.forEach { (k: String?, v: Any?) -> println("$k = $v") }
    }

    @Bean
    fun topic() = NewTopic("test-topic3", 3, 3)

    @KafkaListener(topics = ["test-topic3"], groupId = "test-consumer-group")
    fun listen(value: CurrencyRate) {
        println(value)
    }


}
