package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader


@Component
class CommandLineExecutor {

    fun execute(command: String, commandID: Int) {
        val r = Runtime.getRuntime()
        val p = r.exec(command)
        val reader = BufferedReader(InputStreamReader(p.getInputStream()))
        p.waitFor()
        println("Command $commandID: " + reader.readText())
    }
}