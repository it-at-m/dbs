package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out.SendMailOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailAdapter implements SendMailOutPort {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendMail(final String recipient, final String subject, final String body) {
        final SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(mailProperties.getFromAddress());
        mail.setTo(recipient);
        mail.setSubject(subject);
        mail.setText(body);
        mailSender.send(mail);
    }
}
