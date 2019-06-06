package keycloak.saml.metadata

import com.onelogin.saml2.settings.Metadata
import com.onelogin.saml2.settings.SettingsBuilder
import lombok.Builder
import lombok.Data
import lombok.NonNull

import java.security.KeyStore

import static com.onelogin.saml2.util.Constants.*

class KeyCloak {

    def generateMetadata(SamlModel samlModel, Properties samlProperties) {
        def destFile = samlProperties.get("org.bonitasoft.metadata.dest_file")
        def certAlias = samlProperties.get("org.bonitasoft.keystore.cert_alias")
        def certPassword = samlProperties.get("org.bonitasoft.keystore.cert_password")
        def generateKeyStore = samlProperties.get("org.bonitasoft.keystore.generate")
        def hostname = samlProperties.get("org.bonitasoft.hostname")

        SettingsBuilder settingsBuilder = new SettingsBuilder()
        def settings = settingsBuilder.fromProperties(samlProperties).build()

        settings.spEntityId = samlModel.entityId
        settings.spAssertionConsumerServiceUrl = samlModel.assertionEndPoint
        settings.spSingleLogoutServiceUrl = samlModel.logoutEndpoint
        settings.spNameIDFormat = samlModel.nameIdPolicyFormat

        settings.idpSingleSignOnServiceUrl = samlModel.idpSingleSignOnServiceBindingUrl

        def result = settings.getSPMetadata()

        if (generateKeyStore) {
            KeyStore keyStore
            Metadata metadata = new Metadata(settings)
            keyStore = CertificateGenerator.generateCertificate(hostname, 365, certPassword, certAlias)
            keyStore.getKey(certAlias, certPassword.toCharArray())
            def certificate = keyStore.getCertificate(certAlias)
            result = metadata.signMetadata(metadata.metadataString, keyStore.getKey(certAlias, certPassword.toCharArray()), certificate, RSA_SHA256, SHA256)
        }
        def file = new File(destFile as String)
        if (!file.exists()) {
            file.createNewFile()
        }
        println("generating file ${file.getAbsolutePath()}")
        file.text = result
    }


    def getModel(String xmlFileName, String endPoint) {
        def xmlContent = new File(this.class.getResource("/$xmlFileName").file).text
        new SamlModel(xmlContent, endPoint)
    }


}
