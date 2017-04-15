package cz.bydzodo1.batchLemmatizationProcessor.logger


class CustomLogger private constructor(){
    companion object{
        fun getInstance(): CustomLogger{
            return CustomLogger()
        }
    }

    fun appInfo(info: String){
        println(info)
    }

    fun error(message: String){
        println("!! Some error occurred !!: $message")
    }

    fun emptyLine(){
        println()
    }

    fun exit(message: String){
        println("Exiting the application")
        println("****: " + message)
    }

    fun processing(message: String){
        println("x_ "+message)
    }
}
