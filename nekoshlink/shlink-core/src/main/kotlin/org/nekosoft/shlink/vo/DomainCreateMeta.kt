package org.nekosoft.shlink.vo;

import org.nekosoft.shlink.entity.Domain
import picocli.CommandLine

data class DomainCreateMeta(

    @field:CommandLine.Parameters(index = "0")
    var authority: String = Domain.DEFAULT_DOMAIN,

    @field:CommandLine.Option(names = ["--scheme"], defaultValue = "https")
    var scheme: String = "https",

    @field:CommandLine.Option(names = ["--base"])
    var baseUrlRedirect: String? = null,

    @field:CommandLine.Option(names = ["--req-error"])
    var requestErrorRedirect: String? = null,

    @field:CommandLine.Option(names = ["--pwd-form"])
    var passwordFormRedirect: String? = null,

)
