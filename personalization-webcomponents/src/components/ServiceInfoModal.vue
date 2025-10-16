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
        class="mandatory-subtitle"
      >
        – verpflichtend
      </span>
    </template>

    <template #body>
      {{ service?.note }}
    </template>

    <template #buttons>
      <a
        v-for="onlineService in service?.onlineServices"
        :key="onlineService.uri"
        :href="onlineService.uri"
        target="_blank"
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
      >
        <muc-button icon="calendar"> Termin vereinbaren </muc-button>
      </a>

      <a
        v-if="service?.publicUrl"
        :href="service.publicUrl"
        target="_blank"
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
      <!--
      todo add feature to delete checklist item
      <muc-button
        variant="ghost"
        icon="trash"
        @click="emit('task-delete')"
      >
        Aufgabe löschen
      </muc-button>
      -->
      <muc-button
        variant="ghost"
        icon="check"
        @click="emit('task-done')"
      >
        Aufgabe erledigen
      </muc-button>
    </template>
  </muc-modal>
</template>

<script setup lang="ts">
import type ChecklistItemServiceNavigator from "@/api/persservice/ChecklistItemServiceNavigator.ts";

import { MucButton, MucModal } from "@muenchen/muc-patternlab-vue";

const { open = false, showActions = false } = defineProps<{
  open?: boolean;
  service: ChecklistItemServiceNavigator;
  showActions?: boolean;
}>();

const emit = defineEmits(["close", "cancel", "task-delete", "task-done"]);
</script>
