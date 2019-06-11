# Bonita SAML2 metadata

this projects generates SAML2 xml metada file based on keycloak xml configuration file used by Bonita platform.

## Usage

1. unzip distribution
1. edit `application.properties` according to your needs
1. run `bin/bonita-saml-metadata` (or `bonita-saml-metadata.bat` on windows)
1. use generated file to configure your SAML2 IDP

## Build project

project uses a gradle wrapper

run `./gradew clean build` (or `./gradlew.bat` on windows)