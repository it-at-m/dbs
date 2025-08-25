package de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest;

import de.muenchen.oss.dbs.ticketing.eventing.service.core.port.in.HandleEventInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController("/api/")
@RequiredArgsConstructor
public class RestAdapter {
    private final HandleEventInPort handleEventInPort;

    @PostMapping("event")
    void event() {

    }
}
