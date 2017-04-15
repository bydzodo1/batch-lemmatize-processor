package cz.bydzodo1.batchLemmatizationProcessor.model.xmlContent

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement


@XmlRootElement(name="sentences")
@XmlAccessorType(XmlAccessType.FIELD)
class Sentences {

    @XmlElement(name = "sentence")
    lateinit var sentences: List<Sentence>
}