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

import org.bouncycastle.util.encoders.Base64
import org.slf4j.Logger
import spock.lang.Specification
import spock.lang.Unroll
import sun.security.provider.X509Factory

import java.security.KeyStore

class CertificateGeneratorTest extends Specification {

    Logger logger= org.slf4j.LoggerFactory.getLogger(this.class)

    @Unroll
    def "random certificate for alias #alias ans password #secret"(String secret,String alias) {
        given:
        KeyStore keyStore = CertificateGenerator.generateCertificate("localhohst", 365, secret, alias)


        when:
        def encodedKey = keyStore.getKey(alias, secret.toCharArray()).encoded
        def encodedCertificate = keyStore.getCertificate(alias).encoded


        then:
        encodedKey != null
        encodedCertificate != null
        logger.info("""

alias: $alias
password: $secret
key:
${Base64.toBase64String(encodedKey)}
certificate:
${Base64.toBase64String(encodedCertificate)}
""")

        where:
        secret    | alias
        "secret1" | "alias1"
        "secret2" | "alias2"
        "secret3" | "alias3"
        "secret4" | "alias4"
    }

    def "should read PEM"(){
        given:
        def pem= """
$X509Factory.BEGIN_CERT
MIICLTCCAZagAwIBAgIVAJ02CdC4SdJBXZDwZXE+O85oxmI8MA0GCSqGSIb3DQ
EBCwUAMBUxEzARBgNVBAMMCmxvY2FsaG9oc3QwHhcNMTkwNjA3MDAwMDAwWhcN
MjAwNjA2MDAwMDAwWjAVMRMwEQYDVQQDDApsb2NhbGhvaHN0MIGfMA0GCSqGSI
b3DQEBAQUAA4GNADCBiQKBgQCFfmWZYjv0mz+QeYo85Mx7rDKRqhswx/byuKMO
kNBMpBsVG3uTtlUkwgTIVQTXSNCDkdFnKpj5m4epHK0Rul4pJcIRi+OyJpH4DC
83MH0Z/uTCficiIOg1CM8bKBxSkWRya3MltHdvuWdnIZ9H66Rr+j86S5x5Eqq2
5icvAPCDbwIDAQABo3kwdzAbBgNVHQ4EFDekhfiX4fBSOIWmHqSp3w0nEi5lMB
sGA1UdIwQUN6SF+Jfh8FI4haYepKnfDScSLmUwDwYDVR0TAQH/BAUwAwEB/zAL
BgNVHQ8EBAMCAoQwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA0GCS
qGSIb3DQEBCwUAA4GBAERw+r2gx7qlolKmSU7kKnavn56KKunR6keiZBsc8DhO
rXGtLMMNk6lbN96+0reJlJ4QWo00n0wHGvoWPl8Y3vYFqk8mp088jSGgvmbJTX
4VU2xltnMgBEW2WNCLveX4dbmweCe5W9p1T0rX+3q+fg313meY1qH/41wo9bjPhQlJ
$X509Factory.END_CERT
"""

        when:
        def x509Certificate = CertificateGenerator.convertToX509Certificate(pem)

        then:
        x509Certificate!=null
    }

    def "should read KEY"(){
        given:
        def pem= """
-----BEGIN PRIVATE KEY-----
MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIV+ZZliO/SbP5B5ijzkzHusMpGqGzDH9v
K4ow6Q0EykGxUbe5O2VSTCBMhVBNdI0IOR0WcqmPmbh6kcrRG6XiklwhGL47ImkfgMLzcwfRn+5MJ+JyIg
6DUIzxsoHFKRZHJrcyW0d2+5Z2chn0frpGv6PzpLnHkSqrbmJy8A8INvAgMBAAECgYBrVWOvXjLCzAjhyz
eBGp3nWl8Wi+Vs5XsDUb2ZOoqg9NnpsL092T7AXVenE+TejGdLQnVSNAfnukvrh2kIdga6d1AyxTBBAnYi
d6fO55arCz5h9I2sxxiHq2Mg1aK4JapQ7kV7gmpsydtOR1aj8trH+47mKB6h7W50pbkuFbbtUQJBANFpWf
jmyaBiGRCPIL4mIPHPE5r4RXBOeHD3uVy5/eGSYknPfk2itNiFIRMqSyeBJQy7qNin8z7nVy56+xA+m40C
QQCjMUmHQVIv95az/AM2AKOOZ3EEoQO1HMJ41QoNDNskOz2waUJK4v9lzgiYvMaySPuOMObaF3t/ju8GYI
C0q93rAkAruYejxOyMpG7/WiNIEMYIN2isjvJQxNG5JMB7cApmsx36s2x34WVJYGLLkaUnlnuZ8QUMV+Ue
WUeG2P7bIXdxAkEAkpsorzXjm+wVm0P/rngMd1Y+liYAqqhXnS3mharTBE2kMeOeBqC/SLo7xttwtaITO7
565drKu+Gwok330lfEWQJBAI7VcUMUzMxJmycT+NAkwvuEshrdLLyHTI3anXjokFLPj5hqSkcSQotRq1yH
DQs0spRvdbqGgVYpdoiel+5XLwg=
-----END PRIVATE KEY-----

"""

        when:
        def x509Certificate = CertificateGenerator.convertToPrivateKey(pem)

        then:
        x509Certificate!=null
    }
}
