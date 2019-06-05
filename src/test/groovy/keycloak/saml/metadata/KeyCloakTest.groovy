package keycloak.saml.metadata

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class KeyCloakTest extends Specification {

    KeyCloak keyCloak
    XmlHelper xmlHelper

    def setup() {
        keyCloak = new KeyCloak()
        xmlHelper = new XmlHelper()
    }


    @Ignore
    def "GetSPDescriptor"() {
    }

    @Unroll
    def "should parse #xmlFile"(xmlFile) {
        given:
        SamlModel samlModel = keyCloak.getModel(xmlFile, "http://example.com:1234/bonita")

        when:
        def xmlContent = keyCloak.fromModel(samlModel)

        then:
        println(xmlContent)
//        xmlHelper.isValidAgainstXSD(xmlContent, XmlHelper.METADATA_XSD_2_0)
        xmlContent == "*"

        where:
        xmlFile <<["keycloak-saml.xml","keycloak-example.xml"]
    }
}
