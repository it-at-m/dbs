package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out.SendMailOutPort;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailAdapter implements SendMailOutPort {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final Tika tika = new Tika();

    @Override
    public void sendMail(final MailMessage mailMessage) {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED);
            helper.setFrom(mailProperties.getFromAddress());
            helper.setTo(mailMessage.getRecipient());
            helper.setSubject(mailMessage.getSubject());
            helper.setText(mailMessage.getBody(), true);
            for (final Map.Entry<String, InputStream> entry : mailMessage.getAttachments().entrySet()) {
                //TODO is there a way to do this with streaming? The following line loads the attachment into RAM...
                InputStream bis = new BufferedInputStream(entry.getValue());
                String mime = detectMimeType(bis);
                log.debug("Mime-Type for " + entry.getKey() + " found " + mime);
                final DataSource dataSource = new ByteArrayDataSource(bis, mime);
                helper.addAttachment(entry.getKey(), dataSource);
                //maybe like this?
                //helper.addAttachment(e.getKey(), new InputStreamSourceImpl(e.getValue()));
            }
            mailSender.send(mimeMessage);
        } catch (final MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String detectMimeType(InputStream inputStream) throws IOException {
        return tika.detect(inputStream);
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
