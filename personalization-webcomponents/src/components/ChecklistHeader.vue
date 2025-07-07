<template>
  <div
      class="m-intro m-intro-static-image"
      style="background-color: var(--color-neutrals-blue-xlight)"
  >
    <div class="container">
      <div style="display: grid; grid-template-columns: 75px auto; column-gap: 20px">
        <img class="item1"
             width="64px"
             height="64px"
             :src="getChecklistIconByTitle(checklist.title)"
             alt="checklist-icon"
        />

        <p class="m-intro-vertical__tagline">
          Checkliste
        </p>
        <h1>
          {{ props.checklist.title }}
        </h1>

        <muc-divider class="item2" />

        <div class="item3">
          <b>Erstellungsdatum: </b>{{ props.checklist.lastUpdated.toLocaleString() }}
        </div>

        <div class="item4">
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
        </div>

      </div>
    </div>
  </div>

</template>
<style>

.item1 {
  grid-row: 1 / span 2;
  margin: 10px;
}

.item2 {
  grid-row: 3 / span 2;
  grid-column: 2/ span 1;
  margin-top: 10px;
}

.item3 {
  grid-row: 4 / span 2;
  grid-column: 2 / span 1;
  margin-top: 25px;
  margin-bottom: 25px;

}

.item4 {
  grid-row: 5 / span 2;
  grid-column: 2 / span 1;
  margin-top: 25px;
  margin-bottom: 25px;

}
</style>

<script setup lang="ts">

import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import {computed} from "vue";
import MucChip from "@/components/common/muc-chip.vue";
import {getChecklistIconByTitle} from "@/util/constants.ts";
import {MucDivider} from "@muenchen/muc-patternlab-vue";


const props = defineProps<{
  checklist: DummyChecklist
}>();

const todoCount = computed(() => {
  return props.checklist.items.filter(value => !value.checked).length;
})

</script>