package org.nekosoft.shlink.sec.roles

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Tags:Viewer')")
annotation class IsTagViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Tags:Viewer') and (!#options.withStats or hasRole('Tags:StatsViewer'))")
annotation class IsTagStatsViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Tags:Editor')")
annotation class IsTagEditor

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('Tags:Admin')")
annotation class IsTagAdmin
