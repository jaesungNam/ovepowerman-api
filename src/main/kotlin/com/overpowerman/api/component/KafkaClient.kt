package com.overpowerman.api.component

import CurrencyRate
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaClient {

    @Bean
    fun topic() = NewTopic("test-topic3", 3, 3)

    @KafkaListener(topics = ["test-topic3"], groupId = "test-consumer-group")
    fun listen(value: CurrencyRate) {
        println(value)
    }
}
