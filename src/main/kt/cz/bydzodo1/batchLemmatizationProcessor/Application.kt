package cz.bydzodo1.batchLemmatizationProcessor

import cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor.CommandLineExecutor
import cz.bydzodo1.batchLemmatizationProcessor.logger.CustomLogger
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandProvider
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandResult
import cz.bydzodo1.batchLemmatizationProcessor.model.CommandResultProvider
import cz.bydzodo1.batchLemmatizationProcessor.model.Settings
import cz.bydzodo1.batchLemmatizationProcessor.model.generatingOutput.OutputFileProvider
import cz.bydzodo1.batchLemmatizationProcessor.util.SystemUtils
import java.io.*
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.close
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.BufferedReader
import java.io.InputStreamReader


open class Application {

    private val TEMP_DIR_ATTEMPTS = 10000
    lateinit var settings: Settings
    lateinit var files: Array<File>

    val logger = CustomLogger.getInstance()

    val commandResults = HashMap<String, CommandResult>()
    val commandResultProvider = CommandResultProvider()
    val commandLineExecutor = CommandLineExecutor()
    val outputFileProvider = OutputFileProvider()
    val deleteMeAtEnd: MutableList<File> = mutableListOf()

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
            deleteMeAtEnd.forEach {
                it.delete()
            }
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

        logger.emptyLine()
        println("please, run these script and then press enter")

        val fileNames = arrayListOf<String>()
        commands.forEach({
            val index = commands.indexOf(it) + 1
            fileNames.add(createFileCommand(it, index))
        })
        println(createFileCommand(fileNames.joinToString(" && "), 0))
        logger.emptyLine()

        println("press enter to continue ...")
        System.`in`.read()

        logger.emptyLine()

        var success = true
        pairs.forEach({
            val fileName = it.first
            val tempFilePath = it.second
            try {
                val inputStream = InputStreamReader(tempFilePath.toFile().inputStream())
                val commandResult = commandResultProvider.getCommandResult(inputStream)
                commandResults.put(fileName, commandResult)
            } catch (e: FileNotFoundException) {
                logger.error(e.localizedMessage)
                success = false
                return
            } catch (e: UninitializedPropertyAccessException) {
                success = false
                logger.error("It was not able to process file. It is empty maybe")
                return
            } catch (e: Exception){
                success = false
                return
            }
        })
        if (success){
            val interval = Date().time - start
            logger.appInfo("All files have been successfully lemmatized to ${interval}ms. Let's make a report")
            if (settings.outputFile != "") {
                outputFileProvider.outputFile(commandResults, File(settings.outputFile))
            } else {
                outputFileProvider.outputFile(commandResults)
            }
        } else {
            logger.appInfo("Some error occurred. No output will be generated")
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

    private fun createFileCommand(command: String, index: Int): String {
        val commandName = "command$index" + if (SystemUtils.isWindows()) ".bat" else if(SystemUtils.isUnix())".sh" else ".tmp"
        val demonstrateFile = File(commandName)
        demonstrateFile.setExecutable(true)
        val demonstratewriter = PrintWriter(demonstrateFile, "UTF-8")
        demonstratewriter.print(command)
        demonstratewriter.close()
        deleteMeAtEnd.add(demonstrateFile)
        return demonstrateFile.absoluteFile.toString()
    }
}