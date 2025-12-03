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
    <div style="padding-top: 32px">
      <muc-button
        icon="trash"
        variant="secondary"
        @click="openAcceptDeleteDialog = true"
      >
        Checkliste löschen
      </muc-button>
    </div>
  </muc-intro>

  <muc-modal
    :open="openAcceptDeleteDialog"
    @close="openAcceptDeleteDialog = false"
    @cancel="openAcceptDeleteDialog = false"
  >
    <template #title> Löschen der Checkliste</template>

    <template #body>
      <muc-banner
        noIcon
        type="warning"
        variant="content"
      >
        <p>
          Mit dieser Aktion entfernen Sie die Checkliste
          <strong>„{{ checklist.title }}”</strong> und alle enthaltenen Aufgaben
          endgültig aus Ihrem Bereich.
        </p>
      </muc-banner>
    </template>
    <template #buttons>
      <muc-button
        icon="trash"
        @click="deleteChecklist"
      >
        Checkliste löschen
      </muc-button>
      <muc-button
        variant="secondary"
        @click="openAcceptDeleteDialog = false"
      >
        Abbrechen
      </muc-button>
    </template>
  </muc-modal>
</template>

<script setup lang="ts">
import type ChecklistServiceNavigator from "@/api/persservice/ChecklistServiceNavigator.ts";

import {
  MucBanner,
  MucButton,
  MucIntro,
  MucModal,
} from "@muenchen/muc-patternlab-vue";
import { computed, onMounted, ref } from "vue";

import ChecklistService from "@/api/persservice/ChecklistService.ts";
import MucChip from "@/components/common/MucChip.vue";
import {
  getChecklistIconBySituationId,
  getDateInGermanDateFormat,
} from "@/util/Constants.ts";

const openAcceptDeleteDialog = ref(false);

const props = defineProps<{
  checklist: ChecklistServiceNavigator;
  checklistOverviewUrl: string;
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

function deleteChecklist() {
  const service = new ChecklistService();
  service.deleteChecklist(props.checklist.id).then((resp) => {
    if (resp.ok) {
      location.href = props.checklistOverviewUrl;
    } else {
      resp.text().then((errBody) => {
        throw Error(errBody);
      });
    }
  });
}
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
