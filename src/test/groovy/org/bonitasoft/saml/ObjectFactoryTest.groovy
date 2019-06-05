package org.bonitasoft.saml

import spock.lang.Specification

import javax.xml.bind.JAXB
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.bind.Unmarshaller

class ObjectFactoryTest extends Specification {

    def "jaxbTest"() {
        given:
        def instance = ObjectFactory.newInstance()
        def xmlContent = new File(this.class.getResource("/keycloak-saml.xml").file).text

        when:
        def adapterType = instance.createAdapterType()
        def adapter = instance.createKeycloakSamlAdapter(adapterType)
        JAXBContext context = JAXBContext.newInstance(AdapterType.class)

//        def unmarshaller = context.createUnmarshaller()
        def marshaller = context.createMarshaller()
//        def unmarshal = unmarshaller.unmarshal(new StringReader(xmlContent))
        StringWriter stringWriter = new StringWriter()
        marshaller.marshal(adapter, stringWriter)
        def toString = stringWriter.toString()
        println(toString)

        then:
        toString == null
    }
}
