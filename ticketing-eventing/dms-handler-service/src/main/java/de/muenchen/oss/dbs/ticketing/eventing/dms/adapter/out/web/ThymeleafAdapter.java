package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.web;

import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.History;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketHistory;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.HtmlGenerationOutPort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import java.util.Objects;
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

    private static final Map<String, String> OBJECT_NAMES = Map.of(
            "Ticket", "Ticket",
            "Ticket::Article", "Artikel",
            "Ticket::SharedDraftZoom", "Entwurf",
            "Checklist", "Checkliste",
            "Checklist::Item", "Checklisten-Element",
            "Mention", "Mention");

    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    @SuppressFBWarnings(
            value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Null check is performed explicitly before dereferencing"
    )
    public String generateHistoryHtml(final TicketHistory history) {
        try {
            if (history == null || history.getHistory() == null || history.getAssets() == null || history.getAssets().getUser() == null) {
                log.warn("history not complete. cannot generate html");
                return "";
            }

            // Load users into a map (id -> name)
            final Map<String, User> users = history.getAssets().getUser();

            // Import all events, filter by ‘notification,’ enrich them, and sort them chronologically
            final List<EnrichedItem> allItems = history.getHistory().stream()
                    .map(el -> new EnrichedItem(el, enrichItemDescription(el, users)))
                    .filter(item -> item.description() != null)
                    .sorted(Comparator.comparing(item -> Objects.toString(item.history().getCreatedAt(), "")))
                    .toList();

            if (allItems.isEmpty()) {
                final Context context = new Context();
                context.setVariable("historyGroups", Collections.emptyList());
                return templateEngine.process(HISTORY_TEMPLATE_NAME, context);
            }

            // Sequence formation based on the unique ID
            final List<HistoryGroupViewModel> groupedEntries = new ArrayList<>();

            final EnrichedItem firstItem = allItems.getFirst();
            String currentAuthorKey = getAuthorKey(firstItem.history());
            String currentAuthorName = resolveAuthorName(firstItem.history(), users);
            String firstTimestamp = truncateToMinute(Objects.toString(firstItem.history().getCreatedAt(), ""));
            List<EnrichedItem> currentGroupItems = new ArrayList<>();
            currentGroupItems.add(firstItem);

            for (int i = 1; i < allItems.size(); i++) {
                final EnrichedItem currentItem = allItems.get(i);
                final String authorKey = getAuthorKey(currentItem.history());

                if (authorKey.equals(currentAuthorKey)) {
                    currentGroupItems.add(currentItem);
                } else {
                    groupedEntries.add(new HistoryGroupViewModel(firstTimestamp, currentAuthorName, currentGroupItems));

                    currentAuthorKey = authorKey;
                    currentAuthorName = resolveAuthorName(currentItem.history(), users);
                    firstTimestamp = truncateToMinute(Objects.toString(currentItem.history().getCreatedAt(), ""));
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
    private String getAuthorKey(final History history) {
        final String sourceableType = Objects.toString(history.getSourceableType(), "");
        final String sourceableId = Objects.toString(history.getSourceableId(), "");
        if (!sourceableType.isEmpty() && !sourceableId.isEmpty()) {
            return sourceableType + ":" + sourceableId;
        }
        return "user:" + Objects.toString(history.getCreatedById(), "0");
    }

    /**
     * Returns the display name
     */
    private String resolveAuthorName(final History history, final Map<String, User> users) {

        final String sourceableType = Objects.toString(history.getSourceableType(), "");
        final String sourceableName = Objects.toString(history.getSourceableName(), "");
        if (TRIGGER.equals(sourceableType) && !sourceableName.isEmpty()) {
            return "Trigger: " + sourceableName;
        }
        if (!sourceableName.isEmpty()) {
            return sourceableName;
        }
        final String createdById = Objects.toString(history.getCreatedById(), "0");
        return users.get(createdById) != null ? (users.get(createdById).getFirstname() + " " + users.get(createdById).getLastname()).trim() : createdById;
    }

    private String enrichItemDescription(final History history, final Map<String, User> users) {
        final String type = Objects.toString(history.getType(), "");
        final String object = Objects.toString(history.getObject(), "");
        final String attribute = Objects.toString(history.getAttribute(), "");
        final String valueFrom = Objects.toString(history.getValueFrom(), "");
        final String valueTo = Objects.toString(history.getValueTo(), "");

        final String targetName = OBJECT_NAMES.getOrDefault(object, object);

        return switch (type) {
        case "notification" -> null;
        case "email" -> "E-Mail gesendet an '" + valueTo + "'";
        case "created" -> {
            if (MENTION.equals(object)) {
                final String mentionedUserName = resolveMentionUser(valueTo, users);
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
    private String resolveMentionUser(final String userId, final Map<String, User> userNames) {
        if (userNames.get(userId) != null) {
            final User user = userNames.get(userId);
            final String firstname = Objects.toString(user.getFirstname(), "");
            final String lastname = Objects.toString(user.getLastname(), "");
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

    private record EnrichedItem(History history, String description) {
    }

    private record HistoryGroupViewModel(
            String timestamp,
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

        data.put("customerLastName", customer.getLastname());
        data.put("customerFirstName", customer.getFirstname());
        data.put("customerEMail", customer.getEmail());
        data.put("customerLhmExtId", customer.getLhmextid());
        data.put("customerNote", customer.getNote());

        return data;
    }
}
