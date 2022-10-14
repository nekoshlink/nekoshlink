package org.nekosoft.shlink.service

interface UrlValidator {
    fun validate(url: String): Boolean
    fun validateWithTitle(url: String): String?
}
