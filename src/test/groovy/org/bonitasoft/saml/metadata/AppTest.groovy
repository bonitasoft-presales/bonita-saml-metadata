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

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.NodeDetail
import org.custommonkey.xmlunit.XMLUnit
import org.slf4j.Logger
import spock.lang.Specification

import java.nio.file.Files

class AppTest extends Specification implements SamlTestHelper {

    Logger logger= org.slf4j.LoggerFactory.getLogger(this.class)

    def "should generate metadata"() {
        setup:
        def app = new App()
        Properties properties = new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))

        def xml = this.class.getResourceAsStream("/keycloak-example-keys.xml").text
        def file = Files.createTempFile("keycloak", ".xml").toFile()
        def destFile = Files.createTempFile("metadata", ".xml").toFile()
        file.text = xml
        properties.setProperty("org.bonitasoft.keycloak", file.getAbsolutePath())
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())

        when:
        def result = app.execute(properties)

        then:
        result != null
    }

    def "should get expected XML with signing"() {
        setup:
        def app = new App()
        Properties properties = new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))
        def xml = '''<keycloak-saml-adapter>
    <SP entityID="http://entity-id.com:8081/path/"
        sslPolicy="ALL"
        nameIDPolicyFormat="urn:oasis:names:tc:SAML:2.0:nameid-format:transient"
        forceAuthentication="false"
        isPassive="false"
        turnOffChangeSessionIdOnLogin="true">
        <Keys>
            <Key signing="true" encryption="false">
                <PrivateKeyPem>
                    -----BEGIN PRIVATE KEY-----
                    MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIgdCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMBAAECgYEAyhH6EBASLkZ7TpkXK6spbshNpl+448pjYWIVpCqVqt2fW3TdvcNCFrXBDIj3rqHAX6TZSFw0E+BebUH3BTtww+oTUH6gLaQnYZ6roFjvBn59p6Sd9AGBUEMHV8oLewBUM5Qla/FlJnNBNU69VEcKhXugxb0PBDXij25fcQ1UqwECQQD66jmIyEPEUtILODwFnjQ8LrL37AHYi/+COOLidrHSyFtWTlne/9xPD75pbKEtiDOnUnvARhxWF8G38Igza7PBAkEA4tXoeN82DwAfHbr1MiO9Li1IMeUDUQBKSPvMu8rJLjzDRLPlD2MPBoEr3W145JzBa7uj00PejzQq24+VofF+ZQJAAd5Mn2AeYQ/c0IiSqdgLu4b9fisbuGkSdf3Gcrk/ibpEM9hRgv+UvGH5oP9WE+i3ub87fKsI+vsiTiRUX02mAQJBAIfFl3c5y4ahAP7vl7HiOGr6SZsrw5dpQA19Qecpks9tKUfnEXTrSuQOzu2jh9f2h8NvNbjPh9hZVknDIMIk5Q0CQFwKMW5r5MKtop6f/oDcqHixd0ukS2uEz+Q8hIL0zDX43urQvSrWC3bMSME5BFo5i7LYM0Kj1yn+Cq/gEll+koU=
                    -----END PRIVATE KEY-----
                </PrivateKeyPem>
                <CertificatePem>
                    -----BEGIN CERTIFICATE-----
                    MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQELBQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIgdCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMBAAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73auff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEAbYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vAOm10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                    -----END CERTIFICATE-----
                </CertificatePem>
            </Key>
        </Keys>
        <PrincipalNameMapping policy="FROM_ATTRIBUTE" attribute="Login"/>
        <IDP entityID="https://xxxxxxx-xxxxxx.fr/xxxxx"
             signaturesRequired="true"
             signatureAlgorithm="RSA_SHA256">
            <SingleSignOnService signRequest="true"
                                 validateResponseSignature="true"
                                 requestBinding="POST"
                                 responseBinding="POST"
                                 bindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/POST/SSO"/>
            <SingleLogoutService signRequest="true"
                                 signResponse="true"
                                 validateRequestSignature="true"
                                 validateResponseSignature="true"
                                 requestBinding="POST"
                                 responseBinding="POST"
                                 postBindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/POST/SLO"
                                 redirectBindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/Redirect/SLO"/>
            <Keys>
                <Key signing="true">
                    <CertificatePem>
                        -----BEGIN CERTIFICATE-----
                        MIICLTCCAZagAwIBAgIVAIl9v+QFdmyQVFQ/0TZ5xka0vuiGMA0GCSqGSIb3DQEBCwUAMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwHhcNMTkwNjA3MDAwMDAwWhcNMjAwNjA2MDAwMDAwWjAVMRMwEQYDVQQDDApsb2NhbGhvaHN0MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXCrZMIJcmDcPPOHr91JPsXAFhjlGepsacwjc5u+KJUjVlGCnKqQZWoa4G9OYERCnO31QjbaMcbkhBe/taDPmYru7O3XuPkbpOs5X+fzPqgCNK33fsrmsEd+TfM7kgZLKl+VPhhbgTxruLpZYSVmaXlVtNPrw9ENPHvdhQCGKtCwIDAQABo3kwdzAbBgNVHQ4EFL5z84+vwf9BTp2z9GtYfCesb19/MBsGA1UdIwQUvnPzj6/B/0FOnbP0a1h8J6xvX38wDwYDVR0TAQH/BAUwAwEB/zALBgNVHQ8EBAMCAoQwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCSqGSIb3DQEBCwUAA4GBAIHoDLHXwbE5kuiShhe3AgEoY0szn56VThrTbjnF6tySL2OakzgfkcKFaR0yV2AeauUM9ie10Chy1Wz3zsx03V5juFRQ0bcp6hSWwIyyzkE7PfVzqFUkxLrD4qAsU5d5YOhFElrtVz1aGms8tZPpay+6IS8Ijv0gTpKCq0gtZjJK
                        -----END CERTIFICATE-----
                    </CertificatePem>
                </Key>
            </Keys>
        </IDP>
    </SP>
</keycloak-saml-adapter>
'''
        def file = Files.createTempFile("keycloak", ".xml").toFile()
        file.text = xml
        properties.setProperty("org.bonitasoft.keycloak", file.getAbsolutePath())
        def destFile = Files.createTempFile("metadata", ".xml").toFile()
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())
        properties.setProperty("org.bonitasoft.keystore.generate", "false")
        properties.setProperty("org.bonitasoft.metadata.sign-with-keycloak-cert", "false")

        File props = Files.createTempFile("props", ".properties").toFile()
        def stream = new FileOutputStream(props)
        properties.store(stream, "")

        String[] args = ["-p", props.getAbsolutePath()]

        when:
        App.main(args)

        then:
        def expected = '''<?xml version="1.0"?>
<md:EntityDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata" validUntil="2050-12-31T15:20:09Z"
                     cacheDuration="PT2147483647S" entityID="http://entity-id.com:8081/path/" ID="_f0948d2e-05fd-482e-b4a5-b2ad3735ef37">
    <md:SPSSODescriptor AuthnRequestsSigned="false" WantAssertionsSigned="false"
                        protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
        <md:KeyDescriptor use="signing">
            <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                <ds:X509Data>
                    <ds:X509Certificate>MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQEL
                        BQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2
                        MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEB
                        BQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIg
                        dCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T
                        5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMB
                        AAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73au
                        ff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAd
                        BgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEA
                        bYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vA
                        Om10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx
                        9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                    </ds:X509Certificate>
                </ds:X509Data>
            </ds:KeyInfo>
        </md:KeyDescriptor>
        <md:NameIDFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:transient</md:NameIDFormat>
        <md:AssertionConsumerService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                                     Location="http://localhost:8080/bonita/saml" index="1"/>
    </md:SPSSODescriptor>
</md:EntityDescriptor>'''
        //destFile.text == expected

        // def expectedXmlText = new File(this.class.getResource("/to7_4_0/$expectedXml").file).text
        XMLUnit.setIgnoreWhitespace(true)
        def xmlResult = destFile.text
        logger.info """
Result:
*********
$xmlResult
*********
"""
       checkReturnedXml(expected,xmlResult)

