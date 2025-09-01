# ticketing-eai

EAI (enterprise application integration) for Zammad API with different endpoints for internal and external clients.

```mermaid
flowchart LR
    EAI -->|REST| Zammad
    ic[Internal Clients] -->|Internal REST API| EAI
    ec[External Clients] -->|External REST API| EAI
```

## components

- eai-service: TBD
- [api-client](./api-client): Spring Java Client for making request against EAI based on eai OpenAPI spec
