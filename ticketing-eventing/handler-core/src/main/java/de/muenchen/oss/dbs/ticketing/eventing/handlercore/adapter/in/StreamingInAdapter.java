package de.muenchen.oss.dbs.ticketing.eventing.handlercore.adapter.in;

import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.in.EventHandlerInPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.domain.model.Event;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingInAdapter {
    private final EventHandlerInPort eventHandlerInPort;

    /**
     * Consumer for Zammad events sent via Kafka from the eventing-service.
     *
     * @return The consumer.
     */
    @Bean
    public Consumer<Message<Event>> event() {
        return message -> {
            eventHandlerInPort.handleEvent(message.getPayload());
        };
    }
}
