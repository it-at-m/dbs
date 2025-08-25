package de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest;

import static de.muenchen.oss.dbs.ticketing.eventing.service.configuration.OpenAPIDocumentationConfiguration.BASIC_SCHEME_NAME;

import de.muenchen.oss.dbs.ticketing.eventing.service.configuration.DbsEventingProperties;
import de.muenchen.oss.dbs.ticketing.eventing.service.core.port.in.HandleEventInPort;
import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RestController
@RequiredArgsConstructor
@Slf4j
public class RestAdapter {
    private static final String HEADER_TRIGGER_NAME = "X-Zammad-Trigger";
    private static final String HEADER_DELIVERY_ID = "X-Zammad-Delivery";

    private final DbsEventingProperties dbsEventingProperties;
    private final HandleEventInPort handleEventInPort;
    private final EventMapper eventMapper;

    /**
     * Endpoint for receiving Zammad webhook events.
     *
     * @param triggerName Name of the trigger of the event
     * @param deliveryId Random unique identifier of event
     * @param eventDto Event payload
     */
    @PostMapping("/api/event")
    @SecurityRequirement(name = BASIC_SCHEME_NAME)
    @ApiResponse(responseCode = "200", description = "Successfully processed event")
    public void event(
            @RequestHeader(HEADER_TRIGGER_NAME) final String triggerName,
            @RequestHeader(HEADER_DELIVERY_ID) final String deliveryId,
            @RequestBody final EventDTO eventDto) {
        log.info("Received event '{}' from trigger '{}': {}", deliveryId, triggerName, eventDto);
        final String action = dbsEventingProperties.mapTriggerToAction(triggerName);
        if (action == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No mapping for trigger '%s'".formatted(triggerName));
        }
        final Event event = eventMapper.fromDto(action, eventDto);
        log.debug("Mapped trigger '{}' and payload to event {}", triggerName, event);
        handleEventInPort.handleEvent(event);
        log.debug("Handled event '{}' successfully", deliveryId);
    }
}
