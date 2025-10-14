package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out;

import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.domain.model.MailMessage;

public interface SendMailOutPort {
    void sendMail(MailMessage mailMessage);
}
