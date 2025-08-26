package de.muenchen.oss.dbs.ticketing.eventing.service;

import static de.muenchen.oss.dbs.ticketing.eventing.service.TestConstants.SPRING_TEST_PROFILE;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest.EventDTO;
import de.muenchen.oss.dbs.ticketing.eventing.service.adapter.in.rest.EventMapperImpl;
import de.muenchen.oss.dbs.ticketing.eventing.service.domain.model.Event;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = DbsTicketingEventingService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { EventMapperImpl.class, ObjectMapper.class })
@ActiveProfiles(SPRING_TEST_PROFILE)
@EmbeddedKafka(partitions = 1, topics = { "event-out" })
@ExtendWith(MockitoExtension.class)
class E2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testEventUnauthorized() {
        final ResponseEntity<Void> response = restTemplate.postForEntity("/api/event", Map.of(), Void.class);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }

    @Test
    void testEventSuccessful() throws Exception {
        // setup Kafka consumer
        final Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        final DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        @SuppressWarnings("PMD.CloseResource")
        final Consumer<String, String> consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromEmbeddedTopics(consumer, "event-out");

        // setup call
        final EventDTO eventDTO = new EventDTO("123", "open", "1", "test-anliegen", "test-lhm-1");
        final String basicAuth = "Basic %s".formatted(Base64.getEncoder().encodeToString("test-user:test-password".getBytes(StandardCharsets.UTF_8)));
        @SuppressWarnings("PMD.LooseCoupling")
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", basicAuth);
        headers.set("X-Zammad-Trigger", "test-trigger");
        headers.set("X-Zammad-Delivery", "test-delivery-id");
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(eventDTO), headers);
        // call
        final ResponseEntity<Void> response = restTemplate.postForEntity("/api/event", entity, Void.class);

        // test
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        // await and verify the event sent to Kafka
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            final ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "event-out");
            final Event capturedEvent = objectMapper.readValue(singleRecord.value(), Event.class);
            // test
            assertEquals("test-action", capturedEvent.action());
            assertEquals("123", capturedEvent.ticket());
            assertEquals("open", capturedEvent.status());
            assertEquals("1", capturedEvent.statusId());
            assertEquals("test-anliegen", capturedEvent.anliegenart());
            assertEquals("test-lhm-1", capturedEvent.lhmExtId());
        });
    }
}
