<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

    <div v-if="loading">
      <skeleton-loader />
    </div>
    <div v-else>
      <checklist-header
        v-if="checklist"
        :checklist="checklist"
      >
      </checklist-header>
    </div>
    <checklist-list
        v-model="list" @checked="onChecked" @label-click="onLabelClick"
    ></checklist-list>
  </div>
</template>

<script setup lang="ts">
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";

import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";

import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import ChecklistHeader from "@/components/ChecklistHeader.vue";
import SkeletonLoader from "@/components/common/skeleton-loader.vue";
import { QUERY_PARAM_CHECKLIST_ID } from "@/util/constants.ts";
import ChecklistList from "@/components/ChecklistList.vue";
import type DummyChecklistItem from "@/api/dummyservice/DummyChecklistItem.ts";


const checklist = ref<DummyChecklist>();
const loading = ref(true);
const items = ref<DummyChecklistItem[]>([]);


const list = items;

function onChecked() {
  console.log('List1 checked', items);
}

function onLabelClick(item: DummyChecklistItem) {
  console.log('List label clicked', item);
}

onMounted(() => {
  loading.value = true;
  const dcl = new DummyChecklistService();
  dcl
    .getChecklists()
    .then((checklists) => {
      const urlParams = new URLSearchParams(window.location.search);
      const checklistId = urlParams.get(QUERY_PARAM_CHECKLIST_ID);
      const foundChecklist = checklists.find(
        (checklist) => checklist.id === checklistId
      );

      if (foundChecklist) {
        checklist.value = foundChecklist;
        items.value = checklist.value.items;
      } else {
        throw new Error("Checkliste wurde nicht gefunden");
      }
    })
    .finally(() => (loading.value = false));
});
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>
