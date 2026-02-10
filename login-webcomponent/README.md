# DBS-Login Webcomponent

Shows a Login-Button and/or tries to log in a user visiting a site with this webcomponent attached.

## Usage

1. Add Import to page:

| Environment | Import                                                                                                                       |
| ----------- | ---------------------------------------------------------------------------------------------------------------------------- |
| Integration | `<script src="https://dbs-login-webcomponent-integration-dbs-login.apps.capk.muenchen.de/loader.js" type="module"></script>` |
| Test        | `<script src="https://dbs-login-webcomponent-test.muenchen.de/loader.js" type="module"></script>`                            |
| Prod        | `<script src="https://dbs-login-webcomponent.muenchen.de/loader.js" type="module"></script>`                                 |

2. Add Element to page with appropriate [config](#fragment-properties)

```html
<dbs-login
  kc-url="http://url"
  kc-realm="realm"
  kc-client-id="client-id"
></dbs-login>
```

## URL Parameters

This webcomponent checks the URL for query parameters to control its behaviour and/or to get data passed to it.

### `?lg-idphint`

Advice the login-button to use a specific login provider instantly instead of giving the user a choice.

Currently the following ID-Providers are available:

| IDP                             | Parameter-Value           |
| ------------------------------- | ------------------------- |
| Bayern-ID                       | `?lg-idphint=BayernID`    |
| Elster Unternehmenskonto (NEZO) | `?lg-idphint=ELSTER_NEZO` |

### `?lg-forcelogin=true`

This lets the login button appear in a modal window so the user is forced to log in before continuing to your site.

## Webcomponent Properties

When adding this custom element to your website you can configure its behaviour by changing these properties
accordingly.

### `kc-url`

|          |           |
| -------- | --------- |
| Required | true      |
| Type     | String    |
| Default  | undefined |

Base-URL of your keylcloak-instance. E.g. `https://my-sso.muenchen.de/auth`

### `kc-realm`

|          |           |
| -------- | --------- |
| Required | true      |
| Type     | String    |
| Default  | undefined |

Keycloak-Realm to use for authentication.

### `kc-client-id`

|          |           |
| -------- | --------- |
| Required | true      |
| Type     | String    |
| Default  | undefined |

ID of your client in the given realm on the given keycloak instance.

### `cookie-domain`

|          |           |
| -------- | --------- |
| Required | false     |
| Type     | String    |
| Default  | undefined |

The domain to set for the cookies containing the users token. Further
info: https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie#write_a_new_cookie

### `overview-link`

|          |           |
| -------- | --------- |
| Required | false     |
| Type     | String    |
| Default  | undefined |

Link to the overview page.

### `ticket-link`

|          |           |
| -------- | --------- |
| Required | false     |
| Type     | String    |
| Default  | undefined |

Link to the overview page of all tickets of a user.

### `appointment-link`

|          |           |
| -------- | --------- |
| Required | false     |
| Type     | String    |
| Default  | undefined |

Link to the overview page of all appointments of a user.

### `checklist-link`

|          |           |
| -------- | --------- |
| Required | false     |
| Type     | String    |
| Default  | undefined |

Link to the overview page of all checklists of a user.

## Events

The Login-Webcomponent currently exposes only one event

### OUT: `authorization-event`

The event contains a `detail`-Property which is either an object containing
the following infos about the current user, or is `undefined` when the user logged out.

```typescript
{
  buergerName: string;
  buergerMail: string;
  loginProvider: string;
  trustLevel: "level1" | "level3" | "level4";
  accessToken: string;
}
```

It gets sent when

1. The auth-state of the user changes
2. Every 5 seconds (if a webcomponent didn't receive one of the first events)

### IN: `authorization-request`

This event can be sent by a webcomponent requiring the user to log in. Further more it allows to define
with which provider and authorization level the user should login. Both these options are optional though and can be left out.

The event details are structured as follows:

```typescript
{
  loginProvider: "buergerkonto" | "nezo" | "bundid" | undefined;
  authLevel: "level1" | "level3" | "level4" | undefined;
}
```

It can be used like this:

```js
document.dispatchEvent(
  new CustomEvent("authorization-request", {
    detail: {
      loginProvider: "bundid",
      authLevel: "level3",
    },
  })
);
```

## Dev-Setup

1. Checkout
1. run `npm install && npm run dev`
1. Open localhost:5761 or...
1. ...Integrate into your own dev-site alongside your webcomponent:

```html
<!-- Load login-webcomponent from local dev-server -->
<script
  src="http://127.0.0.1:5173/src/dbs-login-webcomponent.ts"
  type="module"
></script>

<dbs-login></dbs-login>
```
