package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail.Mail;

public interface SendMailOutPort {
    void sendMail(Mail mail);
}
