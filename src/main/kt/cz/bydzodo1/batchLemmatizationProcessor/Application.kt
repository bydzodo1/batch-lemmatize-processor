package main.kt.cz.bydzodo1.batchLemmatizationProcessor

import cz.bydzodo1.batchLemmatizationProcessor.CommandLineExecutor.CommandLineExecutor
import cz.bydzodo1.batchLemmatizationProcessor.Settings
import org.slf4j.LoggerFactory

open class Application {

    val logger = LoggerFactory.getLogger(javaClass)
    lateinit var settings: Settings

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            Application().init(args)
        }
    }

    open fun init(args: Array<String>) {
        logger.debug("Starting application")
        settings = Settings.getSettings(args)
        println(CommandLineExecutor().execute("echo asdfasdf"))
    }
}