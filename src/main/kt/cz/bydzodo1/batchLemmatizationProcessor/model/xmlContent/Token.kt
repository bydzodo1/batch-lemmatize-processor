package cz.bydzodo1.batchLemmatizationProcessor.model.xmlContent

import javax.xml.bind.annotation.*

//<token lemma="onout" tag="VB-S---3P-AA---">one</token>
@XmlAccessorType(XmlAccessType.FIELD)
class Token {

    @XmlValue
    lateinit var token: String

    @XmlAttribute(name = "lemma")
    lateinit var lemma: String

    @XmlAttribute(name = "tag")
    lateinit var tag: String
}