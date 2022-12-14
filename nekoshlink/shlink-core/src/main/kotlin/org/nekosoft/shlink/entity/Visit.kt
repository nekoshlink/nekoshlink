package org.nekosoft.shlink.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.Hibernate
import org.nekosoft.shlink.entity.support.*
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@EntityListeners(JpaDataAccessAudit::class)
class Visit(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    var shortUrl: ShortUrl? = null,

    @Column(length = 64)
    var shortCode: String? = null,

    @Column(length = 256)
    var domain: String? = null,

    var type: VisitType = VisitType.SUCCESSFUL,

    var source: VisitSource = VisitSource.API,

    @Column(length = 1024)
    var visitedUrl: String? = null,

    var date: LocalDateTime = LocalDateTime.now(),

    @Column(length = 1024)
    var referrer: String? = null,

    @Column(length = 1024)
    var remoteAddr: String? = null,

    @Column(length = 1024)
    var pathTrail: String? = null,

    @Column(length = 1024)
    var queryString: String? = null,

    @Column(length = 1024)
    var userAgent: String? = null,

    @Embedded
    var visitLocation: VisitLocation? = VisitLocation(),

    var potentialBot: Boolean? = null,

    ) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Visit

        return if (id != null) {
            id == other.id
        } else {
            false
        }
    }

    override fun hashCode(): Int =
        id?.hashCode() ?: super.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, shortCode = $shortCode, date = $date)"
    }

}
