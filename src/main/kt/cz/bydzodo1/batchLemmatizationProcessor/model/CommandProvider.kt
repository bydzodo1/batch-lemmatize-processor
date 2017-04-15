package cz.bydzodo1.batchLemmatizationProcessor.model

import java.io.File

/**
 * Created by dominik on 15.4.17.
 */
class CommandProvider(val settings: Settings) {
    fun getCommand(it: File):String{
        return "${settings.morphoDiTaRunTaggerPath} ${settings.MORPHODITA_OPTIONS} ${settings.taggerDataFile} ${it.absoluteFile}"
    }
}