//        final List<Diff> allDifferences = new DetailedDiff(XMLUnit.compareXML(expected, xmlResult))
//                .getAllDifferences()
//        if (!allDifferences.isEmpty()) {
//            allDifferences.each { diff ->
//                //ignore @id attribute values generated at migration time
//                def description = diff.getProperties().get("description")
//                switch (description) {
//                    case "attribute value":
//                        def ignoreID = "ID"
//                        assert (diff.getProperties().get("controlNodeDetail") as org.custommonkey.xmlunit.NodeDetail).node.name == ignoreID
//                        assert (diff.getProperties().get("testNodeDetail") as org.custommonkey.xmlunit.NodeDetail).node.name == ignoreID
//                        break
//                    case "text value":
//                    //attribute id is here a text value
//                        def textNodeIds = ["ds:X509Certificate"]
//                        def controlNodeName = (diff.getProperties().get("controlNodeDetail") as org.custommonkey.xmlunit.NodeDetail).node.parentNode
//                        def testNodeName = (diff.getProperties().get("testNodeDetail") as org.custommonkey.xmlunit.NodeDetail).node.parentNode
//                        assert controlNodeName.name == testNodeName.name
//                    // this assertion is designed to display test node content when node name is not in expected textNodeIds
//                        logger.info("found difference in text node "+  testNodeName.name)
//                        assert textNodeIds.contains(testNodeName.name)// || controlNodeName.textContent == testNodeName.textContent
//
//                        assert sanitize(controlNodeName.textContent) == sanitize(testNodeName.textContent)
//                        break
//                    default:
//                        def testNodeName = (diff.getProperties().get("testNodeDetail") as org.custommonkey.xmlunit.NodeDetail).node
//                        logger.info "ERROR: $diff not expected! ***\n${testNodeName.textContent}\n***"
//                        assert diff == null
//                }
//
//            }
//        }

    }

    def "should get expected XML with encryption"() {
        setup:
        def app = new App()
        Properties properties = new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))
        def xml = '''<keycloak-saml-adapter>
    <SP entityID="http://entity-id.com:8081/path/"
        sslPolicy="ALL"
        nameIDPolicyFormat="urn:oasis:names:tc:SAML:2.0:nameid-format:transient"
        forceAuthentication="false"
        isPassive="false"
        turnOffChangeSessionIdOnLogin="true">
        <Keys>
            <Key signing="true" encryption="true">
                <PrivateKeyPem>
                    -----BEGIN PRIVATE KEY-----
                    MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIgdCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMBAAECgYEAyhH6EBASLkZ7TpkXK6spbshNpl+448pjYWIVpCqVqt2fW3TdvcNCFrXBDIj3rqHAX6TZSFw0E+BebUH3BTtww+oTUH6gLaQnYZ6roFjvBn59p6Sd9AGBUEMHV8oLewBUM5Qla/FlJnNBNU69VEcKhXugxb0PBDXij25fcQ1UqwECQQD66jmIyEPEUtILODwFnjQ8LrL37AHYi/+COOLidrHSyFtWTlne/9xPD75pbKEtiDOnUnvARhxWF8G38Igza7PBAkEA4tXoeN82DwAfHbr1MiO9Li1IMeUDUQBKSPvMu8rJLjzDRLPlD2MPBoEr3W145JzBa7uj00PejzQq24+VofF+ZQJAAd5Mn2AeYQ/c0IiSqdgLu4b9fisbuGkSdf3Gcrk/ibpEM9hRgv+UvGH5oP9WE+i3ub87fKsI+vsiTiRUX02mAQJBAIfFl3c5y4ahAP7vl7HiOGr6SZsrw5dpQA19Qecpks9tKUfnEXTrSuQOzu2jh9f2h8NvNbjPh9hZVknDIMIk5Q0CQFwKMW5r5MKtop6f/oDcqHixd0ukS2uEz+Q8hIL0zDX43urQvSrWC3bMSME5BFo5i7LYM0Kj1yn+Cq/gEll+koU=
                    -----END PRIVATE KEY-----
                </PrivateKeyPem>
                <CertificatePem>
                    -----BEGIN CERTIFICATE-----
                    MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQELBQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIgdCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMBAAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73auff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEAbYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vAOm10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                    -----END CERTIFICATE-----
                </CertificatePem>
            </Key>
        </Keys>
        <PrincipalNameMapping policy="FROM_ATTRIBUTE" attribute="Login"/>
        <IDP entityID="https://xxxxxxx-xxxxxx.fr/xxxxx"
             signaturesRequired="true"
             signatureAlgorithm="RSA_SHA256">
            <SingleSignOnService signRequest="true"
                                 validateResponseSignature="true"
                                 requestBinding="POST"
                                 responseBinding="POST"
                                 bindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/POST/SSO"/>
            <SingleLogoutService signRequest="true"
                                 signResponse="true"
                                 validateRequestSignature="true"
                                 validateResponseSignature="true"
                                 requestBinding="POST"
                                 responseBinding="POST"
                                 postBindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/POST/SLO"
                                 redirectBindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/Redirect/SLO"/>
            <Keys>
                <Key signing="true">
                    <CertificatePem>
                        -----BEGIN CERTIFICATE-----
                        MIICLTCCAZagAwIBAgIVAIl9v+QFdmyQVFQ/0TZ5xka0vuiGMA0GCSqGSIb3DQEBCwUAMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwHhcNMTkwNjA3MDAwMDAwWhcNMjAwNjA2MDAwMDAwWjAVMRMwEQYDVQQDDApsb2NhbGhvaHN0MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXCrZMIJcmDcPPOHr91JPsXAFhjlGepsacwjc5u+KJUjVlGCnKqQZWoa4G9OYERCnO31QjbaMcbkhBe/taDPmYru7O3XuPkbpOs5X+fzPqgCNK33fsrmsEd+TfM7kgZLKl+VPhhbgTxruLpZYSVmaXlVtNPrw9ENPHvdhQCGKtCwIDAQABo3kwdzAbBgNVHQ4EFL5z84+vwf9BTp2z9GtYfCesb19/MBsGA1UdIwQUvnPzj6/B/0FOnbP0a1h8J6xvX38wDwYDVR0TAQH/BAUwAwEB/zALBgNVHQ8EBAMCAoQwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCSqGSIb3DQEBCwUAA4GBAIHoDLHXwbE5kuiShhe3AgEoY0szn56VThrTbjnF6tySL2OakzgfkcKFaR0yV2AeauUM9ie10Chy1Wz3zsx03V5juFRQ0bcp6hSWwIyyzkE7PfVzqFUkxLrD4qAsU5d5YOhFElrtVz1aGms8tZPpay+6IS8Ijv0gTpKCq0gtZjJK
                        -----END CERTIFICATE-----
                    </CertificatePem>
                </Key>
            </Keys>
        </IDP>
    </SP>
</keycloak-saml-adapter>
'''
        def file = Files.createTempFile("keycloak", ".xml").toFile()
        file.text = xml
        properties.setProperty("org.bonitasoft.keycloak", file.getAbsolutePath())
        def destFile = Files.createTempFile("metadata", ".xml").toFile()
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())
        properties.setProperty("org.bonitasoft.keystore.generate", "false")
        properties.setProperty("org.bonitasoft.metadata.sign-with-keycloak-cert", "false")

        File props = Files.createTempFile("props", ".properties").toFile()
        def stream = new FileOutputStream(props)
        properties.store(stream, "")

        String[] args = ["-p", props.getAbsolutePath()]

        when:
        App.main(args)

        then:
        def expected = '''<?xml version="1.0"?>
<md:EntityDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata"
                     entityID="http://entity-id.com:8081/path/" ID="_f0948d2e-05fd-482e-b4a5-b2ad3735ef37">
    <md:SPSSODescriptor AuthnRequestsSigned="false" WantAssertionsSigned="false"
                        protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
        <md:KeyDescriptor use="signing">
            <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                <ds:X509Data>
                    <ds:X509Certificate>MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQEL
                        BQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2
                        MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEB
                        BQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIg
                        dCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T
                        5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMB
                        AAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73au
                        ff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAd
                        BgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEA
                        bYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vA
                        Om10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx
                        9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                    </ds:X509Certificate>
                </ds:X509Data>
            </ds:KeyInfo>
        </md:KeyDescriptor>
         <md:KeyDescriptor use="encryption">
            <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                <ds:X509Data>
                    <ds:X509Certificate>MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQEL
                        BQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2
                        MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEB
                        BQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIg
                        dCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T
                        5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMB
                        AAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73au
                        ff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAd
                        BgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEA
                        bYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vA
                        Om10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx
                        9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                    </ds:X509Certificate>
                </ds:X509Data>
            </ds:KeyInfo>
        </md:KeyDescriptor>
        <md:NameIDFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:transient</md:NameIDFormat>
        <md:AssertionConsumerService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                                     Location="http://localhost:8080/bonita/saml" index="1"/>
    </md:SPSSODescriptor>
</md:EntityDescriptor>'''
        //destFile.text == expected

        // def expectedXmlText = new File(this.class.getResource("/to7_4_0/$expectedXml").file).text
        XMLUnit.setIgnoreWhitespace(true)
        def xmlResult = destFile.text
        logger.info """
Result:
*********
$xmlResult
*********
"""
        checkReturnedXml(expected, xmlResult)

    }


    def "should get sign metadata with keycloak key"() {
        setup:
        def app = new App()
        Properties properties = new Properties()
        properties.load(this.class.getResourceAsStream("/application.properties"))
        def xml = '''<keycloak-saml-adapter>
    <SP entityID="http://entity-id.com:8081/path/"
        sslPolicy="ALL"
        nameIDPolicyFormat="urn:oasis:names:tc:SAML:2.0:nameid-format:transient"
        forceAuthentication="false"
        isPassive="false"
        turnOffChangeSessionIdOnLogin="true">
        <Keys>
            <Key signing="true" encryption="true">
                <PrivateKeyPem>
                    -----BEGIN PRIVATE KEY-----
                    MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIgdCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMBAAECgYEAyhH6EBASLkZ7TpkXK6spbshNpl+448pjYWIVpCqVqt2fW3TdvcNCFrXBDIj3rqHAX6TZSFw0E+BebUH3BTtww+oTUH6gLaQnYZ6roFjvBn59p6Sd9AGBUEMHV8oLewBUM5Qla/FlJnNBNU69VEcKhXugxb0PBDXij25fcQ1UqwECQQD66jmIyEPEUtILODwFnjQ8LrL37AHYi/+COOLidrHSyFtWTlne/9xPD75pbKEtiDOnUnvARhxWF8G38Igza7PBAkEA4tXoeN82DwAfHbr1MiO9Li1IMeUDUQBKSPvMu8rJLjzDRLPlD2MPBoEr3W145JzBa7uj00PejzQq24+VofF+ZQJAAd5Mn2AeYQ/c0IiSqdgLu4b9fisbuGkSdf3Gcrk/ibpEM9hRgv+UvGH5oP9WE+i3ub87fKsI+vsiTiRUX02mAQJBAIfFl3c5y4ahAP7vl7HiOGr6SZsrw5dpQA19Qecpks9tKUfnEXTrSuQOzu2jh9f2h8NvNbjPh9hZVknDIMIk5Q0CQFwKMW5r5MKtop6f/oDcqHixd0ukS2uEz+Q8hIL0zDX43urQvSrWC3bMSME5BFo5i7LYM0Kj1yn+Cq/gEll+koU=
                    -----END PRIVATE KEY-----
                </PrivateKeyPem>
                <CertificatePem>
                    -----BEGIN CERTIFICATE-----
                    MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQELBQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIgdCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMBAAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73auff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEAbYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vAOm10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                    -----END CERTIFICATE-----
                </CertificatePem>
            </Key>
        </Keys>
        <PrincipalNameMapping policy="FROM_ATTRIBUTE" attribute="Login"/>
        <IDP entityID="https://xxxxxxx-xxxxxx.fr/xxxxx"
             signaturesRequired="true"
             signatureAlgorithm="RSA_SHA256">
            <SingleSignOnService signRequest="true"
                                 validateResponseSignature="true"
                                 requestBinding="POST"
                                 responseBinding="POST"
                                 bindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/POST/SSO"/>
            <SingleLogoutService signRequest="true"
                                 signResponse="true"
                                 validateRequestSignature="true"
                                 validateResponseSignature="true"
                                 requestBinding="POST"
                                 responseBinding="POST"
                                 postBindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/POST/SLO"
                                 redirectBindingUrl="https://xxxxxxx-xxxxxx.fr/xxx/xx/xx/SAML2/Redirect/SLO"/>
            <Keys>
                <Key signing="true">
                    <CertificatePem>
                        -----BEGIN CERTIFICATE-----
                        MIICLTCCAZagAwIBAgIVAIl9v+QFdmyQVFQ/0TZ5xka0vuiGMA0GCSqGSIb3DQEBCwUAMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwHhcNMTkwNjA3MDAwMDAwWhcNMjAwNjA2MDAwMDAwWjAVMRMwEQYDVQQDDApsb2NhbGhvaHN0MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXCrZMIJcmDcPPOHr91JPsXAFhjlGepsacwjc5u+KJUjVlGCnKqQZWoa4G9OYERCnO31QjbaMcbkhBe/taDPmYru7O3XuPkbpOs5X+fzPqgCNK33fsrmsEd+TfM7kgZLKl+VPhhbgTxruLpZYSVmaXlVtNPrw9ENPHvdhQCGKtCwIDAQABo3kwdzAbBgNVHQ4EFL5z84+vwf9BTp2z9GtYfCesb19/MBsGA1UdIwQUvnPzj6/B/0FOnbP0a1h8J6xvX38wDwYDVR0TAQH/BAUwAwEB/zALBgNVHQ8EBAMCAoQwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCSqGSIb3DQEBCwUAA4GBAIHoDLHXwbE5kuiShhe3AgEoY0szn56VThrTbjnF6tySL2OakzgfkcKFaR0yV2AeauUM9ie10Chy1Wz3zsx03V5juFRQ0bcp6hSWwIyyzkE7PfVzqFUkxLrD4qAsU5d5YOhFElrtVz1aGms8tZPpay+6IS8Ijv0gTpKCq0gtZjJK
                        -----END CERTIFICATE-----
                    </CertificatePem>
                </Key>
            </Keys>
        </IDP>
    </SP>
</keycloak-saml-adapter>
'''
        def file = Files.createTempFile("keycloak", ".xml").toFile()
        file.text = xml
        properties.setProperty("org.bonitasoft.keycloak", file.getAbsolutePath())
        def destFile = Files.createTempFile("metadata", ".xml").toFile()
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())
        properties.setProperty("org.bonitasoft.metadata.dest_file", destFile.getAbsolutePath())
        properties.setProperty("org.bonitasoft.keystore.generate", "false")
        properties.setProperty("org.bonitasoft.metadata.sign-with-keycloak-cert", "true")

        File props = Files.createTempFile("props", ".properties").toFile()
        def stream = new FileOutputStream(props)
        properties.store(stream, "")

        String[] args = ["-p", props.getAbsolutePath()]

        when:
        App.main(args)

        then:
        def expected = '''<?xml version="1.0"?>
<md:EntityDescriptor xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata" validUntil="2050-12-31T15:20:09Z"
                     cacheDuration="PT2147483647S" entityID="http://entity-id.com:8081/path/" ID="_f0948d2e-05fd-482e-b4a5-b2ad3735ef37">
       <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
        <ds:SignedInfo>
            <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:CanonicalizationMethod>
            <ds:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"></ds:SignatureMethod>
            <ds:Reference URI="#_359f173f-4076-4f34-a1e5-1f36b4d77c81">
                <ds:Transforms>
                    <ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"></ds:Transform>
                    <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"></ds:Transform>
                </ds:Transforms>
                <ds:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"></ds:DigestMethod>
                <ds:DigestValue>9uDfUb/d++eH8alpnL+Vg9Utt4fegExVeq6yPhpUaOc=</ds:DigestValue>
            </ds:Reference>
        </ds:SignedInfo>
        <ds:SignatureValue>
            lIEvbjBepd4lE3fL9R0Q5Ry8NhEyqChSFCj70wXjQXafRmOdYQ/e6qoHn5Hjl2qMtVRoKe/soSHox9rVLMcTf3DjGeNuQie7sQHsOIIf7EtH1uIlozr7E90kW4dDVLK8qPEbH8EnEjD3Zxa0NMrTuwBeLNyx30kpj5palfh6veE=
        </ds:SignatureValue>
        <ds:KeyInfo>
            <ds:X509Data>
                <ds:X509Certificate>
                    MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQELBQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIgdCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMBAAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73auff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEAbYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vAOm10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                </ds:X509Certificate>
            </ds:X509Data>
        </ds:KeyInfo>
    </ds:Signature>
    <md:SPSSODescriptor AuthnRequestsSigned="false" WantAssertionsSigned="false"
                        protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol">
        <md:KeyDescriptor use="signing">
            <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                <ds:X509Data>
                    <ds:X509Certificate>MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQEL
                        BQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2
                        MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEB
                        BQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIg
                        dCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T
                        5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMB
                        AAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73au
                        ff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAd
                        BgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEA
                        bYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vA
                        Om10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx
                        9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                    </ds:X509Certificate>
                </ds:X509Data>
            </ds:KeyInfo>
        </md:KeyDescriptor>
         <md:KeyDescriptor use="encryption">
            <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                <ds:X509Data>
                    <ds:X509Certificate>MIICLDCCAZWgAwIBAgIUdgYcPmghyMenrLkPRXjY0InAHDIwDQYJKoZIhvcNAQEL
                        BQAwFTETMBEGA1UEAwwKbG9jYWxob2hzdDAeFw0xOTA2MDcwMDAwMDBaFw0yMDA2
                        MDYwMDAwMDBaMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwgZ8wDQYJKoZIhvcNAQEB
                        BQADgY0AMIGJAoGBAN5Ub4dcUGWI4TGK8YPqIyxUuHlkyP+mFf7LV9gmyp1FhuIg
                        dCfe9SpAUfpcf62XTy0emacIZUHSuCs4eprcen4YHJcSBNfOtmfYOrDjGtu45f5T
                        5Q8uuDuGxizbxqll8Cid27UlewrUmG84n+V+LHR5vFHyNMXD+pW3gdDlp+klAgMB
                        AAGjeTB3MBsGA1UdDgQUMO92rn3+q4Cb6KtFA3JMo6y5gG8wGwYDVR0jBBQw73au
                        ff6rgJvoq0UDckyjrLmAbzAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIChDAd
                        BgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADgYEA
                        bYC0PPLL+/JA0TXoNtyZ6y4CAegSk4yIIAsPD6lArcfydqlLTocaF+zhq0a627vA
                        Om10DjXtDg1hknd2lGr1i/+hQeppYC2hUCixUvLrhnFu5Fl1Z0L3jojyRylQJzDx
                        9cEtZkajFJNo44kKo50y9q3Bi+i8H4ld+ixsviSqRhM=
                    </ds:X509Certificate>
                </ds:X509Data>
            </ds:KeyInfo>
        </md:KeyDescriptor>
        <md:NameIDFormat>urn:oasis:names:tc:SAML:2.0:nameid-format:transient</md:NameIDFormat>
        <md:AssertionConsumerService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                                     Location="http://localhost:8080/bonita/saml" index="1"/>
    </md:SPSSODescriptor>
</md:EntityDescriptor>'''
        //destFile.text == expected

        // def expectedXmlText = new File(this.class.getResource("/to7_4_0/$expectedXml").file).text
        XMLUnit.setIgnoreWhitespace(true)
        def xmlResult = destFile.text
        logger.info """
Result:
*********
$xmlResult
*********
"""
        checkReturnedXml(expected, xmlResult)

    }

    private void checkReturnedXml(String expectedXml, String returnedXml) {
        final List<Diff> allDifferences = new DetailedDiff(XMLUnit.compareXML(expectedXml, returnedXml))
                .getAllDifferences()
        if (!allDifferences.isEmpty()) {
            allDifferences.each { diff ->
                //ignore @id attribute values generated at migration time
                def description = diff.getProperties().get("description")
                switch (description) {
                    case "attribute value":
                        def ignoredID = ["ID", "URI"]
                        def testNodeName = (diff.getProperties().get("testNodeDetail") as NodeDetail).node.name
                        def controlNodeName = (diff.getProperties().get("controlNodeDetail") as NodeDetail).node.name
                        assert testNodeName == controlNodeName
                        assert ignoredID.contains(testNodeName)
                        break
                    case "text value":
                        //attribute id is here a text value
                        def textNodeIds = ["ds:X509Certificate"]
                        def controlNodeName = (diff.getProperties().get("controlNodeDetail") as NodeDetail).node.parentNode
                        def testNodeName = (diff.getProperties().get("testNodeDetail") as NodeDetail).node.parentNode
                        assert controlNodeName.name == testNodeName.name
                        // this assertion is designed to display test node content when node name is not in expected textNodeIds
                        logger.info("found difference in text node " + testNodeName.name)
                        assert textNodeIds.contains(testNodeName.name)// || controlNodeName.textContent == testNodeName.textContent

                        assert sanitize(controlNodeName.textContent) == sanitize(testNodeName.textContent)
                        break
                    default:
                        def testNodeName = (diff.getProperties().get("testNodeDetail") as NodeDetail).node
                        logger.info "ERROR: $diff not expected! ***\n${testNodeName.textContent}\n***"
                        assert diff == null
                }


            }
        }
    }


}
