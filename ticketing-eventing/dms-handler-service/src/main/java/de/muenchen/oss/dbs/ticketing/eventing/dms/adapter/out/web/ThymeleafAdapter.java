package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.HtmlGenerationOutPort;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ThymeleafAdapter implements HtmlGenerationOutPort {

    private final SpringTemplateEngine templateEngine;

    private static final String TEMPLATE_NAME = "communication-html-template";
    private static final String HISTORY_TEMPLATE_NAME = "history-html-template";

    private static final String TRIGGER = "Trigger";
    private static final String MENTION = "Mention";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, String> OBJECT_NAMES = Map.of(
            "Ticket", "Ticket",
            "Ticket::Article", "Artikel",
            "Ticket::SharedDraftZoom", "Entwurf",
            "Checklist", "Checkliste",
            "Checklist::Item", "Checklisten-Element",
            "Mention", "Mention");

    @Override
    public String generateHistoryHtml(final String historyJson) {
        try {
            final JsonNode rootNode = objectMapper.readTree(historyJson);
            final JsonNode historyArray = rootNode.get("history");
            final JsonNode usersNode = rootNode.path("assets").path("User");

            // Load users into a map (id -> name)
            final Map<String, String> userNames = new HashMap<>();
            if (usersNode.isObject()) {
                usersNode.properties().forEach(entry -> {
                    final String userId = entry.getKey();
                    final JsonNode user = entry.getValue();
                    final String firstname = user.path("firstname").asText("");
                    final String lastname = user.path("lastname").asText("");
                    final String fullName = (firstname + " " + lastname).trim();
                    userNames.put(userId, fullName.isEmpty() ? "Unbekannt" : fullName);
                });
            }

            // Import all events, filter by ‘notification,’ enrich them, and sort them chronologically
            final List<EnrichedItem> allItems = StreamSupport.stream(historyArray.spliterator(), false)
                    .map(node -> new EnrichedItem(node, enrichItemDescription(node, usersNode)))
                    .filter(item -> item.description() != null)
                    .sorted(Comparator.comparing(item -> item.node().path("created_at").asText("")))
                    .toList();

            if (allItems.isEmpty()) {
                final Context context = new Context();
                context.setVariable("historyGroups", Collections.emptyList());
                return templateEngine.process(HISTORY_TEMPLATE_NAME, context);
            }

            // Sequence formation based on the unique ID
            final List<HistoryGroupViewModel> groupedEntries = new ArrayList<>();

            final EnrichedItem firstItem = allItems.getFirst();
            String currentAuthorKey = getAuthorKey(firstItem.node());
            String currentAuthorName = resolveAuthorName(firstItem.node(), userNames);
            String firstTimestamp = truncateToMinute(firstItem.node().path("created_at").asText(""));
            List<EnrichedItem> currentGroupItems = new ArrayList<>();
            currentGroupItems.add(firstItem);

            for (int i = 1; i < allItems.size(); i++) {
                final EnrichedItem currentItem = allItems.get(i);
                final String authorKey = getAuthorKey(currentItem.node());

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

            final Context context = new Context();
            context.setVariable("historyGroups", groupedEntries);

            return templateEngine.process(HISTORY_TEMPLATE_NAME, context);

        } catch (Exception e) {
            throw new RuntimeException("Error generating the history HTML", e);
        }
    }

    /**
     * Generates an ID for the author
     */
    private String getAuthorKey(final JsonNode node) {
        final String sourceableType = node.path("sourceable_type").asText("");
        final String sourceableId = node.path("sourceable_id").asText("");
        if (!sourceableType.isEmpty() && !sourceableId.isEmpty()) {
            return sourceableType + ":" + sourceableId;
        }
        return "user:" + node.path("created_by_id").asText("0");
    }

    /**
     * Returns the display name
     */
    private String resolveAuthorName(final JsonNode node, final Map<String, String> userNames) {

        final String sourceableType = node.path("sourceable_type").asText("");
        final String sourceableName = node.path("sourceable_name").asText("");
        if (TRIGGER.equals(sourceableType) && !sourceableName.isEmpty()) {
            return "Trigger: " + sourceableName;
        }
        if (!sourceableName.isEmpty()) {
            return sourceableName;
        }
        final String createdById = node.path("created_by_id").asText("0");
        return userNames.getOrDefault(createdById, createdById);
    }

    private String enrichItemDescription(final JsonNode node, final JsonNode usersNode) {
        final String type = node.path("type").asText("");
        final String object = node.path("object").asText("");
        final String attribute = node.path("attribute").asText("");
        final String valueFrom = node.path("value_from").asText("");
        final String valueTo = node.path("value_to").asText("");

        final String targetName = OBJECT_NAMES.getOrDefault(object, object);

        return switch (type) {
        case "notification" -> null;
        case "email" -> "E-Mail gesendet an '" + valueTo + "'";
        case "created" -> {
            if (MENTION.equals(object)) {
                final String mentionedUserName = resolveMentionUser(valueTo, usersNode);
                yield "erstellt Mention  → '" + mentionedUserName + "'";
            }
            yield "erstellt " + targetName;
        }
        case "removed" -> "entfernt " + targetName;
        case "updated" -> "aktualisiert " + getUpdateDescription(attribute, valueFrom, valueTo, targetName);
        case "added" -> {
            if (!attribute.isEmpty()) {
                yield "added " + object + " " + attribute + " '" + valueTo + "'";
            }
            yield "added " + object + " '" + valueTo + "'";
        }
        default -> type + " (" + object + ")";
        };
    }

    private String getUpdateDescription(
            final String attribute,
            final String valueFrom,
            final String valueTo,
            final String targetName) {

        if (!attribute.isEmpty()) {
            if (!valueFrom.isEmpty() && !valueTo.isEmpty()) {
                return targetName + " " + attribute + " von '" + valueFrom + "' → '" + valueTo + "'";
            }
            if (!valueTo.isEmpty()) {
                return targetName + " " + attribute + " '" + valueTo + "'";
            }
            return targetName + " " + attribute;
        }
        return targetName;
    }

    @SuppressWarnings("PMD.LiteralsFirstInComparisons")
    private String resolveMentionUser(final String userId, final JsonNode usersNode) {
        if (usersNode != null && usersNode.has(userId)) {
            final JsonNode user = usersNode.get(userId);
            final String firstname = user.path("firstname").asText("");
            final String lastname = user.path("lastname").asText("");
            final String fullName = (firstname + " " + lastname).trim();
            if (!fullName.isEmpty() && !fullName.equals("-")) {
                return fullName;
            }
        }
        return userId;
    }

    private String truncateToMinute(final String isoTimestamp) {
        try {
            if (isoTimestamp == null || isoTimestamp.isEmpty()) {
                return "";
            }
            final OffsetDateTime odt = OffsetDateTime.parse(isoTimestamp);
            final ZonedDateTime localTime = odt.atZoneSameInstant(ZoneId.of("Europe/Berlin"));
            return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            return isoTimestamp;
        }
    }

    private record EnrichedItem(JsonNode node, String description) {
    }

    private record HistoryGroupViewModel(
            String minute,
            String authorName,
            List<EnrichedItem> items) {
    }

    @Override
    public String generateCommunicationHtml(final TicketInternal ticket, final List<ArticleInternal> articles, final User customer, final User owner) {
        try {
            final Context context = new Context();
            context.setVariables(generateDataMap(ticket, articles, customer, owner));

            return templateEngine.process(TEMPLATE_NAME, context);
        } catch (Exception e) {
            throw new RuntimeException("Error generating the communication HTML", e);
        }
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
