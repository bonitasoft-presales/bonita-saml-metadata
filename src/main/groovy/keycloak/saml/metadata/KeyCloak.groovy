package keycloak.saml.metadata

import org.keycloak.common.util.FindFile
import org.keycloak.saml.SPMetadataDescriptor

class KeyCloak {
    SPMetadataDescriptor spMetadataDescriptor = new SPMetadataDescriptor()

    def getSPDescriptor(String binding, String assertionEndpoint, String logoutEndpoint, boolean wantAuthnRequestsSigned, String entityId, String nameIDPolicyFormat, String signingCerts) {
        SPMetadataDescriptor.getSPDescriptor(binding, assertionEndpoint, logoutEndpoint, wantAuthnRequestsSigned, entityId, nameIDPolicyFormat, signingCerts)
    }

    def getFile(){
        FindFile.findFile("/keycloak-saml.xml")
    }


}
