package org.nekosoft.shlink.service.impl

import org.nekosoft.shlink.sec.web.ShlinkOpenApiSecurityConfiguration
import org.nekosoft.shlink.service.ShortCodeValidator

/**
 * The default implementation of the [ShortCodeValidator] interface.
 *
 * It uses the regular expression string from the [ShlinkOpenApiSecurityConfiguration] class to confirm
 * that the provided short code will be effectively matched as a URL by the controllers that resolve short urls
 * for redirection, QR Code generation, or tracking image generation.
 *
 * See [ShlinkOpenApiSecurityConfiguration.URL_SEGMENT_REGEX]
*/
class DefaultShortCodeValidator : ShortCodeValidator {

    val pattern = ShlinkOpenApiSecurityConfiguration.URL_SEGMENT_REGEX.toPattern()

    override fun validate(shortCode: String): Boolean =
        pattern.matcher(shortCode).matches()

}