package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.web;

import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.HtmlGenerationOutPort;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZonedDateTime;
import java.time.ZoneId;


@Service
@RequiredArgsConstructor
@Slf4j
public class ThymeleafAdapter implements HtmlGenerationOutPort {

    private final SpringTemplateEngine templateEngine;

    private static final String TEMPLATE_NAME = "communication-html-template";
    private static final String HISTORY_TEMPLATE_NAME = "history-html-template";

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Zentrale Mapping-Tabelle für die deutschen Objektbezeichnungen
    private static final Map<String, String> OBJECT_NAMES = Map.of(
            "Ticket", "Ticket",
            "Ticket::Article", "Artikel",
            "Ticket::SharedDraftZoom", "Entwurf",
            "Checklist", "Checkliste",
            "Checklist::Item", "Checklisten-Element",
            "Mention", "Mention"
    );

    @Override
    public String generateHistoryHtml(final String historyJson) {
        try {
            JsonNode rootNode = objectMapper.readTree(historyJson);
            JsonNode historyArray = rootNode.get("history");
            JsonNode usersNode = rootNode.path("assets").path("User");

            // 1. Benutzer in eine Map laden (ID -> Name)
            Map<String, String> userNames = new HashMap<>();
            if (usersNode.isObject()) {
                usersNode.properties().forEach(entry -> {
                    String userId = entry.getKey();
                    JsonNode user = entry.getValue();
                    String firstname = user.path("firstname").asText("");
                    String lastname = user.path("lastname").asText("");
                    String fullName = (firstname + " " + lastname).trim();
                    userNames.put(userId, fullName.isEmpty() ? "Unbekannt" : fullName);
                });
            }

            // 2. Alle Events einlesen, 'notification' filtern, anreichern und chronologisch sortieren
            List<EnrichedItem> allItems = StreamSupport.stream(historyArray.spliterator(), false)
                    .map(node -> new EnrichedItem(node, enrichItemDescription(node, usersNode)))
                    .filter(item -> item.description() != null) // Benachrichtigungen herausfiltern
                    .sorted(Comparator.comparing(item -> item.node().path("created_at").asText("")))
                    .toList();

            if (allItems.isEmpty()) {
                Context context = new Context();
                context.setVariable("historyGroups", Collections.emptyList());
                return templateEngine.process(HISTORY_TEMPLATE_NAME, context);
            }

            // 3. Sequenz-Bildung basierend auf der EINDEUTIGEN ID/QUELLE (nicht dem Anzeigenamen)
            List<HistoryGroupViewModel> groupedEntries = new ArrayList<>();

            EnrichedItem firstItem = allItems.getFirst();
            String currentAuthorKey = getAuthorKey(firstItem.node());
            String currentAuthorName = resolveAuthorName(firstItem.node(), userNames);
            String firstTimestamp = truncateToMinute(firstItem.node().path("created_at").asText(""));
            List<EnrichedItem> currentGroupItems = new ArrayList<>();
            currentGroupItems.add(firstItem);

            for (int i = 1; i < allItems.size(); i++) {
                EnrichedItem currentItem = allItems.get(i);
                String authorKey = getAuthorKey(currentItem.node());

                // Wenn dieselbe ID/Quelle direkt aufeinanderfolgt, zur aktuellen Gruppe hinzufügen
                if (authorKey.equals(currentAuthorKey)) {
                    currentGroupItems.add(currentItem);
                } else {
                    groupedEntries.add(new HistoryGroupViewModel(firstTimestamp, currentAuthorName, currentGroupItems));

                    currentAuthorKey = authorKey;
                    currentAuthorName = resolveAuthorName(currentItem.node(), userNames);
                    firstTimestamp = truncateToMinute(currentItem.node().path("created_at").asText(""));
                    currentGroupItems = new ArrayList<>();
                    currentGroupItems.add(currentItem);
                }
            }
            groupedEntries.add(new HistoryGroupViewModel(firstTimestamp, currentAuthorName, currentGroupItems));

            // 4. Thymeleaf Kontext befüllen
            Context context = new Context();
            context.setVariable("historyGroups", groupedEntries);

            return templateEngine.process(HISTORY_TEMPLATE_NAME, context);

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Generieren des History-HTMLs", e);
        }
    }

    /**
     * Erzeugt einen eindeutigen Schlüssel für die Gruppierung basierend auf der ID (Trigger-ID oder User-ID),
     * damit Personen mit gleichem Namen nicht fälschlicherweise zusammengefasst werden.
     */
    private String getAuthorKey(JsonNode node) {
        String sourceableType = node.path("sourceable_type").asText("");
        String sourceableId = node.path("sourceable_id").asText("");
        if (!sourceableType.isEmpty() && !sourceableId.isEmpty()) {
            return sourceableType + ":" + sourceableId;
        }
        return "user:" + node.path("created_by_id").asText("0");
    }

