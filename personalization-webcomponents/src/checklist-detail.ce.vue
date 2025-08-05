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

      <h2 class="h2">Offene Aufgaben ({{todoCount}})</h2>
      <checklist-list
          v-model="openCheckList" @checked="onChecked" @label-click="onLabelClick"
      ></checklist-list>

      <h2 class="h2">Erledigte Aufgaben ({{doneCount}})</h2>
      <checklist-list
          v-model="closedCheckList" @label-click="onLabelClick"
          :is-draggable="false" :disabled="true"
      ></checklist-list>
    </div>
  </div>

</template>

<script setup lang="ts">
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import {computed, onMounted, ref} from "vue";
import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import ChecklistHeader from "@/components/ChecklistHeader.vue";
import SkeletonLoader from "@/components/common/skeleton-loader.vue";
import { QUERY_PARAM_CHECKLIST_ID } from "@/util/constants.ts";
import ChecklistList from "@/components/ChecklistList.vue";
import type DummyChecklistItem from "@/api/dummyservice/DummyChecklistItem.ts";

const checklist = ref<DummyChecklist>();
const loading = ref(true);
const openCheckList = ref<DummyChecklistItem[]>([]);
const closedCheckList = ref<DummyChecklistItem[]>([]);

const todoCount = computed(() => {
  return openCheckList.value.filter((value) => !value.checked).length;
});

const doneCount = computed(() => {
  return closedCheckList.value.filter((value) => value.checked).length;
});

function onChecked(serviceID: string) {
  const idx = openCheckList.value.findIndex(i => i.serviceID === serviceID);
  if (idx === -1) return;

  const [item] = openCheckList.value.splice(idx, 1);
  item.checked = new Date();

  openCheckList.value = [...openCheckList.value];
  closedCheckList.value = [...closedCheckList.value, item];
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
        openCheckList.value = foundChecklist.items.filter(item => item.checked === null);
        closedCheckList.value = foundChecklist.items.filter(item => item.checked !== null);
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

.h2 {
  display: flex;
  justify-content: center; /* Horizontal zentrieren */
  align-items: center;
}
</style>
