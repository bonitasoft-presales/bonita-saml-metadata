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

import org.slf4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files

class KeyCloakTest extends Specification {

    KeyCloak keyCloak

    Logger logger= org.slf4j.LoggerFactory.getLogger(this.class)


    @Unroll
    def "should parse #xmlFile"(xmlFile) {
        def xmlContent1 = new File(this.class.getResource("/${xmlFile}").file).text
        given:
        def samlModel = new SamlModel(xmlContent1, "http://example.com:1234/bonita")
        Properties properties = new Properties()
        def tempFile = Files.createTempFile("metadata", "xml").toFile()
        properties.setProperty("org.bonitasoft.metadata.dest_file", tempFile.getAbsolutePath())

        properties.load(this.class.getResourceAsStream("/application.properties"))
        keyCloak = new KeyCloak(samlModel,properties,logger)

        when:
        def xmlContent = keyCloak.generateMetadata()

        then:
        xmlContent != null

        where:
        xmlFile << ["keycloak-example-keys.xml"]
    }

    def "should handle validUntil attribute"() {
        given:
        def xmlFile = "./keycloak-example-keys.xml"
        def xmlContent1 = new File(this.class.getResource("/${xmlFile}").file).text
        def samlModel = new SamlModel(xmlContent1, "http://example.com:1234/bonita")
        Properties properties = new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))
        def tempFile = Files.createTempFile("metadata", "xml").toFile()
        properties.setProperty("org.bonitasoft.metadata.dest_file", tempFile.getAbsolutePath())
        properties.setProperty("org.bonitasoft.validUntil", "2050-12-31T15:20:09Z")
        keyCloak = new KeyCloak(samlModel,properties,logger)

        when:
        def xmlContent = keyCloak.generateMetadata()

        then: 'valid until is in generrated file'
        def parsed = new XmlParser().parseText(xmlContent as String)
        parsed.@validUntil == "2050-12-31T15:20:09Z"
    }
}
