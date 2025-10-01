<template>
  <muc-card
    :title="checklist.title"
    tagline="Checkliste"
    @click="gotoChecklist(checklist.id!)"
  >
    <template #headerPrefix>
      <div class="card-header-icon">
        <img
          width="56px"
          height="56px"
          :src="getChecklistIconBySituationId(checklist.situationId)"
          alt="checklist-icon"
        />
      </div>
    </template>
    <template #content>
      <div class="chip-group">
        <muc-chip
          v-if="todoCount"
          background-color="var(--mde-color-status-warning-light)"
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
          background-color="var(--mde-color-status-success-light)"
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

      <div style="padding-top: 16px; padding-bottom: 20px">
        <checklistitem-listitem
          v-for="(item, index) in firstThreeItemsSortedByChecked"
          :checklist-item="item"
          :key="item.serviceID"
          :class="{
            'pt-8': index != 0,
            'pb-8': index != firstThreeItemsSortedByChecked.length - 1,
          }"
        >
        </checklistitem-listitem>
      </div>

      <div v-if="checklist.lastUpdate">
        <b>Letzte Änderung:</b>
        {{ new Date(checklist.lastUpdate).toLocaleDateString() }}
      </div>
    </template>
  </muc-card>
</template>

<script setup lang="ts">
import type Checklist from "@/api/persservice/Checklist.ts";

import { MucCard } from "@muenchen/muc-patternlab-vue";
import { computed } from "vue";

import ChecklistitemListitem from "@/components/ChecklistitemListitem.vue";
import MucChip from "@/components/common/MucChip.vue";
import {
  getChecklistIconBySituationId,
  QUERY_PARAM_CHECKLIST_ID,
} from "@/util/Constants.ts";

const props = defineProps<{
  checklist: Checklist;
  checklistDetailUrl: string;
}>();

const todoCount = computed(() => {
  if (props.checklist && props.checklist.checklistItems) {
    return props.checklist.checklistItems.filter((value) => !value.checked)
      .length;
  } else {
    return undefined;
  }
});

const doneCount = computed(() => {
  if (props.checklist && props.checklist.checklistItems) {
    return props.checklist.checklistItems.filter((value) => value.checked)
      .length;
  } else {
    return undefined;
  }
});

const firstThreeItemsSortedByChecked = computed(() => {
  const sortedItems = [...props.checklist.checklistItems].sort((a, b) =>
    a.checked === b.checked ? 0 : a.checked ? 1 : -1
  );
  return sortedItems.slice(0, 3);
});

function gotoChecklist(checklistId: string) {
  location.href = `${props.checklistDetailUrl}?${QUERY_PARAM_CHECKLIST_ID}=${checklistId}`;
}
</script>

<style scoped>
.card-header-icon {
  margin-right: 16px;
}

.pt-8 {
  padding-top: 8px;
}

.pb-8 {
  padding-bottom: 8px;
}

.chip-group {
  display: flex;
  flex-wrap: wrap;
  column-gap: 8px;
  row-gap: 8px
}
</style>
