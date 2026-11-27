package de.muenchen.dbs.ticketing.dprs.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import jakarta.xml.bind.DatatypeConverter;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ResetKeyGenerator {

    public static final String SEPARATOR = "#";
    private static final String TOKEN_VERSION = "v1";
    private static final int GCM_IV_LENGTH_BYTES = 12;

    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public ResetKeyGenerator(@Value("${prs.aes.key}") String aesKey) {
        byte[] decodedKey = DatatypeConverter.parseHexBinary(aesKey);
        secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public String generateResetKey(String ticketNumber, LocalDateTime validUntil) throws IllegalArgumentException {
        String valueToEncrypt = ticketNumber + SEPARATOR + validUntil;
        byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);
        byte[] cipherText = EncryptorAesGcm.encrypt(valueToEncrypt.getBytes(UTF_8), secretKey, iv);

        return TOKEN_VERSION + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(iv)
                + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(cipherText);
    }

    public LocalDateTime getValidUntilDate(String resetKey) {
        try {
            return parseResetKey(resetKey).validUntil();
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            log.warn("Invalid reset key received");
            return null;
        }
    }

    public String getTicketNumberFromResetKey(String resetKey) {
        return parseResetKey(resetKey).ticketNumber();
    }

    private ResetKey parseResetKey(String resetKey) {
        String[] tokenParts = resetKey.split("\\.", -1);
        if (tokenParts.length != 3 || !TOKEN_VERSION.equals(tokenParts[0])) {
            throw new IllegalArgumentException("Invalid reset key format");
        }

        byte[] iv = Base64.getUrlDecoder().decode(tokenParts[1]);
        if (iv.length != GCM_IV_LENGTH_BYTES) {
            throw new IllegalArgumentException("Invalid reset key initialization vector");
        }

        String[] payload = EncryptorAesGcm.decrypt(Base64.getUrlDecoder().decode(tokenParts[2]), secretKey, iv)
                .split(SEPARATOR, -1);
        if (payload.length != 2 || payload[0].isBlank()) {
            throw new IllegalArgumentException("Invalid reset key payload");
        }

        return new ResetKey(payload[0], LocalDateTime.parse(payload[1]));
    }

    private record ResetKey(String ticketNumber, LocalDateTime validUntil) {
    }
}
