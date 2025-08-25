package de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest;

import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
interface EventMapper {
    Event fromDto(String action, EventDTO dto);
}
