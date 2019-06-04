package keycloak.saml.metadata

import spock.lang.Ignore
import spock.lang.Specification

class KeyCloakTest extends Specification {

    KeyCloak keyCloak

    def setup() {
        keyCloak = new KeyCloak()
    }


    @Ignore
    def "GetSPDescriptor"() {
    }

    def "GetFile"() {
        given:
        SamlModel samlModel = keyCloak.getModel("keycloak-saml.xml", endPoint)

        when:
        def xmlDescriptor = keyCloak.fromModel(samlModel)

        then:
        xmlDescriptor == "*"

    }
}
