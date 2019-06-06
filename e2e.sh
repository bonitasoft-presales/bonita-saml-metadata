#!/usr/bin/env bash
./gradlew  check  distZip
unzip -q -o build/distributions/keycloak-saml-metadata.zip -d build/distributions

cp e2e/keycloack-saml.xml build/distributions/keycloak-saml-metadata/keycloak-example.xml
cd build/distributions/keycloak-saml-metadata

pwd
ls -l

# check help
bin/keycloak-saml-metadata --help

# check help
bin/keycloak-saml-metadata --properties ./application.properties

ls -l sp-metadata.xml
cat sp-metadata.xml