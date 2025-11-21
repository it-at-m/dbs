# Introduction

The DBS (Digitaler Bürgerservice / Digital Citizen Service) is a digital service provided by the government of the city of munich.

It currently consists of these modules:

- DBS-Ticketing with [Zammad](https://opensource.muenchen.de/de/software/zammad.html)
- DBS-P13N
- DBS-Login
- Terminvereinbarung (<https://github.com/it-at-m/eappointment>)

## Architecture

[![system overview Digitaler Bürgerservice DBS](./assets/systemueberblick_dbs.drawio.png)](https://github.com/it-at-m/dbs/blob/main/docs/assets/systemueberblick_dbs.drawio.png)

Citizens submit requests to the form server (e.g [Meldung von Insektennestern](https://service.muenchen.de/intelliform/forms/01/09/09/meldung_insektennest/index)), which sends the request to Zammad via the `dbs-ticketing-webcomponent` and the [dbs-ticketing-eai](https://github.com/it-at-m/dbs/tree/main/ticketing-eai).
Zammad serves as the backend of the DBS, where the clerks process the requests and communicate with the citizen via our own frontend `dbs-ticketing-webcomponents`.
This is integrated with [Web Components](https://developer.mozilla.org/en-US/docs/Web/API/Web_components) into the official site of the City of Munich.

### Authentication

The authentication of applicants and administrators takes place via a [Keycloak](https://opensource.muenchen.de/de/software/keycloak.html) and not via Zammad.
Keycloak uses two realms for this purpose:

* the realm `public` in which citizens are managed and, depending on the level of trust
* and the realm `muenchen.de` to manage the city of Munich Employees are automatically created with their associated group structures (department, team, etc) with the [zammad-ldap-sync](https://github.com/it-at-m/zammad-ldap-sync), changed or removed if necessary. This ensures that only authorized persons have access to the pending requests from citizens.


### Data protection

As DBS also processes highly sensitive personal data, it is important that we ensure data protection.
DBS does not send any information to applicants by e-mail, but only posts it in its frontend `dbs-ticketing-webcomponents`


### Reporting

As the [Zammad Reports](https://admin-docs.zammad.org/en/latest/manage/report-profiles.html) did not meet our reporting requirements, complex reports are created and made available via [Metabase](https://opensource.muenchen.de/software/metabase.html).


