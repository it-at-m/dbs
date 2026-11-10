package de.muenchen.oss.dbs.ticketing.eventing.dms.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleAttachment;
import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.DmsOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.HtmlGenerationOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.dms.config.DmsProperties;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsContentObjectInfo;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsContentObjectRequest;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsRequestContext;
import de.muenchen.oss.dbs.ticketing.eventing.dms.domain.model.DmsTarget;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.application.port.out.TicketingOutPort;
import de.muenchen.oss.dbs.ticketing.eventing.handlercore.domain.model.Event;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventHandlingUseCaseTest {
    private static final String USERNAME = "user";
    private static final String JOBOE = "oe";
    private static final String INCOMING_COO_EMPTY_CONTENT_OBJECTS = "COO.incoming.empty";
    private static final String INCOMING_COO_WITH_CONTENT_OBJECTS = "COO.incoming.content";
    private static final String HISTORY_FILE_COO = "COO.history.file";
    private static final String COMMUNICATION_FILE_COO = "COO.communication.file";
    private static final DmsRequestContext REQUEST_CONTEXT = new DmsRequestContext(USERNAME, JOBOE, null);
    private static final DmsTarget INCOMING_TARGET_EMPTY_CONTENT_OBJECTS = new DmsTarget(INCOMING_COO_EMPTY_CONTENT_OBJECTS, REQUEST_CONTEXT);
    private static final DmsTarget INCOMING_TARGET_WITH_CONTENT_OBJECTS = new DmsTarget(INCOMING_COO_WITH_CONTENT_OBJECTS, REQUEST_CONTEXT);

    private static final List<String> RELEVANT_ACTIONS = List.of("ARTICLE_ACTION", "STATUS_ACTION", "RELEVANT_ACTION");
    private static final String RELEVANT_ACTION = "RELEVANT_ACTION";
    private static final String ARTICLE_ACTION = "ARTICLE_ACTION";
    private static final String NOT_RELEVANT_ACTION = "NOT_RELEVANT_ACTION";
    private static final List<String> ARTICLE_ACTIONS = List.of("ARTICLE_ACTION", "ARTICLE_ACTION_2", "ARTICLE_ACTION_3");
    private static final String TICKET_ID = "ticketId";
    private static final String HISTORY = "history";
    private static final String GENERATED_HISTORY = "generated_history";
    private static final String GENERATED_COMMUNICATION = "generated_communication";
    private static final String CUSTOMER_ID = "customer_id";
    private static final String OWNER_ID = "owner_id";
    private static final String ARTICLE_1 = "article1";
    private static final String ARTICLE_2 = "article2";

    private static final String HISTORY_FILENAME = "änderungshistorie";
    private static final String HISTORY_FILEEXTENSION = "html";
    private static final String COMMUNICATION_FILENAME = "ticketverlauf";
    private static final String COMMUNICATION_FILEEXTENSION = "html";

    private static final byte[] ATTACHMENT_1_BYTES = "Attachment_1".getBytes(StandardCharsets.UTF_8);
    private static final byte[] ATTACHMENT_2_BYTES = "Attachment_2".getBytes(StandardCharsets.UTF_8);

    private static final DmsContentObjectInfo HISTORY_FILE_INFO = new DmsContentObjectInfo(HISTORY_FILE_COO, HISTORY_FILENAME, HISTORY_FILEEXTENSION);
    private static final DmsContentObjectInfo COMMUNICATION_FILE_INFO = new DmsContentObjectInfo(COMMUNICATION_FILE_COO, COMMUNICATION_FILENAME,
            COMMUNICATION_FILEEXTENSION);

    @Mock
    private DmsProperties dmsProperties;

    @Mock
    private DmsOutPort dmsOutPort;

    @Mock
    private HtmlGenerationOutPort htmlGenerationOutPort;

    @Mock
    private TicketingOutPort ticketingOutPort;

    @InjectMocks
    private EventHandlingUseCase eventHandlingUseCase;

    @Test
    void ignoresEvent_whenActionIsNotRelevant() {
        // When
        when(dmsProperties.getRelevantActions()).thenReturn(RELEVANT_ACTIONS);

        final Event event = new Event(NOT_RELEVANT_ACTION, TICKET_ID, null, null, null, null);

        eventHandlingUseCase.handleEvent(event);

        // Then
        verifyNoInteractions(dmsOutPort, ticketingOutPort, htmlGenerationOutPort);
    }

    @Test
    void ignoresEvent_whenTicketDoesNotMeetRequirements() {
        // Given
        final TicketInternal ticket = createTicket(null, null, null);

        // When
        when(dmsProperties.getRelevantActions()).thenReturn(RELEVANT_ACTIONS);
        when(ticketingOutPort.getTicket(TICKET_ID)).thenReturn(ticket);

        final Event event = new Event(RELEVANT_ACTION, TICKET_ID, null, null, null, null);

        eventHandlingUseCase.handleEvent(event);

        // Then
        verify(ticketingOutPort).getTicket(TICKET_ID);
        verifyNoInteractions(dmsOutPort, htmlGenerationOutPort);
    }

    @Test
    void ignoresEvent_whenTicketHasNoArticles() {
        // Given
        final TicketInternal ticket = createTicket(INCOMING_COO_EMPTY_CONTENT_OBJECTS, null, null);

        // When
        when(dmsProperties.getRelevantActions()).thenReturn(RELEVANT_ACTIONS);
        when(ticketingOutPort.getTicket(TICKET_ID)).thenReturn(ticket);

        final Event event = new Event(ARTICLE_ACTION, TICKET_ID, null, null, null, null);

        eventHandlingUseCase.handleEvent(event);

        // Then
        verify(ticketingOutPort).getTicket(TICKET_ID);
        verifyNoInteractions(dmsOutPort, htmlGenerationOutPort);
    }

    @Test
    void addsHistoryAndCommunication_whenIncomingIsEmpty() {
        // Given
        final ArticleInternal article1 = createArticle(ARTICLE_1, null);

        final ArticleInternal article2 = createArticle(ARTICLE_2, null);

        final TicketInternal ticket = createTicket(INCOMING_COO_EMPTY_CONTENT_OBJECTS, null, List.of(article1, article2));

        final User customer = new User();
        customer.setId(CUSTOMER_ID);

        final User owner = new User();
        owner.setId(OWNER_ID);

        // When
        when(dmsProperties.getRelevantActions()).thenReturn(RELEVANT_ACTIONS);
        when(dmsProperties.getArticleActions()).thenReturn(ARTICLE_ACTIONS);
        when(ticketingOutPort.getTicket(TICKET_ID)).thenReturn(ticket);
        when(ticketingOutPort.getTicketHistory(TICKET_ID)).thenReturn(HISTORY);
        when(ticketingOutPort.getUserInfo(CUSTOMER_ID)).thenReturn(customer);
        when(ticketingOutPort.getUserInfo(OWNER_ID)).thenReturn(owner);
        when(htmlGenerationOutPort.generateHistoryHtml(HISTORY)).thenReturn(GENERATED_HISTORY);
        when(htmlGenerationOutPort.generateCommunicationHtml(ticket, ticket.getArticles(), customer, owner)).thenReturn(GENERATED_COMMUNICATION);
        when(dmsOutPort.getIncomingContentObjects(INCOMING_TARGET_EMPTY_CONTENT_OBJECTS)).thenReturn(List.of());

        final Event event = new Event(ARTICLE_ACTION, TICKET_ID, null, null, null, null);

        eventHandlingUseCase.handleEvent(event);

        // Then

        final ArgumentCaptor<List<DmsContentObjectRequest>> captor = ArgumentCaptor.forClass(List.class);

        verify(ticketingOutPort).getTicket(TICKET_ID);
        verify(dmsOutPort).addContentObjectsToIncoming(eq(INCOMING_TARGET_EMPTY_CONTENT_OBJECTS), captor.capture());
        verify(ticketingOutPort, never()).getAttachmentContent(any(), any(), any());
        verify(dmsOutPort, never()).updateContentObject(any(), any());

        final List<DmsContentObjectRequest> actual = captor.getValue();

        assertThat(actual)
                .extracting(DmsContentObjectRequest::name)
                .containsExactly(
                        HISTORY_FILENAME + "." + HISTORY_FILEEXTENSION,
                        COMMUNICATION_FILENAME + "." + COMMUNICATION_FILEEXTENSION);

    }

    @Test
    void addsAttachments_whenIncomingAlreadyContainsGeneratedFiles() {
        // Given

        final ArticleInternal article1 = createArticle(ARTICLE_1, null);

        final ArticleAttachment attachment1 = createAttachment("id1", "filename_1");

        final ArticleAttachment attachment2 = createAttachment("id2", "filename_2");

        final ArticleInternal article2 = createArticle(ARTICLE_2, List.of(attachment1, attachment2));

        final TicketInternal ticket = createTicket(INCOMING_COO_WITH_CONTENT_OBJECTS, null, List.of(article1, article2));

        final User customer = new User();
        customer.setId(CUSTOMER_ID);

        final User owner = new User();
        owner.setId(OWNER_ID);

        // When
        when(dmsProperties.getRelevantActions()).thenReturn(RELEVANT_ACTIONS);
        when(dmsProperties.getArticleActions()).thenReturn(ARTICLE_ACTIONS);
        when(ticketingOutPort.getTicket(TICKET_ID)).thenReturn(ticket);
        when(ticketingOutPort.getTicketHistory(TICKET_ID)).thenReturn(HISTORY);
        when(ticketingOutPort.getUserInfo(CUSTOMER_ID)).thenReturn(customer);
        when(ticketingOutPort.getUserInfo(OWNER_ID)).thenReturn(owner);
        when(ticketingOutPort.getAttachmentContent(TICKET_ID, article2.getId(), attachment1.getId())).thenReturn(new ByteArrayInputStream(ATTACHMENT_1_BYTES));
        when(ticketingOutPort.getAttachmentContent(TICKET_ID, article2.getId(), attachment2.getId())).thenReturn(new ByteArrayInputStream(ATTACHMENT_2_BYTES));
        when(htmlGenerationOutPort.generateHistoryHtml(HISTORY)).thenReturn(GENERATED_HISTORY);
        when(htmlGenerationOutPort.generateCommunicationHtml(ticket, ticket.getArticles(), customer, owner)).thenReturn(GENERATED_COMMUNICATION);
        when(dmsOutPort.getIncomingContentObjects(INCOMING_TARGET_WITH_CONTENT_OBJECTS)).thenReturn(List.of(HISTORY_FILE_INFO, COMMUNICATION_FILE_INFO));

        final Event event = new Event(ARTICLE_ACTION, TICKET_ID, null, null, null, null);

        eventHandlingUseCase.handleEvent(event);

        // Then

        final ArgumentCaptor<List<DmsContentObjectRequest>> captor = ArgumentCaptor.forClass(List.class);

        verify(ticketingOutPort).getTicket(TICKET_ID);
        verify(dmsOutPort).addContentObjectsToIncoming(eq(INCOMING_TARGET_WITH_CONTENT_OBJECTS), captor.capture());
        verify(dmsOutPort, times(2)).updateContentObject(any(DmsTarget.class), any(DmsContentObjectRequest.class));

        final List<DmsContentObjectRequest> actual = captor.getValue();

        assertThat(actual)
                .extracting(DmsContentObjectRequest::name)
                .containsExactly(
                        attachment1.getId() + "_" + attachment1.getFilename(),
                        attachment2.getId() + "_" + attachment2.getFilename());
    }

    @Test
    void addsAttachments_whenWiederaufsetzpunktIsSet() {
        // Given
        final ArticleAttachment attachmentSaved1 = createAttachment("idSaved1", "filename_saved_1");

        final ArticleAttachment attachmentSaved2 = createAttachment("idSaved2", "filename_saved_2");

        final ArticleInternal article1 = createArticle(ARTICLE_1, List.of(attachmentSaved1, attachmentSaved2));

        final ArticleAttachment attachment1 = createAttachment("id1", "filename_1");

        final ArticleAttachment attachment2 = createAttachment("id2", "filename_2");

        final ArticleInternal article2 = createArticle(ARTICLE_2, List.of(attachment1, attachment2));

        final TicketInternal ticket = createTicket(INCOMING_COO_WITH_CONTENT_OBJECTS, ARTICLE_1, List.of(article1, article2));

        final User customer = new User();
        customer.setId(CUSTOMER_ID);

        final User owner = new User();
        owner.setId(OWNER_ID);

        // When
        when(dmsProperties.getRelevantActions()).thenReturn(RELEVANT_ACTIONS);
        when(dmsProperties.getArticleActions()).thenReturn(ARTICLE_ACTIONS);
        when(ticketingOutPort.getTicket(TICKET_ID)).thenReturn(ticket);
        when(ticketingOutPort.getTicketHistory(TICKET_ID)).thenReturn(HISTORY);
        when(ticketingOutPort.getUserInfo(CUSTOMER_ID)).thenReturn(customer);
        when(ticketingOutPort.getUserInfo(OWNER_ID)).thenReturn(owner);
        when(ticketingOutPort.getAttachmentContent(TICKET_ID, article2.getId(), attachment1.getId())).thenReturn(new ByteArrayInputStream(ATTACHMENT_1_BYTES));
        when(ticketingOutPort.getAttachmentContent(TICKET_ID, article2.getId(), attachment2.getId())).thenReturn(new ByteArrayInputStream(ATTACHMENT_2_BYTES));
        when(htmlGenerationOutPort.generateHistoryHtml(HISTORY)).thenReturn(GENERATED_HISTORY);
        when(htmlGenerationOutPort.generateCommunicationHtml(ticket, ticket.getArticles(), customer, owner)).thenReturn(GENERATED_COMMUNICATION);
        when(dmsOutPort.getIncomingContentObjects(INCOMING_TARGET_WITH_CONTENT_OBJECTS)).thenReturn(List.of(HISTORY_FILE_INFO, COMMUNICATION_FILE_INFO));

        final Event event = new Event(ARTICLE_ACTION, TICKET_ID, null, null, null, null);

        eventHandlingUseCase.handleEvent(event);

        // Then

        final ArgumentCaptor<List<DmsContentObjectRequest>> captor = ArgumentCaptor.forClass(List.class);

        verify(ticketingOutPort).getTicket(TICKET_ID);
        verify(dmsOutPort).addContentObjectsToIncoming(eq(INCOMING_TARGET_WITH_CONTENT_OBJECTS), captor.capture());
        verify(dmsOutPort, times(2)).updateContentObject(any(DmsTarget.class), any(DmsContentObjectRequest.class));

        final List<DmsContentObjectRequest> actual = captor.getValue();

        assertThat(actual)
                .extracting(DmsContentObjectRequest::name)
                .containsExactly(
                        attachment1.getId() + "_" + attachment1.getFilename(),
                        attachment2.getId() + "_" + attachment2.getFilename());
    }

    private TicketInternal createTicket(final String eakteCooTarget, final String eakteWiederaufsetzpunkt, final List<ArticleInternal> articles) {
        final TicketInternal ticket = new TicketInternal();
        ticket.setId(TICKET_ID);
        ticket.setEakteUserlogin(USERNAME);
        ticket.setEakteJoboe(JOBOE);
        ticket.setEakteCooTarget(eakteCooTarget);
        ticket.setEakteWiederaufsetzpunkt(eakteWiederaufsetzpunkt);
        ticket.setArticles(articles);
        ticket.setOwnerId(OWNER_ID);
        ticket.setCustomerId(CUSTOMER_ID);
        return ticket;
    }

    private ArticleInternal createArticle(final String articleId, final List<ArticleAttachment> attachments) {
        final ArticleInternal article = new ArticleInternal();
        article.setId(articleId);
        article.setTicketId(TICKET_ID);
        article.setBody("Text");
        article.setAttachments(attachments);
        return article;
    }

    private ArticleAttachment createAttachment(final String attachmentId, final String filename) {
        final ArticleAttachment attachment = new ArticleAttachment();
        attachment.setId(attachmentId);
        attachment.setFilename(filename);
        return attachment;
    }

}
