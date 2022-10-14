package org.nekosoft.shlink.vo;

import com.fasterxml.jackson.annotation.JsonIgnore
import org.nekosoft.shlink.entity.Domain
import picocli.CommandLine

data class DomainEditMeta(

    @field:CommandLine.Parameters(index = "0")
    @field:JsonIgnore // not used in JSON - URL Path Variable provides this information
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
