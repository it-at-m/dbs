package de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.usecase;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleAttachment;
import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.UpdateTicketDTO;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.in.EventHandlerInPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out.TicketingOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.domain.model.Event;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.adapter.out.mail.MailMessage;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.application.port.out.SendMailOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.config.MailHandlerProperties;
import de.muenchen.oss.dbs.ticketing.eventing.mailhandler.exceptions.NoValidArticleException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
    public static final String TICKETING_VERTRAUENSNIVEAU = "ticketingVertrauensniveau";
    public static final String LEGACY_POSTKORB_HANDLE = "legacyPostkorbHandle";
    public static final String ACCOUNT_SOURCE = "accountSource";

    public static final String TO_POSTBOX_DEFAULT = "send";
    public static final String TO_POSTBOX_HIGH = "send_high_authLevel";

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
        try {
            // find event ticket
            final TicketInternal ticket = ticketingOutPort.getTicket(event.ticket());
            //check if ticket should be sent
            if (!isRelevantTicket(ticket)) {
                log.debug("Ticket not relevant");
                return;
            }
            // get parsed form
            final Map<String, Object> form = getParsedForm(ticket);
            //find relevant article
            final ArticleInternal article = findRelevantArticle(ticket);
            sendMail(ticket, form, article);

            //reset flag sende_nachricht_nach_extern
            resetTicket(ticket);

            log.info("Event handled successfully");
        } catch (NoValidArticleException e) {
            log.error(e.getMessage());
            log.error("Event NOT handled successfully");
        }
    }

    private boolean isRelevantEvent(final Event event) {
        log.debug("checking event: " + event);
        return
        // state was changed by trigger send-to-postbox
        mailHandlerProperties.getTicketChangeAction().equals(event.action()) &&
        // is relevant anliegen
                mailHandlerProperties.getRelevantTicketTypes().contains(event.anliegenart()) &&
                // user does have an lhmExtId (i.e. BayernID or BundID user)
                event.lhmExtId() != null && !event.lhmExtId().isEmpty();
    }

    private boolean isRelevantTicket(final TicketInternal ticket) {
        log.debug("Checking value of sende_nachricht_nach_extern: " + ticket.getSendeNachrichtNachExtern());
        return TO_POSTBOX_DEFAULT.equals(ticket.getSendeNachrichtNachExtern()) || TO_POSTBOX_HIGH.equals(ticket.getSendeNachrichtNachExtern());
    }

    private ArticleInternal findRelevantArticle(final TicketInternal ticket) throws NoValidArticleException {
        assert ticket.getArticles() != null;
        return ticket.getArticles().stream()
                // find last public articles of type "note"
                .filter(i -> Boolean.FALSE.equals(i.getInternal()) &&
                        ArticleInternal.TypeEnum.NOTE.equals(i.getType()))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new NoValidArticleException("no valid article found in ticket " + ticket.getId()));
    }

    private void sendMail(final TicketInternal ticket, final Map<String, Object> form, final ArticleInternal article) {
        final String recipient = mailHandlerProperties.getRecipient();
        final String subject = buildSubject(ticket, form);
        log.debug("Created subject: " + subject);
        final String body = buildBody(article);
        log.debug("Created body: " + body);
        final Map<String, InputStream> attachments = buildAttachments(article);
        sendMailOutport.sendMail(new MailMessage(recipient, subject, body, attachments));
    }

    private String buildSubject(final TicketInternal ticket, final Map<String, Object> form) {
        final String authlevel;
        if (form.get(TICKETING_VERTRAUENSNIVEAU) == null) {
            log.error("no ticketingVertrauensniveau found in ticket " + ticket.getId() + " - setting level1");
            authlevel = "level1";
        } else {
            authlevel = "3".equals(ticket.getSendeNachrichtNachExtern()) ? "level3" : String.valueOf(form.get(TICKETING_VERTRAUENSNIVEAU));
        }

        return "[%s;%s;%s;%s] Neue Nachricht zu Ihrem Anliegen '%s'"
                .formatted(
                        form.get(LEGACY_POSTKORB_HANDLE),
                        form.get(ACCOUNT_SOURCE),
                        authlevel,
                        "Zammad-Eventing",
                        ticket.getTitle());
    }

    private String buildBody(final ArticleInternal article) {
        return article.getBody();
    }

    private Map<String, InputStream> buildAttachments(final ArticleInternal article) {
        if (article.getAttachments() == null) {
            return new HashMap<>();
        }
        return article.getAttachments().stream().collect(Collectors.toMap(ArticleAttachment::getFilename,
                a -> ticketingOutPort.getAttachmentContent(article.getTicketId(), article.getId(), a.getId())));
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

    private void resetTicket(final TicketInternal ticket) {
        final UpdateTicketDTO updateTicketDTO = new UpdateTicketDTO();
        updateTicketDTO.setId(ticket.getId());
        updateTicketDTO.setDirektkennwort(ticket.getDirektkennwort());
        updateTicketDTO.setSendeNachrichtNachExtern(null);
        ticketingOutPort.updateTicket(updateTicketDTO);
    }
}
