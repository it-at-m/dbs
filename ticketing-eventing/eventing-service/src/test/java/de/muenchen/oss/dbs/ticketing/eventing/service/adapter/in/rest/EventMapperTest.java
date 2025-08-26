package de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import org.junit.jupiter.api.Test;

class EventMapperTest {
    private final static String ACTION = "Test Action";
    private final static String TICKET_ID = "123";
    private final static String STATUS = "open";
    private final static String STATUS_ID = "1";
    private final static String ANLIEGENART = "Test Anliegen";
    private final static String LHM_EXT_ID = "lhm-123";

    private final EventMapper eventMapper = new EventMapperImpl();

    @Test
    void testEventFromDto() {
        final EventDTO eventDTO = new EventDTO(
                TICKET_ID, STATUS, STATUS_ID, ANLIEGENART, LHM_EXT_ID);
        final Event event = eventMapper.fromDto(ACTION, eventDTO);
        assertEquals(ACTION, event.action());
        assertEquals(TICKET_ID, event.ticket());
        assertEquals(STATUS, event.status());
        assertEquals(STATUS_ID, event.statusId());
        assertEquals(ANLIEGENART, event.anliegenart());
        assertEquals(LHM_EXT_ID, event.lhmExtId());
    }
}
