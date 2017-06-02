package cz.bydzodo1.batchLemmatizationProcessor.model

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class CommandProvider(val settings: Settings) {
    fun getCommands(files: Array<File>, pairs: MutableList<Pair<String, Path>>, tempFile: Path): ArrayList<String> {
        val commands = arrayListOf<String>()
        var commandBuilder = getStringBuilder()
        var builderBuilded = false

        files.forEach {
            builderBuilded = false
            val newTempFileName = tempFile.toAbsolutePath().toString() + File.separator + "tmp"+ it.name.hashCode() + ".txt"
            val pathNewTempFile = Paths.get(newTempFileName)
//            Files.createFile(pathNewTempFile)
            val command = "\"${it.absoluteFile}\":\"$newTempFileName\""
            commandBuilder.append(" ").append(command)
            pairs.add(Pair(it.name, pathNewTempFile))
            if (commandBuilder.length > 7000){
                commands.add(commandBuilder.toString())
                commandBuilder = getStringBuilder()
                builderBuilded = true
            }
        }
        if (!builderBuilded){
            commands.add(commandBuilder.toString())
        }
        return commands
    }

    private fun getStringBuilder(): StringBuilder{
        return StringBuilder("${settings.morphoDiTaRunTaggerPath} ${settings.MORPHODITA_OPTIONS} ${settings.taggerDataFile}")
    }
}