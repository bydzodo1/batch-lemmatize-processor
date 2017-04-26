package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import org.apache.commons.lang3.SystemUtils
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.io.PrintWriter
import java.util.*


class CommandLineExecutor {

    fun execute(command: String, index: Int) {
        val randUuid = UUID.randomUUID().toString()
        val file = File.createTempFile(randUuid, if (SystemUtils.IS_OS_WINDOWS) ".bat" else if(SystemUtils.IS_OS_LINUX) ".sh" else ".tmp")
        file.setExecutable(true)

        createFileCommand(command, index)

        val writer = PrintWriter(file, "UTF-8")
        writer.print(command)
        writer.close()
        val r = Runtime.getRuntime()
        val p = r.exec(file.absolutePath)
        val inputStream: InputStream = BufferedInputStream(p.inputStream)
        println("CMD $index output is: " + inputStream.read())
        p.waitFor()
        if (p.exitValue() == 0) {
            println("Command $index exit successfully")
        } else {
            println("Command $index failed")
        }
    }

    private fun createFileCommand(command: String, index: Int){
        val commandName = "command$index" + if (SystemUtils.IS_OS_WINDOWS) ".bat" else if(SystemUtils.IS_OS_LINUX)".sh" else ".tmp"
        val demonstrateFile = File(commandName)
        demonstrateFile.setExecutable(true)
        val demonstratewriter = PrintWriter(demonstrateFile, "UTF-8")
        demonstratewriter.print(command)
        demonstratewriter.close()
        println("Command $index was created. You can try launch it. The path is: " + demonstrateFile.absoluteFile)
    }
}