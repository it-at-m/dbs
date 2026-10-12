package de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.ticketing;

public interface TicketHistoryOutPort {
    String getTicketHistory(String ticketId);
}
