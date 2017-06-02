package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import cz.bydzodo1.batchLemmatizationProcessor.util.SystemUtils
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.io.PrintWriter
import java.util.*


class CommandLineExecutor {

    fun execute(command: String, index: Int) {
        val randUuid = UUID.randomUUID().toString()
        val file = File.createTempFile(randUuid, if (SystemUtils.isWindows()) ".bat" else if(SystemUtils.isUnix()) ".sh" else ".tmp")
        file.setExecutable(true)

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


}