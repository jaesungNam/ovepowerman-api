package com.overpowerman.api.controller

import com.overpowerman.api.model.CurrencyRate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class KafkaController {
    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, CurrencyRate>

    @GetMapping("/send")
    fun testKafka(@RequestParam("message") message: String ) {
        kafkaTemplate.executeInTransaction { t ->
            t.send("test-topic3", CurrencyRate("${message}1", "wq", 2.0))
            t.send("test-topic3", CurrencyRate("${message}2", "wq", 2.0))
            t.send("test-topic3", CurrencyRate("${message}3", "wq", 2.0))
            t.send("test-topic3", CurrencyRate("${message}4", "wq", 2.0))
        }
    }
}
