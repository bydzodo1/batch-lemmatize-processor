package cz.bydzodo1.batchLemmatizationProcessor.model

import cz.bydzodo1.batchLemmatizationProcessor.model.xmlContent.Sentence
import cz.bydzodo1.batchLemmatizationProcessor.model.xmlContent.Sentences
import java.io.InputStreamReader
import javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller


class CommandResultProvider {

    fun getCommandResult(xmlInput: InputStreamReader): CommandResult {
        val jc = JAXBContext.newInstance(Sentences::class.java)
        val unmarshaller = jc.createUnmarshaller()
        val sentences = unmarshaller.unmarshal(xmlInput) as Sentences

        return CommandResult(sentences.sentences)

    }

}