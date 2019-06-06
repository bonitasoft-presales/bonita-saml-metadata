package keycloak.saml.metadata

import com.onelogin.saml2.settings.Metadata
import com.onelogin.saml2.settings.SettingsBuilder

import static com.onelogin.saml2.util.Constants.*

class KeyCloak {

    def generateMetadata(SamlModel samlModel) {

        SettingsBuilder settingsBuilder = new SettingsBuilder()
        def settings = settingsBuilder.fromFile("saml2.properties")
                .build()

        settings.spEntityId = samlModel.entityId
        settings.spAssertionConsumerServiceUrl = samlModel.assertionEndPoint
        settings.spSingleLogoutServiceUrl = samlModel.logoutEndpoint
        settings.spNameIDFormat = samlModel.nameIdPolicyFormat

        settings.idpSingleSignOnServiceUrl = samlModel.idpSingleSignOnServiceBindingUrl
        settings.getSPMetadata()

        Metadata metadata = new Metadata(settings)
        metadata.println()

        String certAlias = "bonita"
        String secret = "secret"
        def keyStore = CertificateGenerator.generateCertificate("localhost", 365, secret, certAlias)

        keyStore.getKey(certAlias, secret.toCharArray())
        def certificate = keyStore.getCertificate(certAlias)
        metadata.signMetadata(metadata.metadataString, keyStore.getKey(certAlias, secret.toCharArray()), certificate, RSA_SHA256, SHA256)

    }


    def getModel(String xmlFileName, String endPoint) {
        def xmlContent = new File(this.class.getResource("/$xmlFileName").file).text
        new SamlModel(xmlContent, endPoint)
    }


}
