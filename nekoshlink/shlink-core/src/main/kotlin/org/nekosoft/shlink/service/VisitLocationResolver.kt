package org.nekosoft.shlink.service

import org.nekosoft.shlink.entity.support.VisitLocation
import javax.servlet.http.HttpServletRequest

interface VisitLocationResolver {
    fun extractLocationInformation(request: HttpServletRequest): VisitLocation
}
