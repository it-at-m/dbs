# Deployment dbs-p13n

## dbs-p13n-service

| Property | Description | Default |
| -------- | ----------- | ------- |

## dbs-p13n-webcomponents

There are multiple webcomponents available which should be integrated to separate pages of your frontend.

1. Add import to your page `<script src="<host>/loader-<component-name>-webcomponent.js" type="module"></script>`
2. Add the element to your page with the appropriate configuration.

### `checklist-preview`

Shows a "preview" of all services in the calculated checklist. Grabs it's data from localstorage.

| Property               | Type   | Description                                        | Default |
| ---------------------- | ------ | -------------------------------------------------- | ------- |
| `checklist-detail-url` | string | URL to the checklist detail page                   | -       |
| `new-checklist-url`    | string | URL to the page where the "Lebenslagen" get chosen | -       |

### `checklist-overview`

Shows first (max 2) checklists of the current user to be displayed on an overview-page.

| Property                     | Type   | Description                                                                                                                                                                                        | Default |
| ---------------------------- | ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------- |
| `checklist-detail-url`       | string | URL to the checklist detail page                                                                                                                                                                   | -       |
| `checklist-overview-url`     | string | URL to the checklist overview page                                                                                                                                                                 | -       |
| `new-checklist-url`          | string | URL to the page where the "Lebenslagen" get chosen                                                                                                                                                 | -       |
| `displayed-on-detail-screen` | string | Wether this component is used on the cheklist-detial page. <br/> <br/>If true it will hide the "current" checklist identified by the same query-parameter as the `checklist-detail`-Queryparameter | false   |

### `checklist-detail`

Shows detailed data of a specific checklist.

| Property                 | Type   | Description                        | Default |
| ------------------------ | ------ | ---------------------------------- | ------- |
| `my-checklists-url`      | string | URL to the my checklist page       | -       |

| Query-Parameter | Description                    |
| --------------- | ------------------------------ |
| `cl-id`         | ID of the checklist to display |

### `my-checklists`

| Property               | Type   | Description                      | Default |
| ---------------------- | ------ | -------------------------------- | ------- |
| `checklist-detail-url` | string | URL to the checklist detail page | -       |
| `new-checklist-url`    | string | URL to the new checklist page    | -       |
