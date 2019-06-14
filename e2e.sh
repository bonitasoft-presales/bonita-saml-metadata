#!/usr/bin/env bash
./gradlew clean check distZip
unzip -q -o build/distributions/bonita-saml-metadata.zip -d build/distributions

cp e2e/keycloack-saml.xml build/distributions/bonita-saml-metadata/keycloak-example.xml
cp e2e/application-signed.properties build/distributions/bonita-saml-metadata/application-signed.properties
cp e2e/application-not-signed.properties build/distributions/bonita-saml-metadata/application-not-signed.properties
cd build/distributions/bonita-saml-metadata

pwd
ls -l

# check help
bin/bonita-saml-metadata --help

# check generate
bin/bonita-saml-metadata --properties ./application-signed.properties

# check signed generate
bin/bonita-saml-metadata --properties ./application-not-signed.properties

ls -l sp-metadata.xml
cat sp-metadata.xml