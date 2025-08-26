package de.muenchen.oss.dbs.ticketing.eventing.service.adapter.out.streaming;

import de.muenchen.oss.dbs.ticketing.eventing.service.core.port.out.SendEventOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.service.domain.exception.MessageOutException;
import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingAdapter implements SendEventOutPort {
    private static final String BINDING_NAME = "event-out";

    private final StreamBridge streamBridge;

    @Override
    public void sendEvent(final Event event) {
        final boolean successful;
        try {
            successful = streamBridge.send(BINDING_NAME, event);
        } catch (final RuntimeException e) {
            throw new MessageOutException("Exception while sending event %s".formatted(event), e);
        }
        if (!successful) {
            throw new MessageOutException("Event couldn't be send %s".formatted(event));
        }
    }
}
