package de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest;

import de.muenchen.oss.dbs.ticketing.eventing.service.configuration.DbsEventingProperties;
import de.muenchen.oss.dbs.ticketing.eventing.service.core.port.in.HandleEventInPort;
import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Service
@RestController("/api/")
@RequiredArgsConstructor
@Slf4j
public class RestAdapter {
    private static final String TRIGGER_HEADER = "X-Zammad-Trigger";
    private static final String ID_HEADER = "X-Zammad-Delivery";

    private final DbsEventingProperties dbsEventingProperties;
    private final HandleEventInPort handleEventInPort;
    private final EventMapper eventMapper;

    @PostMapping("event")
    public void event(
            @RequestHeader(TRIGGER_HEADER) final String trigger,
            @RequestHeader(ID_HEADER) final String deliveryId,
            @RequestBody final EventDTO eventDto) {
        log.info("Received event {} from trigger {}: {}", deliveryId, trigger, eventDto);
        final String action = dbsEventingProperties.getTriggerMapping().get(trigger);
        if (action == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No mapping for trigger %s".formatted(trigger));
        }
        final Event event = eventMapper.fromDto(action, eventDto);
        log.debug("Mapped trigger {} and payload to event {}", trigger, event);
        handleEventInPort.handleEvent(event);
        log.debug("Handled event successfully");
    }
}
