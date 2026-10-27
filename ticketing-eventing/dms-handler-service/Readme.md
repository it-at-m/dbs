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

Was ich bisher geändert habe:
- ZammadAdapter gelöscht und die Methoden in mail-handler-service geschoben 
  (und auf die Snapshot-Version in der pom.xml umgestellt)
- ZammadApiException gelöscht, und stattdessen die in handler-core verwendet
- alle Article-Events im Eventing-Service als Mapping hinterlegt
- in application-local.yml
   - alle Actions hinterlegt als relevantActions und ArticleActions
   - eakte baseUrl, username, password ergänzt
   - security.oauth2... ergänzt 
- im communication-html-template.html ein paar Einträge korrigiert, z.B. 
   - createdDate statt created_date
   - article.body auf utext gestellt, damit das html richtig angezeigt wird
- Mittels KI eine HTML-Repräsentation von Änderungshistorie erstellt 

Aktuelle Beobachtung:
- Änderungshistorie und html werden jedes Mal upgedatet, auch wenn sich gar nichts geändert hat? Macht das Sinn?
- Die Artikel in der HTML-Repräsentation sollten die Artikel-ID sichtbar haben und die Attachments sollten diese ID als Präfix führen (vgl. Spec).