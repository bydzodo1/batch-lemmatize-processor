package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import org.springframework.stereotype.Component
import java.io.InputStreamReader


@Component
class CommandLineExecutor {

    fun execute(command: String) {
        val r = Runtime.getRuntime()
        val p = r.exec(command)
        p.waitFor()
    }
}