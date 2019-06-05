#!/usr/bin/env bash

curl -o ../src/main/schemas/xjc/saml-schema-metadata-2.0.xsd http://docs.oasis-open.org/security/saml/v2.0/saml-schema-metadata-2.0.xsd
curl -o ../src/main/schemas/xjc/xmldsig-core-schema.xsd http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd
curl -o ../src/main/schemas/xjc/xenc-schema.xsd http://www.w3.org/TR/2002/REC-xmlenc-core-20021210/xenc-schema.xsd
curl -o ../src/main/schemas/xjc/saml-schema-assertion-2.0.xsd https://docs.oasis-open.org/security/saml/v2.0/saml-schema-assertion-2.0.xsd
rm -rf ./src/main/groovy/org/bonitasoft/saml/xsd


# https://stackoverflow.com/questions/23011547/webservice-client-generation-error-with-jdk8
# https://docs.oracle.com/javase/7/docs/api/javax/xml/XMLConstants.html#ACCESS_EXTERNAL_SCHEMA

#touch jaxp.properties
#echo "javax.xml.accessExternalSchema=all" > jaxp.properties
#cat jaxp.properties
#cp jaxp.properties /Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home/lib

#xjc -d ./src/main/groovy/ -p org.bonitasoft.saml.metadata.dsig ./src/main/resources/xsd/metadata/xmldsig-core-schema.xsd
#xjc -d ./src/main/groovy/ -p org.bonitasoft.saml.metadata.xenc ./src/main/resources/xsd/metadata/xenc-schema.xsd
xjc -verbose -d ../src/main/groovy/ -catalog catalog.xml -b binding.xml -p org.bonitasoft.saml.xsd saml-schema-metadata-2.0.xsd