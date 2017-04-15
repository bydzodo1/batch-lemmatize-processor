package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import java.nio.file.Path

class CommandLineExecutor {

    fun execute(command: String, tempDir: Path) {
        val r = Runtime.getRuntime()
        val p = r.exec(command, null, tempDir.toFile())
        p.waitFor()
    }
}