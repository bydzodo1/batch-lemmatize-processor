package main.kt.cz.bydzodo1.batchLemmatizationProcessor

import cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor.CommandLineExecutor
import cz.bydzodo1.batchLemmatizationProcessor.logger.CustomLogger
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandProvider
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandResult
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandResultProvider
import cz.bydzodo1.batchLemmatizationProcessor.model.Settings
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.util.*


open class Application {

    lateinit var settings: Settings

    val logger = CustomLogger.getInstance()

    val commandResults = mutableListOf<CommandResult>()

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val start = Date().time
            val app = Application()
            app.init(args)

            val end = Date().time
            val interval = end - start
            app.shutdown("Everything is done for $interval ms, good bye", true)
        }
    }

    // first arg is morphodita run_tagger, second is tagger file, third is dir with input files
    open fun init(args: Array<String>) {
        val params = arrayOf("/home/dominik/repository/batch-lemmatize-processor/bin-linux64/run_tagger", "/home/dominik/repository/batch-lemmatize-processor/czech-tagger/czech-morfflex-pdt-161115.tagger", "/home/dominik/repository/batch-lemmatize-processor/inputs")
        if (params.any({ it == "help" })) {
            help()
            shutdown("help", false)
        }
        if (params.size < 3) {
            help()
            shutdown("There are no all three params. Try to pass 'help'", false)
        }
        logger.appInfo("starting Batch Lemmatization Processor")
        logger.appInfo("actual folder is " + getPath())
        logger.emptyLine()
        logger.appInfo("MorphoDiTa run_tagger file       : " + params[0])
        logger.appInfo("MorphoDiTa tagger data model file: " + params[1])
        logger.appInfo("Folder with input files          : " + params[2])
        if (params.size > 3)
            logger.appInfo("Output File is                   : " + params[3])
        settings = Settings.getSettings(params)

        val folder = File(settings.inputFolder)
        val files = folder.listFiles()
        if (files.isEmpty()) {
            shutdown("There are no files to lemmatize in input folder")
        }

        logger.appInfo("There are ${files.size} to lemmatize")

        val commandResultProvider = CommandResultProvider()
        val commandLineExecutor = CommandLineExecutor()
        val commandProvider = CommandProvider(settings)
        files.forEach {
            logger.processing("Processing file ${it.name}")
            val command = commandProvider.getCommand(it)
            val executed = commandLineExecutor.execute(command)
            val commandResult = commandResultProvider.getCommandResult(executed)
            commandResults.add(commandResult)
        }

    }

    private fun getPath(): Path {
        val currentRelativePath = Paths.get("")
        return currentRelativePath.toAbsolutePath()
    }

    private fun shutdown(message: String, quickly: Boolean = false) {
        logger.exit(message)
        if (!quickly) {
            logger.appInfo("exit? (press enter)")
            val reader = BufferedReader(InputStreamReader(System.`in`))
            reader.readLine()
        }
        System.exit(0)
    }

    private fun help() {
        logger.appInfo("please follow this: java -jar run_wrapper.jar run_tagger datamodel.tagger inputFolder [outputFile]")
    }
}