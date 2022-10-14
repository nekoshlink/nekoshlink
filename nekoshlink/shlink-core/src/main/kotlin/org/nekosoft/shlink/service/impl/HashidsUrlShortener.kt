package org.nekosoft.shlink.service.impl

import org.hashids.Hashids
import org.nekosoft.shlink.entity.ShortUrl.Companion.MIN_LENGTH
import org.nekosoft.shlink.service.UrlShortener
import org.nekosoft.shlink.service.exception.MinimumShortCodeLengthException
import org.springframework.beans.factory.annotation.Value
import java.util.random.RandomGeneratorFactory

class HashidsUrlShortener : UrlShortener {

    @Value("\${nekoshlink.shortener.hashids.salt}")
    private var salt: String = "nekoshlink-shortcode-salt"

    private var hashids: Hashids = Hashids(salt)

    private val random = RandomGeneratorFactory.getDefault().create()

    override fun shorten(length: Int): String {
        if (length < MIN_LENGTH) throw MinimumShortCodeLengthException(length)
        val hash = hashids.encode(random.nextLong(Hashids.MAX_NUMBER), MIN_LENGTH.toLong())
        val reducedHash = if (hash.length <= length) hash else {
            val startPos = random.nextInt(hash.length - length)
            hash.substring(startPos, startPos + length)
        }
        return reducedHash
    }

}
