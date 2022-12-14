package org.nekosoft.shlink.entity

import org.hibernate.Hibernate
import org.nekosoft.shlink.entity.support.AuditInfo
import org.nekosoft.shlink.entity.support.JpaDataAccessAudit
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class, JpaDataAccessAudit::class)
class Domain(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(unique = true, length = 256)
        var authority: String = DEFAULT_DOMAIN,

        @Column(length = 16)
        var scheme: String = "https",

        @Column(length = 1024)
        var baseUrlRedirect: String? = null,

        @Column(length = 1024)
        var requestErrorRedirect: String? = null,

        @Column(length = 1024)
        var passwordFormRedirect: String? = null,

        var isDefault: Boolean = authority == DEFAULT_DOMAIN,

        @Embedded
        var auditInfo: AuditInfo = AuditInfo(),

        ) {

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
                other as Domain

                return if (id != null) {
                        id == other.id
                } else {
                        scheme == other.scheme && authority == other.authority
                }
        }

        override fun hashCode(): Int =
                id?.hashCode() ?: (scheme + authority).hashCode()

        @Override
        override fun toString(): String =
                this::class.simpleName + "(id = $id, scheme = $scheme, authority = $authority)"

        companion object {
                // make sure this string could not also work as a valid authority
                // e.g. by using invalid characters in a domain name such as < > # etc
                const val DEFAULT_DOMAIN = "<<#DEFAULT#>>"
        }
}
