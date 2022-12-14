package org.nekosoft.shlink.vo

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.nekosoft.shlink.entity.Domain.Companion.DEFAULT_DOMAIN
import picocli.CommandLine.Parameters
import picocli.CommandLine.Option
import java.time.LocalDateTime

data class ShortUrlCreateMeta(

    @field:Parameters(index = "0")
    var longUrl: String? = null,

    @field:Option(names = ["-s", "--short-code"])
    var shortCode: String? = null,

    @field:Option(names = ["-t", "--tags"])
    var tags: List<String>? = null,

    @field:Option(names = ["-f", "--valid-from"])
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var validFrom: LocalDateTime? = null,

    @field:Option(names = ["-u", "--valid-until"])
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var validUntil: LocalDateTime? = null,

    @field:Option(names = ["-v", "--max-visits"])
    var maxVisits: Int? = null,

    @field:Option(names = ["-d", "--domain"], defaultValue = DEFAULT_DOMAIN)
    var domain: String? = DEFAULT_DOMAIN,

    @field:Option(names = ["--title"])
    var title: String? = null,

    @field:Option(names = ["--crawlable"], defaultValue = true.toString())
    var crawlable: Boolean = true,

    @field:Option(names = ["--forward-query"], defaultValue = true.toString())
    var forwardQuery: Boolean = true,

    @field:Option(names = ["--forward-path"])
    var forwardPathTrail: Boolean = false,

    @field:Option(names = ["-p", "--password"], interactive = true, arity = "0..1", echo = false)
    var password: String? = null,

)
