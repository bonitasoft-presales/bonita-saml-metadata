package keycloak.saml.metadata

import spock.lang.Ignore
import spock.lang.Specification

class KeyCloakTest extends Specification {

    KeyCloak keyCloak

    def setup(){
        keyCloak=new KeyCloak()
    }


    @Ignore
    def "GetSPDescriptor"() {
    }

    def "GetFile"() {
        when:
        def file = keyCloak.getFile()

        then:
        file == null

    }
}
