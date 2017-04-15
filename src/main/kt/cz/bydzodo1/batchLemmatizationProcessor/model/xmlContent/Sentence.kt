package cz.bydzodo1.batchLemmatizationProcessor.model.xmlContent

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

@XmlAccessorType(XmlAccessType.FIELD)
class Sentence {

    @XmlElement(name = "token")
    lateinit var tokens: List<Token>
}