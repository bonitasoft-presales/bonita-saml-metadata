#!/usr/bin/env bash

./gradlew clean check distZip

#get current version
./gradlew -q currentVersion > build/version.txt
jarVersion=`head -1 build/version.txt`

unzip -q -o "build/distributions/bonita-saml-metadata-${jarVersion}".zip -d build/distributions

cp e2e/keycloack-saml.xml build/distributions/bonita-saml-metadata-${jarVersion}/keycloak-example.xml
cp e2e/application-signed.properties build/distributions/bonita-saml-metadata-${jarVersion}/application-signed.properties
cp e2e/application-not-signed.properties build/distributions/bonita-saml-metadata-${jarVersion}/application-not-signed.properties
cd build/distributions/bonita-saml-metadata-${jarVersion}

pwd
ls -l

# check help
bin/bonita-saml-metadata --help

# check default properties has all required properties
bin/bonita-saml-metadata --properties ./application.properties

# check generate
bin/bonita-saml-metadata --properties ./application-signed.properties

# check signed generate
bin/bonita-saml-metadata --properties ./application-not-signed.properties

ls -l sp-metadata.xml
cat sp-metadata.xml