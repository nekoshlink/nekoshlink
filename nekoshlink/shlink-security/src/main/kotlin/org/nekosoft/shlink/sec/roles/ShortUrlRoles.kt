package org.nekosoft.shlink.sec.roles

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('ShortUrls:Viewer')")
annotation class IsShortUrlViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('ShortUrls:Viewer') and (!#options.withStats or hasRole('ShortUrls:StatsViewer'))")
annotation class IsShortUrlStatsViewer

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('ShortUrls:Editor')")
annotation class IsShortUrlEditor

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('ShortUrls:Admin')")
annotation class IsShortUrlAdmin
