# dbs-ticketing

🚧 WIP

## ticketing-eai

EAI (enterprise application integration) for Zammad API with different endpoints for internal and external clients.

```mermaid
flowchart LR
    EAI -->|REST| Zammad
    ic[Internal Clients] -->|Internal REST API| EAI
    ec[External Clients] -->|External REST API| EAI
```

### components

- eai-service: TBD
- [api-client](./api-client): Spring Java Client for making request against EAI based on eai OpenAPI spec

## ticketing-eventing

Event notification via Zammad webhooks and Apache Kafka

```mermaid
flowchart LR
    z[Zammad] -->|REST Webhook| e[eventing-service] --> k([Apache Kafka])
    k --> c1[other Service]
    k --> c2[other Service]
```

### Components

- [eventing-service]: Takes Zammad webhook event via REST and forwards it to Apache Kafka
