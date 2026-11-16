# Lokal testen

- In C:\code\MPdZ\dbs\ticketing-eventing\stack hochfahren per `podman compose up`
- eventing-service hochfahren
- dms-handler-service hochfahren
- Swagger öffnen: http://localhost:8080/swagger-ui/index.html#/rest-adapter/event
- Einloggen: zammad / password

Ausführen

```
POST /api/event

X-Zammad-Trigger: T2804_Event_Neuer_InternerArtikel
X-Zammad-Delivery: myID

{
  "ticket": "719",
  "status": "new",
  "status_id": "1",
  "anliegenart": "Einkommensorientierte Zusatzförderung",
  "lhmextid": ""
}
```