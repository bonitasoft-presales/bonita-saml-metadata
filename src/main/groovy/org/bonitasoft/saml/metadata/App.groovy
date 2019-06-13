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

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class App {


    public static final String PROPERTIES = "properties"
    public static final String HELP = "help"


    String execute(Properties properties) {
Logger logger= LoggerFactory.getLogger(this.class)
        def endpoint = properties.get("org.bonitasoft.endpoint")
        def xmlKeyCloak = new File(properties.get("org.bonitasoft.keycloak")).text

        def samlModel = new SamlModel(xmlKeyCloak, endpoint)
        def keyCloak = new KeyCloak(samlModel,properties,logger)
        keyCloak.generateMetadata()
    }

    static void main(String[] args) {
        CommandLineParser parser = new DefaultParser()

        def options = createOptions()
        CommandLine cmd
        try {
            // parse the command line arguments
            cmd = parser.parse(options, args, true)
            def propertyFile = "./application.properties"
            start(cmd, options, propertyFile)
        } catch (ParseException exp) {
            System.err.println("ERROR: error while parsing arguments " + exp.getMessage())
            HelpFormatter formatter = new HelpFormatter()
            formatter.printHelp("bonita-saml-metadata[.bat]", options)
            System.exit(1)
        }
    }

    private static void start(CommandLine cmd, Options options, String propertyFile) {
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter()
            formatter.printHelp("bonita-saml-metadata[.bat]", options)
            System.exit(0)
        }

        if (cmd.hasOption("p")) {
            propertyFile = cmd.getOptionValue(PROPERTIES)
        }

        def file = new File(propertyFile)
        if (!file.exists()) {
            throw new IllegalArgumentException("file does not exists ${file.getAbsolutePath()}")
        } else {
            println "using property file ${file.getAbsolutePath()}"
        }
        Properties properties = new Properties()
        file.withInputStream { properties.load(it) }

        properties. load(new FileInputStream(file.getAbsolutePath()))
        new App().execute(properties)
    }

    static def createOptions() {
        Options options = new Options()
        options.addOption("p", PROPERTIES, true, "Provide a configuration file")
        options.addOption("h", HELP, false, "Display help")
        options
    }
}
