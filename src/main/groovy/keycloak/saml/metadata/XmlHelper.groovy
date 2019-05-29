package keycloak.saml.metadata

import org.codehaus.groovy.runtime.InvokerHelper

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import static javax.xml.transform.TransformerFactory.newInstance

class XmlHelper {

    public static final String XSD_1_6 = "/xsd/1.6.xsd"

    def isValidAgainstXSD(String xmlContent, String xsdFileName) {
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(new StreamSource(this.getClass().getResourceAsStream(xsdFileName)))
                .newValidator()
                .validate(new StreamSource(new StringReader(xmlContent)))
        true
    }

    def applyXsl(String xmlAsText, String xslFileName) {
        def transformer = newInstance().newTransformer(new StreamSource(this.getClass().getResourceAsStream(xslFileName)))
        StringWriter stringWriter = new StringWriter()
        StringReader stringReader = new StringReader(xmlAsText )
        def source = new StreamSource(stringReader)
        def result = new StreamResult(stringWriter)
        transformer.transform(source, result)
        stringWriter.toString()
    }


}
