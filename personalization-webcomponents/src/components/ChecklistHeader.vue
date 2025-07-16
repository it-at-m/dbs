<template>
  <muc-intro tagline="Checkliste" :title="checklist.title" :img="getChecklistIconByTitle(checklist.title)" :imgAlt="checklist.title">
    <p style="padding-bottom: 18px">
      <b>Erstellungsdatum: </b>{{ props.checklist.lastUpdated.toLocaleString() }}
    </p>
    <p>
      <b>Aufgaben: </b>
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
          <use href="#icon-pencil"/>
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
    </p>
  </muc-intro>
</template>

<script setup lang="ts">

import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import {computed} from "vue";
import MucChip from "@/components/common/muc-chip.vue";
import {getChecklistIconByTitle} from "@/util/constants.ts";
import {MucIntro} from "@muenchen/muc-patternlab-vue";


const props = defineProps<{
  checklist: DummyChecklist
}>();

const todoCount = computed(() => {
  return props.checklist.items.filter(value => !value.checked).length;
});

const doneCount = computed(() => {
  return props.checklist.items.filter((value) => value.checked).length;
});

</script>