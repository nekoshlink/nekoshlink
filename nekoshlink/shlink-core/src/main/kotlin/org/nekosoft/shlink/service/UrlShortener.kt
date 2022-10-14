package org.nekosoft.shlink.service

interface UrlShortener {
    fun shorten(length: Int = 5): String
}
