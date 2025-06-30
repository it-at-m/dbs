<template>
  <muc-card
    :title="checklist.title"
    tagline="Checkliste"
  >
    <template #headerPrefix>
      <div class="card-header-icon">
        <img
          width="56px"
          height="56px"
          :src="getChecklistIconByTitle(checklist.title)"
          alt="checklist-icon"
        />
      </div>
    </template>
    <template #content>
      <div>
        <muc-chip
          v-if="todoCount"
          style="margin-right: 16px"
          background-color="#FDD1AC"
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
          background-color="#B7D2B7"
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
          :key="index"
          :class="{
            'pt-8': index != 0,
            'pb-8': index != firstThreeItemsSortedByChecked.length - 1,
          }"
        >
        </checklistitem-listitem>
      </div>

      <div>
        <b>Letzte Änderung:</b> {{ checklist.lastUpdated.toLocaleString() }}
      </div>
    </template>
  </muc-card>
</template>

<script setup lang="ts">
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";

import { MucCard } from "@muenchen/muc-patternlab-vue";
import { computed } from "vue";

import ChecklistitemListitem from "@/components/checklistitem-listitem.vue";
import MucChip from "@/components/common/muc-chip.vue";
import { getChecklistIconByTitle } from "@/util/constants.ts";

const props = defineProps<{
  checklist: DummyChecklist;
}>();

const todoCount = computed(() => {
  return props.checklist.items.filter((value) => !value.checked).length;
});

const doneCount = computed(() => {
  return props.checklist.items.filter((value) => value.checked).length;
});

const firstThreeItemsSortedByChecked = computed(() => {
  const sortedItems = [...props.checklist.items].sort((a, b) =>
    a.checked === b.checked ? 0 : a.checked ? 1 : -1
  );
  return sortedItems.slice(0, 3);
});
</script>

<style scoped>
.card-header-icon {
  margin-right: 16px;
  border: 1px solid var(--color-neutrals-blue);
  border-radius: 56px;
}

.pt-8 {
  padding-top: 8px;
}

.pb-8 {
  padding-bottom: 8px;
}
</style>
