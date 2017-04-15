package cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor

import java.io.File
import java.nio.file.Path
import java.io.PrintWriter



class CommandLineExecutor {

    fun execute(command: String, index: Int) {
        val values = arrayListOf("a","b","c","d","e","f","g","h","i","j")
        val scriptName = "script${values[index-1]}.sh"
        val file = File(scriptName)
        file.setExecutable(true)
        val writer = PrintWriter(file, "UTF-8")
        writer.print(command)
        writer.close()

        val r = Runtime.getRuntime()
        val p = r.exec("./"+scriptName)
        p.waitFor()
    }
}