# Lokal testen

- In C:\code\MPdZ\dbs\ticketing-eventing\stack hochfahren per `podman compose up`
- eventing-service hochfahren
- mail-handler-service hochfahren
- Swagger öffnen: http://localhost:8080/swagger-ui/index.html#/rest-adapter/event
- Einloggen: zammad / password

Ausführen

```
POST /api/event

X-Zammad-Trigger: T2805_Event_Nachricht_In_Postkorb
X-Zammad-Delivery: myID

{
  "ticket": "220",
  "status": "open",
  "status_id": "1",
  "anliegenart": "technischer Bürgersupport",
  "lhmextid": "e4cc62ef-1562-4de2-9619-f2b93ba6610f"
}
```
Wichtig:
- Anliegenart, die supported wird (siehe `relevant-ticket-types` in `application-local.yml`)
- `lhmextid` gesetzt (auch im Ticket)