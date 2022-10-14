package org.nekosoft.shlink.rest

import org.nekosoft.shlink.ShlinkCoreConfiguration
import org.nekosoft.utils.CrawlerDetect
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(ShlinkCoreConfiguration::class)
class ShlinkRestApiServer {

	@Bean
	fun crawlerDetector(): CrawlerDetect =
		CrawlerDetect.newInstance()

	companion object {
		const val VERSION_STRING = "1"
	}

}

fun main(args: Array<String>) {
	runApplication<ShlinkRestApiServer>(*args)
}
