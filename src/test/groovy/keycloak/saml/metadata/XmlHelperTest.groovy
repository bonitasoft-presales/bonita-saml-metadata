package keycloak.saml.metadata

import spock.lang.Specification
import spock.lang.Unroll

import static keycloak.saml.metadata.XmlHelper.*

class XmlHelperTest extends Specification {

    @Unroll
    def "should #xmlFile validate XSD"(String xmlFile, String xsdFile) {
        setup:
        XmlHelper xmlHelper = new XmlHelper()
        def xmlContent = new File(this.class.getResource("/$xmlFile").file).text

        when:
        def result = xmlHelper.isValidAgainstXSD(xmlContent, xsdFile)

        then:
        result

        where:
        xmlFile                | xsdFile
        "keycloak-saml.xml"    | KEYCLOAK_XSD_1_6
        "keycloak-example.xml" | KEYCLOAK_XSD_1_6
        "sp-metadata.xml"          | METADATA_XSD_2_0


    }

    def "should apply xsl"(String xmlFile) {
        setup:
        XmlHelper xmlHelper = new XmlHelper()
        String xmlContent = new File(this.class.getResource("/$xmlFile").file).text

        when:
        def result = xmlHelper.applyXsl(xmlContent, "/xsl/keycloak-saml_to_metadata.xsl")

        then:
        result == "*"

        where:
        xmlFile << ["keycloak-saml.xml"]


    }
}
