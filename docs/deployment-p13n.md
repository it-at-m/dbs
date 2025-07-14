# dbs-p13n-service

| Property | Description | Default |
| -------- | ----------- | ------- |

# dbs-p13n-webcomponents

There are multiple webcomponents available which should be integrated to separate pages of your frontend.

1. Add import to your page `<script src="<host>/loader-<component-name>-webcomponent.js" type="module"></script>`
2. Add the element to your page with the appropriate configuration.

## `checklist-overview`

| Property               | Type   | Description                      | Default |
| ---------------------- | ------ | -------------------------------- | ------- |
| `checklist-detail-url` | string | URL to the checklist detail page | -       |

## `checklist-detail`

| Query-Parameter | Description                    |
| --------------- | ------------------------------ |
| `cl-id`         | ID of the checklist to display |

## `my-checklists`

| Property               | Type   | Description                      | Default |
| ---------------------- | ------ | -------------------------------- | ------- |
| `checklist-detail-url` | string | URL to the checklist detail page | -       |
| `new-checklist-url`    | string | URL to the new checklist page    | -       |
