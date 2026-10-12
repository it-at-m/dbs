package de.muenchen.oss.dbs.ticketing.eventing.dms.application.usecase;

import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.dms.DmsOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.ticketing.TicketHistoryOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.dms.config.DmsProperties;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsContentObjectInfo;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsContentObjectRequest;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsTarget;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.in.EventHandlerInPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out.TicketingOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.domain.model.Event;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandlingUseCase implements EventHandlerInPort {

    private static final String HISTORY_FILENAME = "änderungshistorie";
    private static final String HISTORY_FILEEXTENSION = "json";
    public static final String COMMUNICATION_FILENAME = "ticketverlauf";
    public static final String COMMUNICATION_FILEEXTENSION = "html";

    private final DmsProperties dmsProperties;
    private final DmsOutPort dmsOutPort;
    private final TicketingOutPort ticketingOutPort;
    private final TicketHistoryOutPort ticketHistoryOutPort;

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
            //check if the ticket contains all relevant data
            if (!ticketMeetsRequirements(ticket)) {
                log.debug("Ticket not relevant");
                return;
            }

            final DmsTarget dmsTarget = new DmsTarget(
                    ticket.getEakteCooTarget(),
                    ticket.getEakteUserlogin(),
                    ticket.getEakteJoboe(),
                    ticket.getEakteJobposition());

            final List<ArticleInternal> articles = ticket.getArticles();

            if (articles == null) {
                log.debug("Articles empty");
                return;
            }

            final List<DmsContentObjectRequest> attachmentsContentObjects = processTicketHistoryAndCommunication(ticket, articles, dmsTarget);

            if (isArticleEvent(event)) {
                attachmentsContentObjects.addAll(generateAttachmentsContentObjects(ticket, articles));
            }

            if (!attachmentsContentObjects.isEmpty()) {
                dmsOutPort.addContentObjectsToIncoming(dmsTarget, attachmentsContentObjects);
            }

            log.info("Event handled successfully");
        } catch (final Exception e) {
            log.error("Exception during event handling: {}", e.getMessage());
        }
    }

    private boolean isRelevantEvent(final Event event) {
        log.debug("checking event: {}", event);
        return dmsProperties.getRelevantActions().contains(event.action());
    }

    private boolean isArticleEvent(final Event event) {
        log.debug("checking article event: {}", event);
        return dmsProperties.getArticleActions().contains(event.action());
    }

    private boolean ticketMeetsRequirements(final TicketInternal ticket) {
        log.debug("Checking ticket requirements: {}", ticket.getEakteCooTarget());
        return isPresent(ticket.getEakteUserlogin())
                && isPresent(ticket.getEakteJoboe())
                && isPresent(ticket.getEakteCooTarget());
    }

    private boolean isPresent(final String str) {
        return str != null && !str.isEmpty();
    }

    private List<DmsContentObjectRequest> processTicketHistoryAndCommunication(final TicketInternal ticket, final List<ArticleInternal> articles,
            final DmsTarget dmsTarget) {
        final List<DmsContentObjectRequest> addToIncomingContentObject = new ArrayList<>(List.of());
        final String ticketHistory = ticketHistoryOutPort.getTicketHistory(ticket.getId());
        final DmsContentObjectRequest historyContentObject = genertateHistoryContentObject(ticketHistory);

        final DmsContentObjectRequest communicationContentObject = genertateCommunicationContentObject(articles);

        final List<DmsContentObjectInfo> dmsContentObjectInfos = dmsOutPort.getIncomingContentObjects(dmsTarget);

        if (dmsContentObjectInfos.isEmpty()) {
            addToIncomingContentObject.add(historyContentObject);
            //TODO add communication
        } else {
            final DmsContentObjectInfo dmsHistoryContentObject = getContentObject(dmsContentObjectInfos, HISTORY_FILENAME, HISTORY_FILEEXTENSION);

            if (dmsHistoryContentObject == null) {
                addToIncomingContentObject.add(historyContentObject);
            } else {
                dmsOutPort.updateContentObject(new DmsTarget(dmsHistoryContentObject.getCoo(), dmsTarget), historyContentObject);
            }

            final DmsContentObjectInfo dmsCommunicationContentObject = getContentObject(dmsContentObjectInfos, COMMUNICATION_FILENAME,
                    COMMUNICATION_FILEEXTENSION);

            if (dmsCommunicationContentObject == null) {
                addToIncomingContentObject.add(communicationContentObject);
            } else {
                dmsOutPort.updateContentObject(new DmsTarget(dmsCommunicationContentObject.getCoo(), dmsTarget), communicationContentObject);
            }

        }

        return addToIncomingContentObject;
    }

    private DmsContentObjectInfo getContentObject(final List<DmsContentObjectInfo> dmsContentObjectInfos, final String filename, final String fileextension) {
        return dmsContentObjectInfos.stream()
                .filter(object -> Objects.equals(object.getFilename(), filename)
                        && Objects.equals(object.getFileextension(), fileextension))
                .findFirst()
                .orElse(null);
    }

    private DmsContentObjectRequest genertateHistoryContentObject(final String history) {
        final byte[] bytes = history.getBytes(StandardCharsets.UTF_8);
        return new DmsContentObjectRequest(HISTORY_FILENAME + "." + HISTORY_FILEEXTENSION, new ByteArrayInputStream(bytes));
    }

    private DmsContentObjectRequest genertateCommunicationContentObject(final List<ArticleInternal> articles) {
        //TODO add communication
        return null;
    }

    private List<DmsContentObjectRequest> generateAttachmentsContentObjects(final TicketInternal ticket, final List<ArticleInternal> articles) {
        final List<ArticleInternal> articlesToSave;
        if (ticket.getEakteWiederaufsetzpunkt() != null) {
            final ArticleInternal lastSavedArticle = articles.stream()
                    .filter(article -> Objects.equals(article.getId(), ticket.getEakteWiederaufsetzpunkt()))
                    .findFirst()
                    .orElse(null);

            if (lastSavedArticle != null) {
                final int lastSavedArticleIndex = articles.indexOf(lastSavedArticle);
                articlesToSave = articles.stream()
                        .skip(lastSavedArticleIndex + 1)
                        .toList();
            } else {
                articlesToSave = List.of();
            }
        } else {
            articlesToSave = articles;
        }

        final List<DmsContentObjectRequest> dmsContentObjectRequests = new ArrayList<>(List.of());

        articlesToSave.forEach(article -> {
            if (article.getAttachments() != null) {
                article.getAttachments().forEach(attachment -> {
                    final InputStream content = ticketingOutPort.getAttachmentContent(article.getTicketId(), article.getId(), attachment.getId());
                    dmsContentObjectRequests.add(new DmsContentObjectRequest(attachment.getId() + "_" + attachment.getFilename(), content));
                });
            }
        });
        return dmsContentObjectRequests;
    }
}
