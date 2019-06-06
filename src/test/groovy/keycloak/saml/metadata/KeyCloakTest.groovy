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
        def samlModel = keyCloak.getModel(xmlFile, "http://example.com:1234/bonita")
        Properties properties=new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))


        when:
        def xmlContent = keyCloak.generateMetadata(samlModel,properties)

        then:
        xmlContent != null

        where:
        xmlFile << ["keycloak-example.xml"]
    }
}
