#!/usr/bin/env bash
./gradlew  check  distZip
unzip -q -o build/distributions/keycloak-saml-metadata.zip -d build/distributions

cp e2e/keycloack-saml.xml build/distributions/keycloak-saml-metadata/keycloak-example.xml
cp e2e/application-signed.properties build/distributions/keycloak-saml-metadata/application-signed.properties
cp e2e/application-not-signed.properties build/distributions/keycloak-saml-metadata/application-not-signed.properties
cd build/distributions/keycloak-saml-metadata

pwd
ls -l

# check help
bin/keycloak-saml-metadata --help

# check help
bin/keycloak-saml-metadata --properties ./application-signed.properties
bin/keycloak-saml-metadata --properties ./application-not-signed.properties

ls -l sp-metadata.xml
cat sp-metadata.xml