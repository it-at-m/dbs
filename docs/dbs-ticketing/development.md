# Local development of dbs-ticketing 🚧 WIP

## dbs-ticketing-eai

not yet published

## dbs-direct-pass-retrieval-service

1. `cd /direct-pass-retrieval-service`
1. run `apigateway/runLocalNoSecurity.sh`
1. `cd frontend && npm run dev`
1. Run `src/test/java/de/muenchen/dbs/ticketing/prs/util/ResetKeyGeneratorTest.java` to generate a valid link for testing the component locally

To test your setup go to <http://localhost:3000/?q=generated-key> .

## dbs-ticketing-eventing

For local testing:

- Run stack with `/ticketing-eventing/stack/docker-compose.yml`
- Run DbsTicketingEventingService
- Run DbsTicketingEventingMailHandlerService

Open **Event-Service** via Swagger (<http://localhost:8080/swagger-ui/index.html>):

- User: zammad
- Password: password
- Method: POST `/api/event`
- X-Zammad-Trigger: `T2805_Event_Nachricht_In_Postkorb`
- X-Zammad-Delivery: `myID`
- Request Body:

```json
{
  "ticket": "3702",
  "status": "closed",
  "status_id": "1",
  "anliegenart": "technischer Bürgersupport",
  "lhmextid": "33caabe6-317c-4c2d-8bf7-6c36230599db"
}
```

Open **Kafka** (<http://localhost:8089/):\>

- Inspect: `Topics --> dbs-ticketing-event --> Messages`
- Should see a new corresponding event

Open **Mailpit** (<http://localhost:8025/>):

- Should see a mail with the public articles of the ticket
