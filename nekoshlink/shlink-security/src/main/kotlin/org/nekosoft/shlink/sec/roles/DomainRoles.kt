package org.nekosoft.shlink.sec.roles

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Domains:Viewer')")
@MustBeDocumented
annotation class IsDomainViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Domains:Viewer') and (!#options.withStats or hasRole('Domains:StatsViewer'))")
@MustBeDocumented
annotation class IsDomainStatsViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Domains:Editor')")
@MustBeDocumented
annotation class IsDomainEditor

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Domains:Admin')")
@MustBeDocumented
annotation class IsDomainAdmin
