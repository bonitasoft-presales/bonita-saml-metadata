/*
 * Copyright (C) 2018 Bonitasoft S.A.
 * Bonitasoft is a trademark of Bonitasoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * or Bonitasoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 */
package keycloak.saml.metadata;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CertificateGenerator {

    public static KeyStore generateCertificate(String host, int validityInDays, String privateKeyPassword,
            String certAlias) throws Exception {

        SecureRandom random = new SecureRandom();

        // create keypair
        KeyPair keypair = generateKeyPair();
        X509Certificate certificate = generateSelfSignedCertificate(host, validityInDays, random, keypair);

        KeyStore keyStore = createKeyStore();

        keyStore.setKeyEntry(certAlias, keypair.getPrivate(), privateKeyPassword.toCharArray(),
                new Certificate[] { certificate });

        return keyStore;
    }

    private static KeyPair generateKeyPair() throws Exception {
        SecureRandom random = new SecureRandom();
        java.security.KeyPairGenerator keypairGen = java.security.KeyPairGenerator.getInstance("RSA");
        keypairGen.initialize(1024, random);
        KeyPair keypair = keypairGen.generateKeyPair();
        return keypair;
    }

    private static X509Certificate generateSelfSignedCertificate(String host, int validityInDays, SecureRandom random,
            KeyPair keypair) throws IOException, OperatorCreationException, CertificateException {
        // fill in certificate fields
        X500Name subject = new X500NameBuilder(BCStyle.INSTANCE)
                .addRDN(BCStyle.CN, host)
                .build();
        byte[] id = new byte[20];
        random.nextBytes(id);
        BigInteger serial = new BigInteger(160, random);
        Instant today = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant();
        X509v3CertificateBuilder certificate = new JcaX509v3CertificateBuilder(
                subject,
                serial,
                Date.from(today),
                Date.from(today.plus(validityInDays, ChronoUnit.DAYS)),
                subject,
                keypair.getPublic());
        certificate.addExtension(Extension.subjectKeyIdentifier, false, id);
        certificate.addExtension(Extension.authorityKeyIdentifier, false, id);
        BasicConstraints constraints = new BasicConstraints(true);
        certificate.addExtension(
                Extension.basicConstraints,
                true,
                constraints.getEncoded());
        KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.digitalSignature);
        certificate.addExtension(Extension.keyUsage, false, usage.getEncoded());
        ExtendedKeyUsage usageEx = new ExtendedKeyUsage(new KeyPurposeId[] {
                KeyPurposeId.id_kp_serverAuth,
                KeyPurposeId.id_kp_clientAuth
        });
        certificate.addExtension(
                Extension.extendedKeyUsage,
                false,
                usageEx.getEncoded());

        // build BouncyCastle certificate
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .build(keypair.getPrivate());
        X509CertificateHolder holder = certificate.build(signer);

        // convert to JRE certificate
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        converter.setProvider(new

        BouncyCastleProvider());
        return converter.getCertificate(holder);
    }

    private static KeyStore createKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);//init new keystore
        return keyStore;
    }

}
