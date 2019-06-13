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

import java.nio.file.Files

class AppTest extends Specification {


    def "should generate metadata"() {
        setup:
        def app = new App()
        Properties properties = new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))

        def xml = this.class.getResourceAsStream("/keycloak-example.xml").text
        def file = Files.createTempFile("keycloak", ".xml").toFile()
        def destFile = Files.createTempFile("metadata", ".xml").toFile()
        file.text = xml
        properties.setProperty("org.bonitasoft.keycloak", file.getAbsolutePath())
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())

        when:
        def result = app.execute(properties)

        then:
        result != null
    }

    def "should get parameters"() {
        setup:
        def app = new App()
        Properties properties = new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))


        def xml = this.class.getResourceAsStream("/keycloak-example.xml").text
        def file = Files.createTempFile("keycloak", ".xml").toFile()
        file.text = xml
        properties.setProperty("org.bonitasoft.keycloak", file.getAbsolutePath())
        def destFile = Files.createTempFile("metadata", ".xml").toFile()
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())

        File props=Files.createTempFile("props",".properties").toFile()
        def stream = new FileOutputStream(props)
        properties.store(stream,"")

        String[] args = ["-p", props.getAbsolutePath()]

        when:
        App.main(args)

        then:
        true
    }
}
