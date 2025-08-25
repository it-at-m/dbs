package de.muenchen.oss.dbs.ticketing.eventing.service.domain.model;

import jakarta.validation.constraints.NotBlank;

public record Event(
        @NotBlank String action,
        @NotBlank String ticket,
        String status,
        String status_id,
        String anliegenart,
        String lhmExtId
// TODO additional payload or everything/partial as map?
) {
}
