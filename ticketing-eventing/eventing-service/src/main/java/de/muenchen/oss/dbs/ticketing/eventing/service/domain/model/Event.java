package de.muenchen.oss.dbs.ticketing.eventing.service.domain.model;

public record Event(
        String action,
        String ticket,
        String status,
        String status_id,
        String anliegenart,
        String lhmExtId
// TODO additional payload or everything/partial as map?
) {
}
