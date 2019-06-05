package keycloak.saml.metadata


import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

import static javax.xml.transform.TransformerFactory.newInstance

class XmlHelper {

    public static final String KEYCLOAK_XSD_1_6 = "/xsd/1.6.xsd"
    public static final String METADATA_XSD_2_0 = "/xsd/saml-schema-metadata-2.0.xsd"

    def isValidAgainstXSD(String xmlContent, String xsdFileName) {
        def xsdContent=new File(this.class.getResource(xsdFileName).file).text
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(new StreamSource(new StringReader(xsdContent)))
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
