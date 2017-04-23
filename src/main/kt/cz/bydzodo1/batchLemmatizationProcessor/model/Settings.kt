package cz.bydzodo1.batchLemmatizationProcessor.model


class Settings private constructor(val args: Array<String>) {
    var MORPHODITA_OPTIONS = "--input untokenized --output xml"

    var morphoDiTaRunTaggerPath: String = ""
    var taggerDataFile = ""
    var inputFolder = ""
    var outputFile = ""

    companion object{
        fun getSettings(args: Array<String>): Settings {
            val settings = Settings(args)
            settings.processParams()
            return settings
        }
    }

    private fun processParams(){
        morphoDiTaRunTaggerPath = args[0]
        taggerDataFile = args[1]
        inputFolder = args[2]
        if (args.size > 3) {
            outputFile = args[3]
        }
    }
}