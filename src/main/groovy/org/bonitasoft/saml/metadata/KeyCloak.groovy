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
import com.onelogin.saml2.util.Util
import org.slf4j.Logger

import java.security.KeyStore
import java.time.Instant

import static com.onelogin.saml2.util.Constants.*
import static org.bonitasoft.saml.metadata.CertificateGenerator.*
import static org.bonitasoft.saml.metadata.CertificateGenerator.convertToPrivateKey

class KeyCloak {

    private SamlModel samlModel
    private Properties samlProperties
    private Logger logger

    KeyCloak(SamlModel samlModel, Properties samlProperties, Logger logger) {

        this.logger = logger
        this.samlProperties = samlProperties
        this.samlModel = samlModel
    }

    def generateMetadata() {
        Node rootNode = samlModel.rootNode


        def destFile = getRequiredSamlProperty("org.bonitasoft.metadata.dest_file")
        def generateKeyStore = Boolean.parseBoolean(getRequiredSamlProperty("org.bonitasoft.keystore.generate", false, false))
        def keyDescriptorSigning = Boolean.parseBoolean(rootNode.SP.Keys.Key.@signing[0] as String)
        def keyDescriptorEncryption = Boolean.parseBoolean(rootNode.SP.Keys.Key.@encryption[0] as String)
        def certificatePem = rootNode.SP.Keys.Key.CertificatePem.text()
        def privateKeyPem = rootNode.SP.Keys.Key.PrivateKeyPem.text()

        // metada has one field and signs if cert is provided
        // a node is added if "encryption" is true
        if (keyDescriptorSigning) {
            setSamlProperty("onelogin.saml2.security.want_assertions_encrypted", "false")
        }
        if (keyDescriptorEncryption) {
            setSamlProperty("onelogin.saml2.security.want_assertions_encrypted", "true")
        }
        def certPassword
        def hostname
        def certAlias
        def signWithKeyCloakCert = Boolean.parseBoolean(getRequiredSamlProperty("org.bonitasoft.metadata.sign-with-keycloak-cert"))
        if (generateKeyStore) {
            certAlias = getRequiredSamlProperty("org.bonitasoft.keystore.cert_alias")
            certPassword = getRequiredSamlProperty("org.bonitasoft.keystore.cert_password", true)
            hostname = getRequiredSamlProperty("org.bonitasoft.hostname")
        }

        def validUntil = Optional.ofNullable(getSamlProperty("org.bonitasoft.validUntil", false, false))
        def cacheDuration = Optional.ofNullable(getSamlProperty("org.bonitasoft.cacheDuration", false, false))

        //SP certificate
        setSamlProperty("onelogin.saml2.sp.x509cert", certificatePem)
        setSamlProperty("onelogin.saml2.sp.privatekey", privateKeyPem)

        setSamlProperty("onelogin.saml2.unique_id_prefix", "_")


        SettingsBuilder settingsBuilder = new SettingsBuilder()


        def settings = settingsBuilder.fromProperties(samlProperties).build()

        settings.spEntityId = rootNode.SP.@entityID[0]
        settings.spAssertionConsumerServiceUrl = samlModel.assertionEndPoint
        settings.spNameIDFormat = rootNode.SP.@nameIDPolicyFormat[0]

        Calendar validUntilCalendar
        Integer cacheDurationInt

        if (validUntil.isPresent() && validUntil.get().toString().trim().length() > 0) {
            def now = Instant.parse(validUntil.get())
            validUntilCalendar = now.toCalendar()
        }
        if (cacheDuration.isPresent()) {
            cacheDurationInt = cacheDuration.get()
        }


        def errors = settings.checkSPSettings()
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("error founds:" + errors)
        }
        Metadata metadata = new Metadata(settings, validUntilCalendar, cacheDurationInt)
        def spMetadata = metadata.getMetadataString()

        if (generateKeyStore) {
            KeyStore keyStore = generateCertificate(hostname, 365, certPassword, certAlias)
            def certificate = keyStore.getCertificate(certAlias)
            spMetadata = metadata.signMetadata(metadata.metadataString, keyStore.getKey(certAlias, certPassword.toCharArray()), certificate, RSA_SHA256, SHA256)
        }
        if (signWithKeyCloakCert) {
            spMetadata = metadata.signMetadata(
                    metadata.metadataString,
                    Util.loadPrivateKey(privateKeyPem),
                    Util.loadCert(certificatePem), RSA_SHA256, SHA256)
        }
        def file = new File(destFile as String)
        if (!file.exists()) {
            file.createNewFile()
        }

        logger.info("generating file ${file.getAbsolutePath()}")
        file.text = spMetadata
    }

    def getRequiredSamlProperty(String propertyName, boolean required = true, boolean quiet = false) {
        getSamlProperty(propertyName, required, quiet)
    }

    def getSamlProperty(String propertyName, boolean required, boolean quiet) {
        if (required && !samlProperties.containsKey(propertyName)) {
            throw new IllegalArgumentException("Property $propertyName is not set. Set a valid value and retry.")
        }
        def value = samlProperties.get(propertyName)
        def display = value
        if (quiet) {
            display = "*******"
        }
        logger.info("using property [$propertyName=$display]")
        value
    }

    def setSamlProperty(String propertyName, String keycloakProperty) {
        if (keycloakProperty != null) {
            samlProperties.put(propertyName, keycloakProperty)
        }
    }
}
