# Local testing

- Startup `dbs/ticketing-eventing/stack` with `podman compose up`
- Startup eventing-service
- Startup mail-handler-service
- Open Swagger: http://localhost:8080/swagger-ui/index.html#/rest-adapter/event
- Login with: `zammad / password`

Execute

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
Important:
- Use supported `anliegenart` (see`relevant-ticket-types` in `application-local.yml`)
- Have `lhmextid` set (also in ticket)