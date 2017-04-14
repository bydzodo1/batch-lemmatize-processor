package cz.bydzodo1.batchLemmatizationProcessor


class Settings private constructor(args: Array<String>) {
    var MORPHODITA_OPTIONS = "--input untokenized --output xml"

    var morphoDiTaRunTaggerPath: String = ""
    var taggerDataFile = ""
    var inputFolder = ""

    companion object{
        fun getSettings(args: Array<String>): Settings{
            val settings = Settings(args)
            settings.processParams()
            return settings
        }
    }

    private fun processParams(){


    }

    /*
    run_tagger.exe =../czech-tagger/czech-morfflex-pdt-161115.tagger C:/ukazky/texty/keZlemmatizovani.txt > D:/texty/vystup/zlemmatizovane.txt
     */
}