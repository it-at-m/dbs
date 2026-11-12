package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.web;

import static org.assertj.core.api.Assertions.assertThat;

import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleAttachment;
import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Slf4j
class ThymeleafAdapterTest {

    private ThymeleafAdapter thymeleafAdapter;

    @BeforeEach
    void setUp() {
        // Instantiate template engine setup for the unit test
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        thymeleafAdapter = new ThymeleafAdapter(templateEngine);
    }

    @Test
    void testGenerateHistoryHtml() throws IOException {

        final ClassPathResource resource = new ClassPathResource("history.json");
        final String historyJson = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        final String resultHtml = thymeleafAdapter.generateHistoryHtml(historyJson);

        assertThat(resultHtml).isNotNull();
        assertThat(resultHtml).isNotEmpty();

        assertThat(resultHtml).contains("Ticket-Verlauf");

        assertThat(resultHtml).contains("Alex Huber");

        assertThat(resultHtml).contains("2026-05-11 13:25");
    }

    @Test
    void testGenerateCommunicationHtml() {

        final ArticleInternal article1 = createArticle("1", List.of(createAttachment("attachment_1"), createAttachment("attachment_2")));
        final ArticleInternal article2 = createArticle("2", null);
        final ArticleInternal article3 = createArticle("3", List.of(createAttachment("attachment_3"), createAttachment("attachment_4")));

        final List<ArticleInternal> articles = List.of(article1, article2, article3);

        final TicketInternal ticket = new TicketInternal();
        ticket.setTitle("Test Title");
        ticket.setNumber("Test Number");
        ticket.setStateExternal("Test State");
        ticket.setPriorityId("Test Priority");
        ticket.setVertrauensniveau("Test Vertrauensniveau");
        ticket.setVertrauensniveauTicket("Test Vertrauensniveau Ticket");

        final User customer = new User();
        customer.setId("customer");
        customer.setFirstname("customer_firstname");
        customer.setLastname("customer_lastname");
        customer.setEmail("customer_mail");
        customer.setLhmextid("customer_lhmExtId");
        customer.setNote("customer_note");

        final User owner = new User();
        owner.setFirstname("owner_firstname");
        owner.setLastname("owner_lastname");

        final String resultHtml = thymeleafAdapter.generateCommunicationHtml(ticket, articles, customer, owner);
        log.info(resultHtml);

        assertThat(resultHtml).isNotNull();
        assertThat(resultHtml).isNotEmpty();

        assertThat(resultHtml).contains(ticket.getTitle());

        assertThat(resultHtml).contains("BETREFF:");

        assertThat(resultHtml).doesNotContain("AN:");

        assertThat(resultHtml).contains(owner.getFirstname() + ' ' + owner.getLastname());
    }

    private ArticleInternal createArticle(final String articleId, final List<ArticleAttachment> attachments) {
        final ArticleInternal article = new ArticleInternal();
        article.setId(articleId);
        article.setBody("Text" + articleId);
        article.setFrom("From" + articleId);
        article.setCreatedAt(OffsetDateTime.now());
        article.setSubject("Subject" + articleId);
        article.setType(ArticleInternal.TypeEnum.WEB);
        article.setAttachments(attachments);
        return article;
    }

    private ArticleAttachment createAttachment(final String attachmentId) {
        final ArticleAttachment attachment = new ArticleAttachment();
        attachment.setId(attachmentId);
        attachment.setFilename(attachmentId);
        return attachment;
    }
}
