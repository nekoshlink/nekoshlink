package org.nekosoft.shlink.service.exception

import org.nekosoft.shlink.entity.ShortUrl.Companion.MIN_LENGTH
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*


/**
 *
 */
sealed class NekoShlinkException(val status: HttpStatus, msg: String? = null) : Exception(msg)


/**
 *
 */
sealed class NekoShlinkDomainException(status: HttpStatus, msg: String? = null) : NekoShlinkException(status, msg)

class InvalidDefaultDomainException(val domain: String) : NekoShlinkDomainException(BAD_REQUEST, "The specified default domain is invalid : $domain")
class InvalidDomainDefaultsException(val option: String) : NekoShlinkDomainException(BAD_REQUEST, "The specified domain default option is invalid : $option")
class DefaultDomainDoesNotExistsException : NekoShlinkDomainException(EXPECTATION_FAILED, "A default domain does not yet exist")
class DefaultDomainAlreadyExistsException(val authority: String) : NekoShlinkDomainException(BAD_REQUEST, "A default domain already exists : $authority")
class DomainDoesNotExistException(val authority: String) : NekoShlinkDomainException(NOT_FOUND, "The given domain does not exist : $authority")
class DomainAlreadyExistsException(val authority: String) : NekoShlinkDomainException(BAD_REQUEST, "The given domain already exists : $authority")

/**
 *
 */
sealed class NekoShlinkTagException(status: HttpStatus, msg: String? = null) : NekoShlinkException(status, msg)

class TagDoesNotExistException(val name: String) : NekoShlinkTagException(NOT_FOUND, "The tag does not exist : $name")
class TagAlreadyExistsException(val name: String) : NekoShlinkTagException(BAD_REQUEST, "The tag already exists : $name")
class TagListOptionsException() : NekoShlinkTagException(BAD_REQUEST, "You can find a Tag either by ID or by Name only")


/**
 *
 */
sealed class NekoShlinkCreationException(status: HttpStatus, msg: String? = null) : NekoShlinkException(status, msg)

class MissingLongUrlException : NekoShlinkCreationException(BAD_REQUEST, "A Long URL is required in order to create a Short URL")
class InvalidLongUrlException(val longUrl: String) : NekoShlinkCreationException(BAD_REQUEST, "The given Long URL is invalid : $longUrl")
class InvalidShortCodeException(val shortCode: String) : NekoShlinkCreationException(BAD_REQUEST, "The given Short URL is invalid : $shortCode")
class DuplicateShortCodeException(val shortCode: String, val authority: String) : NekoShlinkCreationException(BAD_REQUEST, "The given Short URL already exists : $authority / $shortCode")
class MinimumShortCodeLengthException(val length: Int) : NekoShlinkCreationException(BAD_REQUEST, "The length of the Short URL must be greater than $MIN_LENGTH : $length")


/**
 *
 */
sealed class NekoShlinkEditException(status: HttpStatus, msg: String? = null) : NekoShlinkException(status, msg)

class FindOptionsException : NekoShlinkEditException(BAD_REQUEST, "You can modify a Short URL either by ID or by Short URL + Domain")
class ShortUrlDoesNotExistException(val id: Long?, val shortCode: String?, val authority: String?) : NekoShlinkEditException(NOT_FOUND, "The given Short URL does not exist : id $id, $authority / $shortCode")


/**
 *
 */
sealed class NekoShlinkResolutionException(status: HttpStatus, msg: String) : NekoShlinkException(status, msg)

class MissingShortUrlException() : NekoShlinkResolutionException(BAD_REQUEST, "A Short URL is required in order to perform resolution")
class ShortUrlNotFoundException(val shortCode: String, val authority: String) : NekoShlinkResolutionException(NOT_FOUND, "The Short URL could not be found : $authority / $shortCode")
class ProtectedShortUrlResolutionException(val shortCode: String, val authority: String) : NekoShlinkResolutionException(UNAUTHORIZED, "The Short URL resolution is protected : $authority / $shortCode")
class ShortUrlHasExpiredException(val shortCode: String, val authority: String) : NekoShlinkResolutionException(FORBIDDEN, "The Short URL has expired : $authority / $shortCode")
class ShortUrlNotEnabledYetException(val shortCode: String, val authority: String) : NekoShlinkResolutionException(FORBIDDEN, "The Short URL is not enabled yet : $authority / $shortCode")
class MaxVisitLimitReachedException(val shortCode: String, val authority: String) : NekoShlinkResolutionException(FORBIDDEN, "The Short URL has reached the maximum allowed number of visits : $authority / $shortCode")
class PathTrailNotAllowedException(val shortCode: String, val authority: String, val pathTrail: String) : NekoShlinkResolutionException(BAD_REQUEST, "This Long URL cannot be resolved with a path trail : $authority / $shortCode : $pathTrail")
class QueryParamsNotAllowedException(val shortCode: String, val authority: String, val queryParams: String) : NekoShlinkResolutionException(BAD_REQUEST, "This Long URL cannot be resolved with query parameters : $authority / $shortCode : $queryParams")
