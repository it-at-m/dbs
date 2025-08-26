package de.muenchen.oss.dbs.ticketing.eventing.service.core.usecase;

import de.muenchen.oss.dbs.ticketing.eventing.service.core.port.in.HandleEventInPort;
import de.muenchen.oss.dbs.ticketing.eventing.service.core.port.out.SendEventOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandleEventUseCase implements HandleEventInPort {
    private final SendEventOutPort sendEventOutPort;

    @Override
    public void handleEvent(final Event event) {
        sendEventOutPort.sendEvent(event);
    }
}
