package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import java.nio.file.Path

class ListCommandsLineExecutor {

    fun execute(commands: List<String>) {
        val pb = ProcessBuilder(commands)
        val p = pb.start()
        p.waitFor()
    }
}


