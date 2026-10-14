package de.muenchen.oss.dbs.ticketing.eventing.dms.adapter.out.web;

import de.muenchen.oss.dbs.ticketing.eai.client.model.ArticleInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.TicketInternal;
import de.muenchen.oss.dbs.ticketing.eai.client.model.User;
import de.muenchen.oss.dbs.ticketing.eventing.dms.application.port.out.HtmlGenerationOutPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public String generateCommunicationHtml(final TicketInternal ticket, final List<ArticleInternal> articles, final User customer, final User owner) {

        Context context = new Context();
        context.setVariables(generateDataMap(ticket, articles, customer, owner));

        return templateEngine.process(TEMPLATE_NAME, context);
    }

    private Map<String, Object> generateDataMap(final TicketInternal ticket, final List<ArticleInternal> articles, final User customer, final User owner) {
        Map<String, Object> data = new HashMap<>();
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
