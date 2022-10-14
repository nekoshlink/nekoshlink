package org.nekosoft.shlink.dao

import org.nekosoft.shlink.entity.Domain
import org.nekosoft.shlink.service.exception.DefaultDomainAlreadyExistsException
import org.nekosoft.shlink.service.exception.DefaultDomainDoesNotExistsException
import org.nekosoft.shlink.service.exception.DomainAlreadyExistsException
import org.nekosoft.shlink.service.exception.DomainDoesNotExistException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.net.URI

/**
 * Provides the interface for all the operations that can be performed on the Domain realm.
 */
interface DomainDataAccess {

    /**
     * Creates a new Domain with the given data. Returns the new Domain that was created, as
     * stored in the persistent store. For example, the returned value would provide the ID that
     * was generated in the store and that was not set in the given parameter.
     *
     * @param domain the data of the Domain to be created
     * @return the new Domain that was created, as stored in the persistent store
     * @throws DefaultDomainAlreadyExistsException if the user tried to create a new Domain
     * that was set to be the default
     * @throws DomainAlreadyExistsException if the domain authority of the domain already exists
     */
    fun create(domain: Domain): Domain

    /**
     * Updates an existing Domain with the given data. Returns the new Domain that was created,
     * as stored in the persistent store. This method will look the domain up by authority.
     * The ID of the domain is not used and will not be updated either.
     *
     * @param domain the new data for the Domain to be updated
     * @return the updated Domain, as stored in the persistent store
     * @throws DomainDoesNotExistException if the user tried to update a Domain that does not exist
     */
    fun update(domain: Domain): Domain

    /**
     * Deletes an existing Domain. This method will look the domain up by authority. The ID of the
     * domain is ignored.
     *
     * @param authority the authority of the Domain to be deleted
     * @throws DomainDoesNotExistException if the user tried to delete a Domain that does not exist
     */
    fun remove(authority: String)

    /**
     * Lists all domains in this NekoShlink instance.
     *
     * @param pageable a Pageable instance to return the list of domains in pages
     */
    fun list(pageable: Pageable? = null): Page<Domain>

    /**
     * Finds a Domain by authority.
     *
     * @param authority the authority of the Domain to be returned
     * @return the Domain corresponding to the given authority, or `null`
     * if the authority could not be found
     */
    fun findByAuthority(authority: String?): Domain?

    /**
     * Returns the default Domain object. A default domain should always be set, and one
     * is created automatically at the start of a new instance, so an exception
     * should not usually be expected.
     *
     * @return the default Domain
     * @throws DefaultDomainDoesNotExistsException if a default domain cannot be found
     */
    fun getDefault(): Domain

    /**
     * Returns the default authority. This is simply the authority associated to
     * the [default domain][getDefault].
     *
     * @return the default authority, or an empty string if not set
     */
    fun getDefaultAuthority(): String

    /**
     * Returns the default base URL redirect. This is used when the user browses to
     * the top level URL of an associated Domain, only if that Domain does not have
     * a value set for the [baseUrlRedirect][Domain.baseUrlRedirect] property.
     *
     * This value must be always set.
     *
     * @return a URI instance pointing to the default base URL redirect
     */
    fun getDefaultBaseUrlRedirect(): URI

    /**
     * Returns the default request error redirect. This is used when one of the open APIs
     * returns an error and the associated Domain does not have a value set for the
     * [requestErrorRedirect][Domain.requestErrorRedirect] property. A standard HTML page is
     * shown if this property and the Domain property are both not set.
     *
     * @return a URI instance pointing to the default request error redirect
     */
    fun getDefaultRequestErrorRedirect(): URI?

    /**
     * Returns the default password form redirect. This is used when a Short URL requires a
     * password for resolution and the associated Domain does not have a value set for the
     * [passwordFormRedirect][Domain.passwordFormRedirect] property. A standard HTML page is
     * shown if this property and the Domain property are both not set.
     *
     * @return a URI instance pointing to the default password form redirect
     */
    fun getDefaultPasswordFormRedirect(): URI?

    /**
     * Change the default domain of the NekoShlink instance.
     *
     * @param authority the authority of the Domain to become the new default
     * @return the Domain instance that was made the new default domain
     * @throws DomainDoesNotExistException if the authority does not exist as a domain
     */
    fun makeDefault(authority: String): Domain

    /**
     * Returns the authority for the given domain, resolving `null` and
     * [DEFAULT_DOMAIN][Domain.DEFAULT_DOMAIN] into the actual authority
     * for the default domain of the instance.
     *
     * @param authority an authority that could be `null` or equal to
     * [DEFAULT_DOMAIN][Domain.DEFAULT_DOMAIN] and should be resolved into the
     * actual authority string for the default domain of the instance
     * @return the resolved authority corresponding to the argument
     */
    fun resolveDefaultAuthority(authority: String?): String
}
