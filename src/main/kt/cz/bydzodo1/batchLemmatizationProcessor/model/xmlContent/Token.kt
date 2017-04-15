package cz.bydzodo1.batchLemmatizationProcessor.model.xmlContent

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlValue

@XmlAccessorType(XmlAccessType.FIELD)
class Token {
    @XmlValue
    lateinit var token: String

    @XmlAttribute(name = "lemma")
    lateinit var lemma: String

    @XmlAttribute(name = "tag")
    lateinit var tag: String

    fun getTag(index: Int): Char{
        if (index > 14) throw IllegalArgumentException("Index ${index} is not allowed here. There are only 15 tags")
        return tag[index]
    }

    fun getTags() :List<Char>{
        return tag.toList()
    }
}
