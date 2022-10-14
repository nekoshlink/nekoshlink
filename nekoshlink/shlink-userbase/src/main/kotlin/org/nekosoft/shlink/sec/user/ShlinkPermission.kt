package org.nekosoft.shlink.sec.user

enum class ShlinkPrivilege {
    Admin,
    Editor,
    StatsViewer,
    Viewer,
}

enum class ShlinkRealm {
    Everything,
    Domains,
    ShortUrls,
    Tags,
    Visits,
    Users,
    Management, // for access to actuator endpoints
}
