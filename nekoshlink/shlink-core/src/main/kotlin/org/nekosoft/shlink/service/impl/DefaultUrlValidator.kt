package org.nekosoft.shlink.service.impl

import org.nekosoft.shlink.service.UrlValidator
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@Service
@ConditionalOnMissingBean(UrlValidator::class)
class DefaultUrlValidator : UrlValidator {

    override fun validate(url: String): Boolean {
        return connectForResponse(url) != null
    }

    override fun validateWithTitle(url: String): String? {
        val body = connectForResponse(url)
        return if (body == null) null else {
            val regex = """<title[^>]*>(.*?)</title>""".toRegex()
            return regex.find(body)?.groups?.get(1)?.value ?: ""
        }
    }

    private fun connectForResponse(url: String): String? {
        val uri = try {
            URI.create(url)
        } catch (e: IllegalArgumentException) {
            return null
        }
        val httpClient: HttpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(9))
                .build()
        val requestHead = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build()
        val httpResponse = try {
            httpClient.send(requestHead, HttpResponse.BodyHandlers.ofString())
        } catch (e: IOException) {
            return null
        }
        return if (httpResponse.statusCode() in 200..299) httpResponse.body() else null
    }
}