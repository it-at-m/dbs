# ticketing-eventing

Event notification via Zammad webhooks and Apache Kafka

```mermaid
flowchart LR
    z[Zammad] -->|REST Webhook| e[eventing-service] --> k([Apache Kafka])
    k --> c1[other Service]
    k --> c2[other Service]
```

## Components

- [eventing-service]: Takes Zammad webhook event via REST and forwards it to Apache Kafka
