package de.muenchen.dbs.ticketing.dprs.api;

import java.time.LocalDateTime;

public record PasswordDTO(String password, LocalDateTime validUntil) {
}
