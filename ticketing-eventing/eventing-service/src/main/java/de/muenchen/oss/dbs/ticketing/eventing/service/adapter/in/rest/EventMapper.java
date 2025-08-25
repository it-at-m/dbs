package de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest;

import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface EventMapper {
    @Mapping(source = "dto.status_id", target = "statusId")
    Event fromDto(String action, EventDTO dto);
}
