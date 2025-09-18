package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out.SendMailOutPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailAdapter implements SendMailOutPort {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendMail(final String recipient, final String subject, final String body) {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            helper.setFrom(mailProperties.getFromAddress());
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
