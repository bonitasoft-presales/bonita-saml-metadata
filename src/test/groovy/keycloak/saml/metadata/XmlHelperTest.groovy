package keycloak.saml.metadata

import spock.lang.Specification

import static keycloak.saml.metadata.XmlHelper.XSD_1_6

class XmlHelperTest extends Specification {

    def "should #xmlFile validate XSD"(String xmlFile) {
        setup:
        XmlHelper xmlHelper = new XmlHelper()
        def xmlContent = new File(this.class.getResource("/$xmlFile").file).text

        when:
        def result = xmlHelper.isValidAgainstXSD(xmlContent, XSD_1_6)

        then:
        result

        where:
        xmlFile << ["keycloak-saml.xml"]


    }

    def "should apply xsl"(String xmlFile) {
        setup:
        XmlHelper xmlHelper = new XmlHelper()
        String xmlContent = new File(this.class.getResource("/$xmlFile").file).text

        when:
        def result = xmlHelper.applyXsl(xmlContent,"/xsl/keycloak-saml_to_metadata.xsl")

        then:
        result == "*"

        where:
        xmlFile << ["keycloak-saml.xml"]


    }
}
