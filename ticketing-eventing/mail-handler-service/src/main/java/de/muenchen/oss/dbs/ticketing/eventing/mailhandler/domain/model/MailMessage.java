package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.domain.model;

import java.io.InputStream;
import java.util.Collection;

public record MailMessage(
        String recipient,
        String subject,
        String body,
        Collection<Attachment> attachments) {
    public record Attachment(
            String filename,
            String mimeType,
            InputStream content) {
    }
}
