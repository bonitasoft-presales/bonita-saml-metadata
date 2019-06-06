#!/usr/bin/env bash

curl -o src/main/resources/xsd/saml-schema-metadata-2.0.xsd http://docs.oasis-open.org/security/saml/v2.0/saml-schema-metadata-2.0.xsd
curl -o src/main/resources/xsd/xmldsig-core-schema.xsd http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd
curl -o src/main/resources/xsd/xenc-schema.xsd http://www.w3.org/TR/2002/REC-xmlenc-core-20021210/xenc-schema.xsd
curl -o src/main/resources/xsd/saml-schema-assertion-2.0.xsd https://docs.oasis-open.org/security/saml/v2.0/saml-schema-assertion-2.0.xsd

