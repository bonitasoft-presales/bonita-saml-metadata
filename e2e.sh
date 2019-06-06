#!/usr/bin/env bash
./gradlew  check  distZip
cd build/distributions
unzip -q -o keycloak-saml-metadata.zip
cd keycloak-saml-metadata
pwd
ls -l

# check help
bin/keycloak-saml-metadata --help

# check help
bin/keycloak-saml-metadata --properties ./application.properties

ls -l sp-metadata.xml
cat sp-metadata.xml