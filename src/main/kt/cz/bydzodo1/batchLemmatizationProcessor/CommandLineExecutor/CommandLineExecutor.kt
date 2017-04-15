package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import org.springframework.stereotype.Component
import java.io.InputStreamReader


@Component
class CommandLineExecutor {

    fun execute(command: String): String {
        val p = Runtime.getRuntime().exec(command)
        return InputStreamReader(p.inputStream).readText()
    }
}