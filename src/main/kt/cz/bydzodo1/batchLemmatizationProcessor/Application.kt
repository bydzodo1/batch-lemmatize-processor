package main.kt.cz.bydzodo1.batchLemmatizationProcessor

import cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor.CommandLineExecutor
import cz.bydzodo1.batchLemmatizationProcessor.logger.CustomLogger
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandProvider
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandResult
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandResultProvider
import cz.bydzodo1.batchLemmatizationProcessor.model.Settings
import cz.bydzodo1.batchLemmatizationProcessor.model.generatingOutput.OutputFileProvider
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap


open class Application {

    private val TEMP_DIR_ATTEMPTS = 10000
    lateinit var settings: Settings
    lateinit var files: Array<File>

    val logger = CustomLogger.getInstance()

    val commandResults = HashMap<String, CommandResult>()
    val commandResultProvider = CommandResultProvider()
    val commandLineExecutor = CommandLineExecutor()
    val outputFileProvider = OutputFileProvider()

    lateinit var tempDir: File

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val start = Date().time
            val app = Application()
            app.start(args)

            val end = Date().time
            val interval = end - start
            app.shutdown("Everything is done for $interval ms, good bye", true)
        }
    }

    open fun start(args: Array<String>) {
        try {
            tempDir = createTempDir()
            this.run(args)
        } catch (e: Exception) {
            throw e
        } catch (e: IllegalStateException) {
            println(e.localizedMessage)
        } finally {
            tempDir.deleteRecursively()
        }
    }

    // first arg is morphodita run_tagger, second is tagger file, third is dir with input files, forth optional output file
    open fun run(args: Array<String>) {
        val start = Date().time

        try {
            Files.createDirectory(tempDir.toPath())
        } catch (e: FileAlreadyExistsException) {
            File(tempDir.absolutePath).listFiles()?.forEach { it.delete() }
        }
        File(tempDir.absolutePath).setWritable(true)
        File(tempDir.absolutePath).setExecutable(true)

        readParams(args)
        readFilesToLemmatize()
        logger.appInfo("There are ${files.size} files to lemmatize")

        val commandProvider = CommandProvider(settings)

        val pairs = mutableListOf<Pair<String, Path>>()
        val commands = commandProvider.getCommands(files, pairs, tempDir.toPath())

        logger.processing("Processing files")
        logger.processing("temp dir " + tempDir.absolutePath)

        if (commands.size > 1){
            logger.processing("Running ${commands.size} commands in parallel")
        }
        logger.emptyLine()
        commands.parallelStream().forEach({
            val index = commands.indexOf(it) + 1
            logger.processing("Running command ($index/${commands.size}) which length is ${it.length}")
            commandLineExecutor.execute(it, index)
            logger.processing("Command $index done")
        })
        logger.emptyLine()

        println("Going to process lemmatization results")
        pairs.forEach({
            val fileName = it.first
            val tempFilePath = it.second

            try {
                val inputStream = InputStreamReader(tempFilePath.toFile().inputStream())
                val commandResult = commandResultProvider.getCommandResult(inputStream)
                commandResults.put(fileName, commandResult)
            } catch (e: FileNotFoundException) {
                logger.error(e.localizedMessage)
            } catch (e: UninitializedPropertyAccessException) {
                logger.error("It was not able to process file. It is empty maybe")
            }

        })
        val interval = Date().time - start
        logger.appInfo("All files have been successfully lemmatized to ${interval}ms. Let's make a report")
        logger.appInfo("There are ${commandResults.size} results found")
        if (settings.outputFile != "") {
            outputFileProvider.outputFile(commandResults, File(settings.outputFile))
        } else {
            outputFileProvider.outputFile(commandResults)
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
        tempDir.deleteRecursively()
        System.exit(0)
    }

    private fun help() {
        logger.appInfo("please follow this: java -jar run_wrapper.jar run_tagger(morphodita tools) datamodel.tagger(morphodita data model) inputFolder [outputFile]")
    }

    fun readParams(args: Array<String>) {
        val params = args
//        val params = arrayOf("/home/dominik/repository/batch-lemmatize-processor/bin-linux64/run_tagger", "/home/dominik/repository/batch-lemmatize-processor/czech-tagger/czech-morfflex-pdt-161115.tagger", "/home/dominik/repository/batch-lemmatize-processor/inputs")
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
    }


    fun readFilesToLemmatize() {
        val folder = File(settings.inputFolder)
        files = folder.listFiles()
        if (files.isEmpty()) {
            shutdown("There are no files to lemmatize in input folder")
        }
    }


    fun createTempDir(): File {
        val baseDir = File(System.getProperty("java.io.tmpdir"))
        val baseName = System.currentTimeMillis().toString() + "-"

        for (counter in 0..TEMP_DIR_ATTEMPTS - 1) {
            val tempDir = File(baseDir, baseName + counter)
            if (tempDir.mkdir()) {
                return tempDir
            }
        }
        throw IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')')
    }
}