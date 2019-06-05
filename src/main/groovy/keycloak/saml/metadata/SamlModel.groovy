package keycloak.saml.metadata

import lombok.Data

@Data
class SamlModel {
    String binding
    URL assertionEndPoint
    URL logoutEndpoint

    boolean wantAuthnRequestSigned

    String entityId
    String nameIdPolicyFormat
    String signingCerts

    //idp
    URL idpSingleSignOnServiceBindingUrl
//    URL idpSingleSignOnServiceRequestBinding

    SamlModel(String xmlContent, String endPoint) {


        def rootNode = new XmlSlurper().parseText(xmlContent)

        def sp = rootNode.SP
        this.entityId = sp.@entityID
        this.assertionEndPoint = new URL("${endPoint}/saml")
        this.logoutEndpoint = new URL("${endPoint}/samlLogout")

//        this.idpSingleSignOnServiceBindingUrl=sp.IDP.SingleSignOnService.@bindingUrl
//        this.idpSingleSignOnServiceRequestBinding=sp.IDP.SingleSignOnService.@requestBinding
        this.idpSingleSignOnServiceBindingUrl = new URL(sp.IDP.SingleSignOnService.@bindingUrl as String)


        this.nameIdPolicyFormat = sp.PrincipalNameMapping.@attribute

    }
}
