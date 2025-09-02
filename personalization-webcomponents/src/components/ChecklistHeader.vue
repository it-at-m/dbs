<template>
  <muc-intro
    tagline="Checkliste"
    :title="checklist.title"
    :img="getChecklistIconByTitle(checklist.title)"
    imgAlt=""
  >
    <p style="padding-bottom: 16px">
      <b>Erstellungsdatum: </b
      >{{ props.checklist.lastUpdated.toLocaleString().split(",")[0] }}
    </p>
    <div class="taskcontainer">
      <div class="task">Aufgaben: </div>
      <div class="chip"><muc-chip
        v-if="todoCount"
        style="margin-right: 8px"
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
      </div>

      <div class="chip">
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
    </div>
  </muc-intro>
</template>

<script setup lang="ts">
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";

import { MucIntro } from "@muenchen/muc-patternlab-vue";
import { computed, onMounted } from "vue";

import MucChip from "@/components/common/muc-chip.vue";
import { getChecklistIconByTitle } from "@/util/constants.ts";

const props = defineProps<{
  checklist: DummyChecklist;
}>();

onMounted(() => {
  const element = document.querySelector(
    '[data-fragment-placeholder="breadcrumb-label"]'
  );
  if (element) {
    element.innerHTML = props.checklist.title;
  }
});

const todoCount = computed(() => {
  return props.checklist.items.filter((value) => !value.checked).length;
});

const doneCount = computed(() => {
  return props.checklist.items.filter((value) => value.checked).length;
});
</script>
<style>
.muc-divider {
  margin-top: 0 !important;
  margin-bottom: 32px !important;
}

.m-intro-vertical__title {
  margin-bottom: 8px !important;
}

.taskcontainer {
  display: grid;
  grid-template-columns: 90px 100px 100px;
  gap: 8px;
}

.task {
  padding-top: 3px;
  text-align: left;
  font-weight: bold;
}

@media (max-width: 450px) {
  .chip:nth-child(3) {
    grid-column: 2;
  }
}
</style>
