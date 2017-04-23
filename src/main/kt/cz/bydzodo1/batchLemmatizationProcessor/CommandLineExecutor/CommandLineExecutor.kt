package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import org.apache.commons.lang3.SystemUtils;
import java.io.File
import java.nio.file.Path
import java.io.PrintWriter
import java.util.*


class CommandLineExecutor {

    fun execute(command: String, index: Int) {
        val randUuid = UUID.randomUUID().toString()
        val file = File.createTempFile(randUuid, if (SystemUtils.IS_OS_WINDOWS) ".bat" else if(SystemUtils.IS_OS_LINUX) ".sh" else ".tmp")
        file.setExecutable(true)

        val writer = PrintWriter(file, "UTF-8")
        writer.print(command)
        writer.close()
        val r = Runtime.getRuntime()
        val p = r.exec(file.absolutePath)
        p.waitFor()
    }
}