package de.muenchen.dbs.ticketing.dprs.util;

import jakarta.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
public class AESGenerator {

    @Test
    public void generateAesKeys() throws Exception {
        // get AES 256 bits (32 bytes) key
        SecretKey secretKey = generate256BitAESKey();

        // AES-GCM needs IV 96-bit (12 bytes)
        byte[] iv = generateRandom12BitNonce();

        log.info("------ AES GCM Encryption Generator ------");
        log.info("Key (hex) ➡ {}", DatatypeConverter.printHexBinary(secretKey.getEncoded()));
        log.info("IV (hex)  ➡ {}", DatatypeConverter.printHexBinary(iv));

    }

    // "IV"
    private static byte[] generateRandom12BitNonce() {
        byte[] nonce = new byte[12];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    // AES secret key
    private static SecretKey generate256BitAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }
}
