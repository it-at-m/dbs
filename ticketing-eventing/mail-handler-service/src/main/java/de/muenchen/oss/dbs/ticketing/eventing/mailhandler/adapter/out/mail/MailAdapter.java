package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out.SendMailOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.domain.model.MailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailAdapter implements SendMailOutPort {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendMail(final MailMessage mailMessage) {
        log.debug("Sending mail {}", mailMessage);
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED);
            helper.setFrom(mailProperties.getFromAddress());
            helper.setTo(mailMessage.recipient());
            helper.setSubject(mailMessage.subject());
            helper.setText(mailMessage.body(), true);
            final List<File> tmpFiles = new ArrayList<>();
            for (final MailMessage.Attachment attachment : mailMessage.attachments()) {
                final TempFileInputStreamDataSource dataSource = new TempFileInputStreamDataSource(attachment.filename(), attachment.mimeType(),
                        attachment.content());
                helper.addAttachment(attachment.filename(), dataSource);
                tmpFiles.add(dataSource.getTempFile());
            }
            mailSender.send(mimeMessage);
            // cleanup tmp files
            for (final File tmpFile : tmpFiles) {
                if (!tmpFile.delete()) {
                    log.warn("Temp file {} couldn't be deleted", tmpFile);
                }
            }
            log.info("Sent mail '{}' to {}", mailMessage.subject(), mailMessage.subject());
        } catch (final MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
