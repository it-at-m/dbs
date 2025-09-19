package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import java.io.InputStream;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailMessage {
    private String recipient;
    private String subject;
    private String body;
    private Map<String, InputStream> attachments;
}
