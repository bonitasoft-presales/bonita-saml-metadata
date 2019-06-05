package keycloak.saml.metadata

import org.bonitasoft.saml.metadata.ObjectFactory
import org.keycloak.saml.SPMetadataDescriptor

class KeyCloak {

    def fromModel(SamlModel samlModel) {
        getSPDescriptor(samlModel.binding, samlModel.assertionEndPoint, samlModel.logoutEndpoint,
                samlModel.wantAuthnRequestSigned, samlModel.entityId, samlModel.nameIdPolicyFormat, samlModel.signingCerts)

        ObjectFactory

    }


    def getSPDescriptor(String binding, String assertionEndpoint, String logoutEndpoint,
                        boolean wantAuthnRequestsSigned, String entityId, String nameIDPolicyFormat, String signingCerts) {
        SPMetadataDescriptor.getSPDescriptor(binding, assertionEndpoint, logoutEndpoint, wantAuthnRequestsSigned, entityId, nameIDPolicyFormat, signingCerts)
    SPMetadataDescriptor.xmlKeyInfo()
    }

    def getModel(String xmlFileName, String endPoint) {
        def xmlContent=new File(this.class.getResource("/$xmlFileName").file).text
        new SamlModel(xmlContent, endPoint)
//        String xmlFile
//        FindFile.findFile("/keycloak-saml.xml")
    }


}
