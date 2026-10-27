package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ThymeleafAdapterTest {

    private ThymeleafAdapter thymeleafAdapter;

    @BeforeEach
    void setUp() {
        // Echtes Template-Engine-Setup für den Unit-Test instanziieren
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        // Adapter direkt mit der Engine erzeugen (ersetzt Lombok @RequiredArgsConstructor im Test)
        thymeleafAdapter = new ThymeleafAdapter(templateEngine);
    }

    @Test
    void testGenerateHistoryHtml() throws Exception {
        // 1. history.json aus src/test/resources/ einlesen
        ClassPathResource resource = new ClassPathResource("history.json");
        String historyJson = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // 2. Methode aufrufen
        String resultHtml = thymeleafAdapter.generateHistoryHtml(historyJson);
        log.info(resultHtml);

        // 3. Validierungen
        assertThat(resultHtml).isNotNull();
        assertThat(resultHtml).isNotEmpty();

        // Prüfen, ob das HTML Grundstrukturen enthält
        assertThat(resultHtml).contains("Ticket-Verlauf");

        // Prüfen, ob die Assets korrekt aufgelöst und gemappt wurden (Name aus User-Assets)
        assertThat(resultHtml).contains("Roland Werner");

        // Prüfen, ob die zeitliche Gruppierung auf die Minute funktioniert hat
        assertThat(resultHtml).contains("2026-05-11 13:25");
    }
}