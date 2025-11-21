<template>
  <muc-intro
    tagline="Checkliste"
    :title="checklist.title"
    :img="getChecklistIconBySituationId(checklist.situationId)"
    variant="detail"
  >
    <div
      v-if="checklist.lastUpdate"
      style="padding-bottom: 16px; padding-left: 3px"
    >
      <strong>Erstellungsdatum: </strong>
      {{ getDateInGermanDateFormat(new Date(checklist.lastUpdate)) }}
    </div>
    <div class="task-container">
      <div
        class="task"
        aria-hidden="true"
      >
        Aufgaben:
      </div>
      <div class="chips-container">
        <muc-chip
          v-if="todoCount"
          background-color="var(--checklist-color-status-open)"
        >
          {{ todoCount }} offen
          <svg
            style="margin-left: 8px; width: 20px; height: 20px"
            aria-hidden="true"
            class="m-button__icon"
          >
            <use href="#icon-pencil" />
          </svg>
        </muc-chip>
        <muc-chip
          v-if="doneCount"
          background-color="var(--checklist-color-status-closed)"
          role="status"
          aria-live="polite"
          aria-label="{{ doneCount }} erledigt"
        >
          {{ doneCount }} erledigt
          <svg
            style="margin-left: 8px; width: 20px; height: 20px"
            aria-hidden="true"
            class="m-button__icon"
          >
            <use href="#icon-check" />
          </svg>
        </muc-chip>
      </div>
    </div>
  </muc-intro>
</template>

<script setup lang="ts">
import type ChecklistServiceNavigator from "@/api/persservice/ChecklistServiceNavigator.ts";

import { MucIntro } from "@muenchen/muc-patternlab-vue";
import { computed, onMounted } from "vue";

import MucChip from "@/components/common/MucChip.vue";
import {
  getChecklistIconBySituationId,
  getDateInGermanDateFormat,
} from "@/util/Constants.ts";

const props = defineProps<{
  checklist: ChecklistServiceNavigator;
}>();

onMounted(() => {
  const element = document.querySelector(
    '[data-fragment-placeholder="breadcrumb-label"]'
  );
  if (element) {
    element.innerHTML = props.checklist.title;
  }

  // Don't show breadcrump "Checkliste"
  const removeChecklist = document.querySelector(
    ".m-breadcrumb__list-item-current"
  );
  if (removeChecklist) {
    removeChecklist.childNodes.forEach((node) => {
      if (node.nodeType === 3) {
        node.textContent = "";
      }
    });
  }

  document.title = props.checklist.title + " - Landeshauptstadt München";
});

const todoCount = computed(() => {
  if (props.checklist && props.checklist.checklistItemServiceNavigatorDtos) {
    return props.checklist.checklistItemServiceNavigatorDtos.filter(
      (value) => !value.checked
    ).length;
  } else {
    return undefined;
  }
});

const doneCount = computed(() => {
  if (props.checklist && props.checklist.checklistItemServiceNavigatorDtos) {
    return props.checklist.checklistItemServiceNavigatorDtos.filter(
      (value) => value.checked
    ).length;
  } else {
    return undefined;
  }
});
</script>
<style>
.m-intro-vertical__title {
  margin-bottom: 8px !important;
}

.task-container {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
}

.task {
  font-weight: bold;
  padding-right: 8px;
  padding-left: 3px;
  padding-top: 5px;
  white-space: nowrap;
}

.chips-container {
  display: flex;
  gap: 8px;
  align-items: flex-start;
  flex-wrap: wrap;
}
</style>
