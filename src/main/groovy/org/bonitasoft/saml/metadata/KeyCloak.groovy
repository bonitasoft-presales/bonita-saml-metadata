/**
 * Copyright (C) 2019 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.saml.metadata

import com.onelogin.saml2.settings.Metadata
import com.onelogin.saml2.settings.SettingsBuilder

import java.security.KeyStore

import static com.onelogin.saml2.util.Constants.*

class KeyCloak {

    def generateMetadata(SamlModel samlModel, Properties samlProperties) {
        def destFile = samlProperties.get("org.bonitasoft.metadata.dest_file")
        def certAlias = samlProperties.get("org.bonitasoft.keystore.cert_alias")
        def certPassword = samlProperties.get("org.bonitasoft.keystore.cert_password")
        def generateKeyStore = Boolean.parseBoolean(samlProperties.get("org.bonitasoft.keystore.generate"))
        def hostname = samlProperties.get("org.bonitasoft.hostname")

        Node rootNode = samlModel.rootNode

        //SP certificate
        setSamlProperty(samlProperties, "onelogin.saml2.sp.x509cert", rootNode.SP.Keys.Key.CertificatePem.text())
        setSamlProperty(samlProperties, "onelogin.saml2.sp.privatekey", rootNode.SP.Keys.Key.PrivateKeyPem.text())


        SettingsBuilder settingsBuilder = new SettingsBuilder()


        settingsBuilder
        def settings = settingsBuilder.fromProperties(samlProperties).build()

        settings.spEntityId = rootNode.SP.@entityID[0]
        settings.spAssertionConsumerServiceUrl = samlModel.assertionEndPoint
        //settings.spSingleLogoutServiceUrl = samlModel.logoutEndpoint
        settings.spNameIDFormat = rootNode.SP.@nameIDPolicyFormat[0]



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

    def setSamlProperty(Properties samlProperties, String propertyName, String keycloakProperty) {
        if (keycloakProperty != null) {
            samlProperties.put(propertyName, keycloakProperty)
        }
    }


    def getModel(String xmlFileName, String endPoint) {
        def xmlContent = new File(this.class.getResource("/$xmlFileName").file).text
        new SamlModel(xmlContent, endPoint)
    }


}
