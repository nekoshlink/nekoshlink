package org.nekosoft.shlink.service

interface ShortCodeValidator {
    fun validate(shortCode: String): Boolean
}