    /**
     * Ermittelt den menschenlesbaren Anzeigenamen für die Darstellung im HTML.
     */
    private String resolveAuthorName(JsonNode node, Map<String, String> userNames) {
        String sourceableType = node.path("sourceable_type").asText("");
        String sourceableName = node.path("sourceable_name").asText("");
        if ("Trigger".equals(sourceableType) && !sourceableName.isEmpty()) {
            return "Trigger: " + sourceableName;
        }
        if (!sourceableName.isEmpty()) {
            return sourceableName;
        }
        String createdById = node.path("created_by_id").asText("0");
        return userNames.getOrDefault(createdById, createdById);
    }

    private String enrichItemDescription(JsonNode node, JsonNode usersNode) {
        String type = node.path("type").asText("");
        String object = node.path("object").asText("");
        String attribute = node.path("attribute").asText("");
        String valueFrom = node.path("value_from").asText("");
        String valueTo = node.path("value_to").asText("");

        // 1. Spezifische Typen / Sonderfälle, die vom Standardmuster abweichen
        if ("notification".equals(type)) {
            return null; // Ignorieren
        }
        if ("email".equals(type)) {
            return "E-Mail gesendet an '" + valueTo + "'";
        }
        if ("Mention".equals(object) && "created".equals(type)) {
            String mentionedUserName = resolveMentionUser(valueTo, usersNode);
            return "erstellt Mention  → '" + mentionedUserName + "'";
        }

        // 2. Objektname auflösen (Fallback auf den technischen Objektnamen, falls neu)
        String targetName = OBJECT_NAMES.getOrDefault(object, object);

        // 3. Generische Ableitung nach Aktion (type)
        return switch (type) {
            case "created" -> "erstellt " + targetName;
            case "removed" -> "entfernt " + targetName;
            case "updated" -> {
                if (!attribute.isEmpty()) {
                    if (!valueFrom.isEmpty() && !valueTo.isEmpty()) {
                        yield "aktualisiert " + targetName + " " + attribute + " von '" + valueFrom + "' → '" + valueTo + "'";
                    } else if (!valueTo.isEmpty()) {
                        yield "aktualisiert " + targetName + " " + attribute + " '" + valueTo + "'";
                    }
                    yield "aktualisiert " + targetName + " " + attribute;
                }
                yield "aktualisiert " + targetName;
            }
            case "added" -> {
                if (!attribute.isEmpty()) {
                    yield "added " + object + " " + attribute + " '" + valueTo + "'";
                }
                yield "added " + object + " '" + valueTo + "'";
            }
            default -> type + " (" + object + ")";
        };
    }

    private String resolveMentionUser(String userId, JsonNode usersNode) {
        if (usersNode != null && usersNode.has(userId)) {
            JsonNode user = usersNode.get(userId);
            String firstname = user.path("firstname").asText("");
            String lastname = user.path("lastname").asText("");
            String fullName = (firstname + " " + lastname).trim();
            if (!fullName.isEmpty() && !"-".equals(fullName)) {
                return fullName;
            }
        }
        return userId;
    }

    private String truncateToMinute(String isoTimestamp) {
        try {
            if (isoTimestamp == null || isoTimestamp.isEmpty()) return "";
            OffsetDateTime odt = OffsetDateTime.parse(isoTimestamp);
            ZonedDateTime localTime = odt.atZoneSameInstant(ZoneId.of("Europe/Berlin"));
            return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            return isoTimestamp;
        }
    }

    public record EnrichedItem(JsonNode node, String description) {}

    public record HistoryGroupViewModel(
            String minute,
            String authorName,
            List<EnrichedItem> items
    ) {}

    @Override
    public String generateCommunicationHtml(final TicketInternal ticket, final List<ArticleInternal> articles, final User customer, final User owner) {

        final Context context = new Context();
        context.setVariables(generateDataMap(ticket, articles, customer, owner));

        return templateEngine.process(TEMPLATE_NAME, context);
    }

    private Map<String, Object> generateDataMap(final TicketInternal ticket, final List<ArticleInternal> articles, final User customer, final User owner) {
        final Map<String, Object> data = new HashMap<>();
        data.put("ticketTitle", ticket.getTitle());
        data.put("ticketNumber", ticket.getNumber());
        data.put("ticketState", ticket.getStateExternal());
        data.put("ticketPriority", ticket.getPriorityId());
        data.put("ticketVertrauensniveau", ticket.getVertrauensniveau());
        data.put("ticketVertrauensniveauTicket", ticket.getVertrauensniveauTicket());

        data.put("ticketInfo", articles.getFirst());
        data.put("articles", articles.subList(1, articles.size()));

        if (owner != null) {
            data.put("ticketOwner", owner.getFirstname() + ' ' + owner.getLastname());
        } else {
            data.put("ticketOwner", "-");
        }

        data.put("customerFamilyName", customer.getLastname());
        data.put("customerGivenName", customer.getFirstname());
        data.put("customerEMail", customer.getEmail());
        data.put("customerLhmExtId", customer.getLhmextid());
        data.put("customerNote", customer.getNote());

        return data;
    }
}
