package com.overpowerman.api.component

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CurrencyRateTaskTest {
    @Autowired
    lateinit var currencyRateTask: CurrencyRateTask

    @Test
    fun testRunOnce() {
        currencyRateTask.currencyRatePolling() // 직접 호출
    }
}
