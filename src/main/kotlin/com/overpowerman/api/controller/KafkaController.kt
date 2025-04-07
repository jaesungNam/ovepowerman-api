package com.overpowerman.api.controller

import CurrencyRate
import org.springframework.beans.factory.annotation.Autowired
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
        var m = CurrencyRate(message, "wq", 2.0)
        kafkaTemplate.send("test-topic3", m)
    }
}
