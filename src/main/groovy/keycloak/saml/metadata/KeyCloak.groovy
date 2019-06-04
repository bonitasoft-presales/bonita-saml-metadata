package keycloak.saml.metadata


import org.keycloak.saml.SPMetadataDescriptor

class KeyCloak {

    def fromModel(SamlModel samlModel) {
        getSPDescriptor(samlModel.binding, samlModel.assertionEndPoint, samlModel.logoutEndpoint,
                samlModel.wantAuthnRequestSigned, samlModel.entityId, samlModel.nameIdPolicyFormat, samlModel.signingCerts)
    }


    def getSPDescriptor(String binding, String assertionEndpoint, String logoutEndpoint,
                        boolean wantAuthnRequestsSigned, String entityId, String nameIDPolicyFormat, String signingCerts) {
        SPMetadataDescriptor.getSPDescriptor(binding, assertionEndpoint, logoutEndpoint, wantAuthnRequestsSigned, entityId, nameIDPolicyFormat, signingCerts)
    }

    def getModel(String xmlFile, String endPoint) {
        new File(this.class.getResource("/$xmlFile").file).text
        new SamlModel(xmlFile, endPoint)
//        String xmlFile
//        FindFile.findFile("/keycloak-saml.xml")
    }


}
