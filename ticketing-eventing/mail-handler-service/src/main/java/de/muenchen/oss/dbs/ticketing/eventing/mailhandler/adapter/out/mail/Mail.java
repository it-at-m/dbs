package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;
import java.util.Map;

@Data
@AllArgsConstructor
public class Mail {
    private String recipient;
    private String subject;
    private String body;
    private Map<String, InputStream> attachments;
}
