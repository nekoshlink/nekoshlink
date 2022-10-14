package org.nekosoft.shlink.service.impl

import org.nekosoft.shlink.entity.ShortUrl
import org.nekosoft.shlink.service.UrlShortener
import org.nekosoft.shlink.service.exception.MinimumShortCodeLengthException
import org.nekosoft.utils.text.generateRandomText

class DefaultUrlShortener : UrlShortener {

    private val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~".toList()
    private val initialAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toList()
    private val finalAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toList()

    override fun shorten(length: Int): String =
        if (length < ShortUrl.MIN_LENGTH) throw MinimumShortCodeLengthException(length)
        else generateRandomText(length, alphabet, initialAlphabet, finalAlphabet)

}
