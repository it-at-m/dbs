package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.usecase;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.in.EventHandlerInPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out.TicketingOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.domain.model.Event;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out.SendMailOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.config.MailHandlerProperties;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandlingUseCase implements EventHandlerInPort {
    private static final String INTERNAL_ATTACHMENTS_ARTICLE_TITLE = "Interner Artikel für interne Anhänge.";
    private static final String FORM_ATTACHMENT_NAME = "XML-Daten.xml";

    private final XmlMapper xmlMapper = new XmlMapper();
    private final MailHandlerProperties mailHandlerProperties;
    private final TicketingOutPort ticketingOutPort;
    private final SendMailOutPort sendMailOutport;

    @Override
    public void handleEvent(final Event event) {
        // check if should handle event
        if (!isRelevantEvent(event)) {
            log.debug("Event not relevant");
            return;
        }
        log.info("Handling event");
        // find event ticket
        final TicketInternal ticket = ticketingOutPort.getTicket(event.ticket());
        // get parsed form
        final Map<String, Object> form = getParsedForm(ticket);
        // send mail
        final String subject = buildSubject(ticket, form);
        final String body = buildBody(ticket);
        sendMailOutport.sendMail(
                mailHandlerProperties.getRecipient(), subject, body);
        log.info("Handled event successfully");
    }

    private boolean isRelevantEvent(final Event event) {
        log.debug("checking event: " + event);
        return
        // state was changed
        mailHandlerProperties.getStateChangeAction().equals(event.action()) &&
        // new state is closed
                mailHandlerProperties.getClosedState().equals(event.status()) &&
                // is relevant anliegen
                mailHandlerProperties.getRelevantTicketTypes().contains(event.anliegenart()) &&
                // user does have an lhmExtId (i.e. BayernID or BundID user)
                event.lhmExtId() != null && !event.lhmExtId().isEmpty();
    }

    private String buildSubject(final TicketInternal ticket, final Map<String, Object> form) {
        return "[%s;%s;%s;%s] Ihr Anliegen '%s' wurde abschließend bearbeitet"
                .formatted(
                        form.get("legacyPostkorbHandle"),
                        form.get("accountSource"),
                        form.get("ticketingVertrauensniveau"),
                        "Dummy",
                        ticket.getTitle());
    }

    private String buildBody(final TicketInternal ticket) {
        assert ticket.getArticles() != null;
        return ticket.getArticles().stream()
                // only public articles of type "web" or "note"
                .filter(i -> Boolean.FALSE.equals(i.getInternal()) &&
                        (ArticleInternal.TypeEnum.WEB.equals(i.getType()) || ArticleInternal.TypeEnum.NOTE.equals(i.getType())))
                // format single article
                .map(i -> "Titel: %s<br>Body: %s".formatted(i.getSubject(), i.getBody()))
                // build body
                .collect(Collectors.joining("<hr>"));

    }

    private Map<String, Object> getParsedForm(final TicketInternal ticket) {
        // find article with internal attachments
        assert ticket.getArticles() != null;
        final ArticleInternal internalAttachmentsArticle = ticket.getArticles().stream()
                .filter(i -> INTERNAL_ATTACHMENTS_ARTICLE_TITLE.equals(i.getSubject()))
                .findFirst().orElseThrow(() -> new IllegalStateException(
                        "Couldn't find internal attachments article with subject '%s'"
                                .formatted(INTERNAL_ATTACHMENTS_ARTICLE_TITLE)));
        // find form attachment
        assert internalAttachmentsArticle.getAttachments() != null;
        final String formAttachmentId = internalAttachmentsArticle.getAttachments().stream()
                .filter(attachment -> FORM_ATTACHMENT_NAME.equals(attachment.getFilename()))
                .findFirst().orElseThrow(() -> new IllegalStateException(
                        "Couldn't find form attachment with filename '%s' on internal attachments article with id %s"
                                .formatted(FORM_ATTACHMENT_NAME, internalAttachmentsArticle.getId())))
                .getId();
        // load content of form attachment
        try (InputStream formContent = ticketingOutPort.getAttachmentContent(ticket.getId(),
                internalAttachmentsArticle.getId(), formAttachmentId)) {
            // parse form
            return xmlMapper.readValue(formContent, Map.class);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
