package org.nekosoft.shlink.cli

import org.nekosoft.shlink.cli.subcommands.*
import picocli.CommandLine
import java.io.File

@CommandLine.Command(
    name = "nkshlink",
    mixinStandardHelpOptions = true,
    version = ["neko-shlink 1.0.0"],
    description = ["A command line interface for NekoShlink, a URL shortener written in Kotlin with Spring Boot"],
    subcommands = [
        ShortUrlSubcommand::class,
        TagSubcommand::class,
        DomainSubcommand::class,
        VisitSubcommand::class,
        RunSubcommand::class,
    ]
)
class ShlinkCommand {

    @CommandLine.ArgGroup(exclusive = true, heading = "Misc Authentication Options%n", multiplicity = "0..1")
    lateinit var authOptions: AuthenticationOptions

    class AuthenticationOptions {
        @CommandLine.ArgGroup(exclusive = false, heading = "Username and Password%n", multiplicity = "1")
        var usrpwdOptions: UserPasswordOptions? = null

        @field:CommandLine.Option(names = ["--access-token-file"], arity = "1", echo = true, description = ["The file containing the OAuth2 Access Token for authorization."])
        var accessTokenFile: File? = null

        @field:CommandLine.Option(names = ["--api-key"], interactive = true, echo = false, description = ["The API Key to use for submitting API requests. Mostly used for compatibility with the shlink.io web app."])
        var apiKey: String? = null
    }

    class UserPasswordOptions {
        @field:CommandLine.Option(names = ["--usr"], interactive = true, arity = "0..1", echo = true, description = ["The username to log in as. If the argument is empty, it can be entered interactively."])
        var authUser: String? = null

        @field:CommandLine.Option(names = ["--pwd"], interactive = true, arity = "0..1", echo = false, description = ["The password for the given username. If the argument is empty, it can be entered interactively, but typed text will not show on the screen."])
        var authPassword: String? = null
    }

}
