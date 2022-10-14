package org.nekosoft.shlink.cli

import org.nekosoft.shlink.cli.ShlinkCommand.AuthenticationOptions
import org.nekosoft.utils.text.tokenizeCommandLineString
import org.openjdk.nashorn.api.scripting.AbstractJSObject
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File

@Component
class NkShlinkScripting(
    val runner: CommandLineRunner,
): AbstractJSObject() {

    override fun isFunction(): Boolean {
        return true
    }

    override fun call(thiz: Any?, vararg args: Any?): Any {
        if (args.size != 1) throw IllegalArgumentException("Only one string argument is expected")
        return execute(args[0].toString())
    }

    val authOptions: AuthenticationOptions = AuthenticationOptions()
    val authOptionsSetter: AuthOptionsSetter = AuthOptionsSetter()

    inner class AuthOptionsSetter {

        fun withUsernamePassword(username: String, password: String) {
            authOptions.usrpwdOptions = ShlinkCommand.UserPasswordOptions()
            authOptions.usrpwdOptions!!.authUser = username
            authOptions.usrpwdOptions!!.authPassword = password
            authOptions.apiKey = null
            authOptions.accessTokenFile = null
        }

        fun withApiKey(apiKey: String) {
            authOptions.apiKey = apiKey
            authOptions.usrpwdOptions = null
            authOptions.accessTokenFile = null
        }

        fun withAccessTokenFile(file: String) {
            authOptions.accessTokenFile = File(file)
            authOptions.apiKey = null
            authOptions.usrpwdOptions = null
        }
    }

    fun execute(command: String) {
        val args = tokenizeCommandLineString(command).toMutableList()
        if (!args.contains("--usr") && !args.contains("--pwd") && !args.contains("--access-token-file") && !args.contains("--api-key")) {
            if (authOptions.usrpwdOptions != null) {
                args.add(0, "--pwd=${authOptions.usrpwdOptions!!.authPassword ?: ""}")
                args.add(0, "--usr=${authOptions.usrpwdOptions!!.authUser ?: ""}")
            } else if (authOptions.accessTokenFile != null) {
                args.add(0, "--access-token-file=${authOptions.accessTokenFile}")
            } else if (authOptions.apiKey != null) {
                args.add(0, "--api-key=${authOptions.apiKey}")
            }
        }
        runner.run(*args.toTypedArray())
    }

}
