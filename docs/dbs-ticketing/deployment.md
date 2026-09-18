# Deployment dbs-ticketing

🚧 WIP

## dbs-ticketing-eai

not yet published

## dbs-direc-pass-retrieval-service

Spec (internal): https://confluence.muenchen.de/pages/viewpage.action?pageId=431662952

### Usage

1. Add Import to page:

| Environment | Import                                                                                                                          |
|-------------|---------------------------------------------------------------------------------------------------------------------------------|
| Dev         | `<script src="https://password-reset-service-integration.muenchen.de/loader.js" type="module"></script>` |
| Test        | `<script src="https://password-reset-service-test.muenchen.de/loader.js" type="module"></script>`                              |
| Prod        | `<script src="https://password-reset-service.muenchen.de/loader.js" type="module"></script>`                                   |

2. Add Element to page

```html

<password-reset-element></password-reset-element>

```

### Webcomponent `password-reset-element`

Shows the password for a ticket if the given link is still valid.

Uses the API of the [dbs-ticketing-eai](https://github.com/it-at-m/dbs/tree/main/ticketing-eai).

#### Query Parameters

##### `q=<validationString>`

An encrypted piece of data that gets sent to the server for validation. If the string is valid, the webcomponent will display the password.
