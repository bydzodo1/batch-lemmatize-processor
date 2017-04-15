package cz.bydzodo1.batchLemmatizationProcessor.model.generatingOutput

import cz.bydzodo1.batchLemmatizationProcessor.model.CommandResult
import cz.bydzodo1.batchLemmatizationProcessor.model.xmlContent.Token
import java.io.PrintWriter


class OutputFileProvider {

    val sep = ";"

    val writer = PrintWriter("output.csv", "UTF-8")
    var columns: MutableList<Column> = mutableListOf()

    fun outputFile(commandResults: HashMap<String, CommandResult>) {
        createColumns(commandResults.map { it.value })

        writeHeader()
        writeBody(commandResults)

        writer.close()
    }

    private fun writeHeader(){
        writer.print("NAZEV${sep}POVET VET${sep}POCET SLOV")
        columns.forEach({
            writer.print(sep + "${it.index + 1}:${it.char}")
        })
        writer.println()
    }

    private fun writeBody(commandResults: HashMap<String, CommandResult>){
        commandResults.forEach({
            writeRow(it.key, it.value)
        })
    }

    private fun writeRow(fileName: String, commandResult: CommandResult){
        val allTokens = mutableListOf<Token>()
        commandResult.sentences.forEach({allTokens.addAll(it.tokens)})

        writer.print(fileName + sep + commandResult.sentences.size + sep + allTokens.size)
        columns.forEach({
            val column = it
            val count = allTokens.map(Token::getTags).filter { it[column.index] == column.char }.size
            writer.print(sep + count)
        })
        writer.println()
    }


    private fun createColumns(commandResults: List<CommandResult>){
        val allTokens = mutableListOf<Token>()
        commandResults.forEach({it.sentences.forEach({allTokens.addAll(it.tokens)})})

        val tagColumns = HashMap<Int, MutableSet<Char>>()
        allTokens.forEach({
            for (i in it.getTags().indices) {
                if (tagColumns[i] == null){
                    tagColumns[i] = mutableSetOf()
                }
                tagColumns[i]!!.add(it.tag[i])
            }
        })
        tagColumns.forEach({
            val index = it.key
            val tags = it.value
            for(tag in tags){
                columns.add(Column(index, tag))
            }
        })

    }

}