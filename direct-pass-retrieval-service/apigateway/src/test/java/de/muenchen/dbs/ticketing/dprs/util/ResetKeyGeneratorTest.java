package de.muenchen.dbs.ticketing.dprs.util;

import java.time.LocalDateTime;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Log4j2
public class ResetKeyGeneratorTest {

    private final ResetKeyGenerator unitToTest = new ResetKeyGenerator(
            "0000000000000000000000000000000000000000000000000000000000000000");

    @Test
    void generatesAndReadsResetKey() {
        LocalDateTime validUntil = LocalDateTime.now().plusHours(3);
        String resetKey = unitToTest.generateResetKey(
                "11000821",
                validUntil);
        log.info("resetKey: {}", resetKey);
        assertThat(unitToTest.getTicketNumberFromResetKey(resetKey)).isEqualTo("11000821");
        assertThat(unitToTest.getValidUntilDate(resetKey)).isEqualTo(validUntil);
    }

    @Test
    void rejectsMalformedResetKeys() {
        assertThat(unitToTest.getValidUntilDate("not-a-reset-key")).isNull();
        assertThatThrownBy(() -> unitToTest.getTicketNumberFromResetKey("not-a-reset-key"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
