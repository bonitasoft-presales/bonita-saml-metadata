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
import org.slf4j.Logger

import java.security.KeyStore
import java.time.Instant

import static com.onelogin.saml2.util.Constants.*

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
        def destFile = getRequiredSamlProperty("org.bonitasoft.metadata.dest_file")
        def generateKeyStore = Boolean.parseBoolean(getRequiredSamlProperty("org.bonitasoft.keystore.generate"))
        def certAlias
        def certPassword
        def hostname
        if (generateKeyStore) {
            certAlias = getRequiredSamlProperty("org.bonitasoft.keystore.cert_alias")
            certPassword = getRequiredSamlProperty("org.bonitasoft.keystore.cert_password", true)
            hostname = getRequiredSamlProperty("org.bonitasoft.hostname")
        }

        def validUntil = Optional.ofNullable(getSamlProperty("org.bonitasoft.validUntil", false, false))
        def cacheDuration = Optional.ofNullable(getSamlProperty("org.bonitasoft.cacheDuration", false, false))

        Node rootNode = samlModel.rootNode

        //SP certificate
        setSamlProperty("onelogin.saml2.sp.x509cert", rootNode.SP.Keys.Key.CertificatePem.text())
        setSamlProperty("onelogin.saml2.sp.privatekey", rootNode.SP.Keys.Key.PrivateKeyPem.text())

        setSamlProperty("onelogin.saml2.unique_id_prefix", "_")


        SettingsBuilder settingsBuilder = new SettingsBuilder()


        settingsBuilder
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

        Metadata metadata = new Metadata(settings, validUntilCalendar, cacheDurationInt)
        def spMetadata = metadata.getMetadataString()

        if (generateKeyStore) {
            KeyStore keyStore = CertificateGenerator.generateCertificate(hostname, 365, certPassword, certAlias)
            def certificate = keyStore.getCertificate(certAlias)
            spMetadata = metadata.signMetadata(metadata.metadataString, keyStore.getKey(certAlias, certPassword.toCharArray()), certificate, RSA_SHA256, SHA256)
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


    //    def getModel(String xmlFileName, String endPoint) {
    //        def xmlContent = new File(this.class.getResource("/$xmlFileName").file).text
    //        new SamlModel(xmlContent, endPoint)
    //    }


}
