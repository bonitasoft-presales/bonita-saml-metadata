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

import spock.lang.Specification
import spock.lang.Unroll

class KeyCloakTest extends Specification {

    KeyCloak keyCloak

    def setup() {
        keyCloak = new KeyCloak()
    }


    @Unroll
    def "should parse #xmlFile"(xmlFile) {
        given:
        def samlModel = keyCloak.getModel(xmlFile, "http://example.com:1234/bonita")
        Properties properties=new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))


        when:
        def xmlContent = keyCloak.generateMetadata(samlModel,properties)

        then:
        xmlContent != null

        where:
        xmlFile << ["keycloak-example-keys.xml"]
    }
}
