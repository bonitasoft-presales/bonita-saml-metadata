/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package keycloak.saml.metadata

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException

class App {


    public static final String PROPERTIES = "properties"
    public static final String HELP = "help"

    String execute(Properties properties) {
        def keyCloak = new KeyCloak()

        def endpoint = properties.get("org.bonitasoft.endpoint")
        def xmlKeyCloak = new File(properties.get("org.bonitasoft.keycloak")).text

        def samlModel = new SamlModel(xmlKeyCloak, endpoint)
        keyCloak.generateMetadata(samlModel, properties)
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
            formatter.printHelp("keycloak-saml-metadata[.bat]", options)
            System.exit(1)
        }
    }

    private static void start(CommandLine cmd, Options options, String propertyFile) {
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter()
            formatter.printHelp("keycloak-saml-metadata[.bat]", options)
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
