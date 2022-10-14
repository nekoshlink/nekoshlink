package org.nekosoft.shlink.cli.subcommands

import org.nekosoft.shlink.cli.NkShlinkScripting
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.springframework.stereotype.Component
import picocli.CommandLine
import java.io.File
import java.io.FileReader
import java.util.concurrent.Callable
import javax.script.ScriptEngineManager

/**
 * Runs a JavaScript file that has access to the `nkshlink` function.
 *
 * The `nkshlink` function is the equivalent of the CLI tool. You can invoke
 * any subcommand of the CLI with all the parameters that you have available in the CLI.
 * The main difference is that you can chain several commands in the same file and of
 * course you can leverage the full power of the JavaScript language in your scripts.
 * Here is an example of a call to the `nkshlink` function to create a new domain.
 * ```
 * nkshlink("--usr nekoadm --pwd password domains create nksf.link --scheme https");
 * ```
 * This command only takes one positional parameter, the name of the script. If no extension
 * is given, and the provided path cannot be read, the command will try adding the default
 * extension `.nkshl` before failing.
 */
@CommandLine.Command(
    name = "run",
    description = ["Execute a NekoShlink script in JavaScript with access to the nkshlink object"],
    mixinStandardHelpOptions = true,
)
@Component
class RunSubcommand(
    val nkshlinkScripting: NkShlinkScripting,
) : Callable<Int> {

    @CommandLine.Parameters(index = "0", description = ["The name of the JavaScript file to run. If no extension is given, '.nkshl' will be tried."])
    lateinit var scriptFile: File

    override fun call(): Int {
        val manager = ScriptEngineManager(null)
        manager.registerEngineName("JavaScript", NashornScriptEngineFactory())
        val engine = manager.getEngineByName("JavaScript")
        engine.put("nkshAuth", nkshlinkScripting.authOptionsSetter)
        engine.put("nkshlink", nkshlinkScripting)
        if (!scriptFile.canRead()) {
            scriptFile = File(scriptFile.toString().plus(".nkshl"))
        }
        engine.eval(FileReader(scriptFile))
        return 0
    }
}
