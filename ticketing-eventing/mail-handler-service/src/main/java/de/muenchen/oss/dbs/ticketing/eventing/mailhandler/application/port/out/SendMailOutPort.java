package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out;

public interface SendMailOutPort {
    void sendMail(String recipient, String subject, String body);
}
