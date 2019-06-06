package keycloak.saml.metadata

import spock.lang.Specification
import spock.lang.Unroll

class KeyCloakTest extends Specification {

    KeyCloak keyCloak

    def setup() {
        keyCloak = new KeyCloak()
    }


    @Unroll
    def "should parse #xmlFile"(xmlFile) {
        given:
        SamlModel samlModel = keyCloak.getModel(xmlFile, "http://example.com:1234/bonita")

        when:
        def xmlContent = keyCloak.generateMetadata(samlModel)

        then:
        xmlContent != null

        where:
        xmlFile << [ "keycloak-example.xml"]
    }
}
