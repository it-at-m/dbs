<template>
  <div>
    <h2 style="padding-bottom: 32px">
      {{ t("preview.suggestedServices") }} ({{ snServices.length }})
    </h2>
    <ul class="snServiceList">
      <li
          class="snServiceElement mde-b2 mde-bold"
          v-for="service in snServices"
          :key="service.serviceID"
          @click="emit('openService', service)"
          tabindex="0"
          @keydown.enter="emit('openService', service)"
          :aria-label="
                  service.required
                    ? service.title + ' – ' + t('preview.mandatory')
                    : service.title
                "
      >
        {{ service.title }}
        <span
            class="required-label mde-b2"
            v-if="service.required"
        >– {{ t("preview.mandatory") }}</span
        >

        <div class="print-only mde-b1">
          <p>
            {{ service.note }}
            <br />
            <strong> {{ t("preview.learnMore") }}: </strong>
            {{
              service.isExternal
                  ? service.publicUrl
                  : getShortLink(service.publicUrl!)
            }}
          </p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import type {ChecklistItemServiceNavigatorDTO} from "@/api/dbs-clients/generated-p13n-service-api";

defineProps<{
  snServices: ChecklistItemServiceNavigatorDTO[];
  t: (key: string) => string;
}>();

const emit = defineEmits<{
  openService: [service: ChecklistItemServiceNavigatorDTO]
}>()

function getShortLink(serviceUrl: string) {
  try {
    const urlObj = new URL(serviceUrl);
    const pathSegments = urlObj.pathname
        .split("/")
        .filter((segment) => segment !== "");

    if (pathSegments.length < 2) {
      // Not enough segments to remove the second last part
      return serviceUrl;
    }

    // Remove the second last segment
    pathSegments.splice(pathSegments.length - 2, 1);

    // Reconstruct the pathname
    urlObj.pathname = "/" + pathSegments.join("/");

    return urlObj.toString();
  } catch {
    // If the URL is invalid, return it as is
    return serviceUrl;
  }
}
</script>

<style scoped>

.snServiceList {
  list-style-type: none;
  padding-left: 0;
  margin: 0;
}

.snServiceElement {
  cursor: pointer;
  padding: 16px 0;
  border-top: 1px solid var(--mde-color-neutral-beau-blue-x-light);
  color: var(--mde-color-brand-mde-blue);
}

.snServiceElement:hover,
.snServiceElement:focus {
  text-decoration: underline;
}

.snServiceElement .required-label {
  color: var(--mde-color-neutral-grey);
}

.snServiceElement:hover .required-label,
.snServiceElement:focus .required-label {
  color: var(--mde-color-neutral-grey-light);
}

.print-only {
  color: unset;
  text-decoration: none;
  padding: 12px;

  display: none;
}


@media print {
  .print-only {
    display: block;
    font-size: 14px;
  }

  .snServiceElement {
    border-top: 1px solid var(--mde-color-neutral-grey);
  }

  /* Ensure the list allows page breaks inside it */
  ul {
    break-inside: auto !important;
    page-break-inside: auto !important; /* For legacy browser compatibility */
  }

  /* Allow individual list items to split across pages */
  li {
    break-inside: avoid;
    page-break-inside: avoid;
  }
}
</style>