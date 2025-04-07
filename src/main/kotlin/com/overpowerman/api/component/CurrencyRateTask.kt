package com.overpowerman.api.component

import CurrencyRate
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class CurrencyRateTask {

    @Scheduled(fixedRate = 1000 * 60 * 5)
    fun currencyRatePolling() {
        val options = ChromeOptions()
        options.addArguments("--headless=new") // 창 없이 실행 (서버에서 유용)
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--disable-gpu")

        val driver = ChromeDriver(options)
        driver.get("https://www.kebhana.com/cms/rate/index.do?contentUrl=/cms/rate/wpfxd651_01i.do");
        val wait = WebDriverWait(driver, Duration.ofSeconds(10))
        wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.tblBasic")) // 예시: h1 태그가 로딩될 때까지
        )
        // 원하는 URL 접속 및 자동화 작업 수행

        // driver.get("https://www.kebhana.com/cms/rate/index.do?contentUrl=/cms/rate/wpfxd651_01i.do")
        var tableRows = driver.findElements(By.cssSelector("table.tblBasic tbody tr"))
        val currencyRates = tableRows.map { tr ->
            val tds = tr.findElements(By.tagName("td"))
            val currencyText = tds[0].text
            val splited = currencyText.split(" ");
            val currencyKor = splited[0]
            val currency = splited[1]

            val divide100 = splited.getOrElse(2){""}.contains("100") ?: false

            val rate = tds[1].text.replace(",", "").toDouble()
            CurrencyRate(currency, currencyKor, if(divide100) rate / 100 else rate)
        }

        println(currencyRates)

    }

}
