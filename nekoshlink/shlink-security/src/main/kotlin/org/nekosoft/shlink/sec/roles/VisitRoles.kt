package org.nekosoft.shlink.sec.roles

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Visits:Viewer')")
annotation class IsVisitViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Visits:Viewer') and hasRole('Visits:StatsViewer')")
annotation class IsVisitStatsViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Visits:Editor')")
annotation class IsVisitEditor   // These roles (VISITS,EDITOR) should not be assigned to users as they are not needed. They are only used internally with RunAs.
