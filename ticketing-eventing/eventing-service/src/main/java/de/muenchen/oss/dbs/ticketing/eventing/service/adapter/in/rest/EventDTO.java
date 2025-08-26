package de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest;

public record EventDTO(
        String ticket,
        String status,
        String status_id,
        String anliegenart,
        String lhmExtId
// TODO additional payload or everything/partial as map?
) {
}
