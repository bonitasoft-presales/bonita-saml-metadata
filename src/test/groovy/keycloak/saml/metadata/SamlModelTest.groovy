package keycloak.saml.metadata

import spock.lang.Specification

class SamlModelTest extends Specification {
    def "should parse XML"() {
        given:
        def keycloakSaml = new File(this.class.getResource("/keycloak-saml.xml").file)
        SamlModel samlModel

        when:
        samlModel = new SamlModel(keycloakSaml.text, "http://my-bonita-server:8080/bonita")

        then:
        samlModel.entityId == "bonita"
        samlModel.nameIdPolicyFormat == "username"
        samlModel.wantAuthnRequestSigned == true
        samlModel.logoutEndpoint == "http://my-bonita-server:8080/bonita/samlLogout"
        samlModel.binding == null
        samlModel.signingCerts == null //FIXME

    }
}
