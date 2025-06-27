<template>
  <muc-card
      :title="checklist.title"
      tagline="Checkliste"
  >
    <template #headerPrefix>
      <div
          style="padding-right: 16px; font-size: 32px;"
      >
        <img src="https://assets.muenchen.de/mde/1.0.10/assets/svg/pictograms/pictogram-cash.svg" alt="cash-icon"></img>
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
              style="margin-left: 8px; width: 20px; height: 20px;"
              aria-hidden="true"
              class="m-button__icon"
          >
            <use href="#icon-pencil"/>
          </svg>
        </muc-chip>
        <muc-chip
            v-if="doneCount"
            background-color="#B7D2B7"
        >
          {{ doneCount }} erledigt
          <svg
              style="margin-left: 8px; width: 20px; height: 20px;"
              aria-hidden="true"
              class="m-button__icon"
          >
            <use href="#icon-check"/>
          </svg>
        </muc-chip>
      </div>

      <div
        style="padding-top: 16px; padding-bottom: 20px;"
      >
        <checklistitem-listitem
            v-for="(item, index) in firstThreeItemsSortedByChecked"
            :checklist-item="item"
            :class="{
              'pt-8': index != 0,
              'pb-8': index != firstThreeItemsSortedByChecked.length - 1
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
import {MucCard} from "@muenchen/muc-patternlab-vue";
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import MucChip from "@/components/common/muc-chip.vue";
import {computed} from "vue";
import ChecklistitemListitem from "@/components/checklistitem-listitem.vue";

const props = defineProps<{
  checklist: DummyChecklist;
}>();

const todoCount = computed(() => {
  return props.checklist.items.filter(value => !value.checked).length;
})

const doneCount = computed(() => {
  return props.checklist.items.filter(value => value.checked).length;
})

const firstThreeItemsSortedByChecked = computed(() => {
  const sortedItems = [...props.checklist.items].sort((a, b) => (a.checked === b.checked) ? 0 : a.checked ? 1 : -1);
  return sortedItems.slice(0, 3);
});

</script>

<style>
.pt-8 {
  padding-top: 8px;
}
.pb-8 {
  padding-bottom: 8px;
}
</style>