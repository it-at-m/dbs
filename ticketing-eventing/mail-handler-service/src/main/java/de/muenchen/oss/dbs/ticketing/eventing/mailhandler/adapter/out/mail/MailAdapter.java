package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out.SendMailOutPort;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailAdapter implements SendMailOutPort {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendMail(Mail mail) {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(mailProperties.getFromAddress());
            helper.setTo(mail.getRecipient());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getBody(), true);
            for (Map.Entry<String, InputStream> e : mail.getAttachments().entrySet()) {
                //TODO is there a way to do this with streaming? The following line loads the attachment into RAM...
                DataSource dataSource = new ByteArrayDataSource(e.getValue(), "application/octet-stream");
                helper.addAttachment(e.getKey(), dataSource);
                //maybe like this?
                //helper.addAttachment(e.getKey(), new InputStreamSourceImpl(e.getValue()));
            }
            mailSender.send(mimeMessage);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private class InputStreamSourceImpl implements InputStreamSource {
//        private final InputStream inputStream;
//
//        public InputStreamSourceImpl(InputStream inputStream) {
//            this.inputStream = inputStream;
//        }
//
//        @Override
//        public InputStream getInputStream() {
//            return inputStream;
//        }
//    }

}
