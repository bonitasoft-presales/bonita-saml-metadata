package keycloak.saml.metadata

import lombok.Data

@Data
class SamlModel {
    String binding
    String assertionEndPoint
    String logoutEndpoint
    boolean wantAuthnRequestSigned
    String entityId
    String nameIdPolicyFormat
    String signingCerts

    SamlModel(String xmlContent,String endPoint ) {


        def rootNode = new XmlSlurper().parseText(xmlContent)

        def sp = rootNode.SP
        this.entityId = sp.@entityID
        this.assertionEndPoint = "${endPoint}/saml"
        this.logoutEndpoint = "${endPoint}/samlLogout"

        //TODO is this OK ?
        this.wantAuthnRequestSigned = sp.forceAuthentication

        this.nameIdPolicyFormat = sp.PrincipalNameMapping.@attribute

    }
}
