<template>
  <muc-modal
    :open="!!open"
    @close="emit('close')"
    @cancel="emit('cancel')"
  >
    <template #title>
      {{ service?.title }}
      <span
        v-if="service?.required"
        class="required-label"
      >
        – verpflichtend
      </span>
    </template>

    <template #body>
      <p style="margin-bottom: 0">
        {{ service?.note }}
      </p>
    </template>

    <template #buttons>
      <a
        v-for="onlineService in service?.onlineServices"
        :key="onlineService.uri"
        :href="onlineService.uri"
        target="_blank"
        tabindex="-1"
      >
        <muc-button
          icon="arrow-right"
          icon-animated
        >
          {{ onlineService.label }}
        </muc-button>
      </a>

      <a
        v-if="service?.appointmentService"
        :href="service.appointmentServiceUrl"
        target="_blank"
        tabindex="-1"
      >
        <muc-button icon="calendar"> Termin vereinbaren </muc-button>
      </a>

      <a
        v-if="service?.publicUrl"
        :href="service.publicUrl"
        target="_blank"
        tabindex="-1"
      >
        <muc-button
          icon="arrow-right"
          icon-animated
          variant="secondary"
        >
          Mehr erfahren
        </muc-button>
      </a>
    </template>

    <template
      v-if="showActions"
      #actions
    >
      <muc-button
        variant="ghost"
        icon="trash"
        @click="emit('task-delete')"
      >
        {{ isMobile ? "Löschen" : "Aufgabe löschen" }}
      </muc-button>
      <muc-button
        variant="ghost"
        :icon="service.checked ? 'circle-outline' : 'check'"
        @click="emit('task-toggle')"
      >
        {{
          service.checked
            ? isMobile
              ? "Aktivieren"
              : "Aufgabe aktivieren"
            : isMobile
              ? "Erledigen"
              : "Aufgabe erledigen"
        }}
      </muc-button>
    </template>
  </muc-modal>
</template>

<script setup lang="ts">
import type ChecklistItemServiceNavigator from "@/api/persservice/ChecklistItemServiceNavigator.ts";

import { MucButton, MucModal } from "@muenchen/muc-patternlab-vue";
import { useMediaQuery } from "@vueuse/core";

import { IS_MOBILE_MEDIA_QUERY } from "@/util/Constants.ts";

const { open = false, showActions = false } = defineProps<{
  open?: boolean;
  service: ChecklistItemServiceNavigator;
  showActions?: boolean;
}>();

const isMobile = useMediaQuery(IS_MOBILE_MEDIA_QUERY);

const emit = defineEmits(["close", "cancel", "task-delete", "task-toggle"]);
</script>

<style>
.required-label {
  color: var(--mde-color-neutral-grey-light);
  font-family: "Open Sans", sans-serif;
  font-size: 1.125rem;
  font-style: normal;
  font-weight: 400;
  line-height: 150%;
}

@media (max-width: 767px) {
  .required-label {
    font-size: 1rem;
  }
}
</style>
