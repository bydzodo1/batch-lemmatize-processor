package cz.bydzodo1.batchLemmatizationProcessor.model

import cz.bydzodo1.batchLemmatizationProcessor.model.xmlContent.Sentences
import java.io.InputStreamReader
import javax.xml.bind.JAXBContext


class CommandResultProvider {

    fun getCommandResult(xmlInput: InputStreamReader): CommandResult {
        val text = "<sentences>" + xmlInput.readText() + "</sentences>"
        val inputStream = InputStreamReader(text.byteInputStream())

        val jc = JAXBContext.newInstance(Sentences::class.java)
        val unmarshaller = jc.createUnmarshaller()
        val sentences = unmarshaller.unmarshal(inputStream) as Sentences
        return CommandResult(sentences.sentences)
    }

}