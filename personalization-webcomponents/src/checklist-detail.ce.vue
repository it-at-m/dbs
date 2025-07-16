<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

      <div v-if="loading">
        <skeleton-loader/>
      </div>
      <div v-else>
        <checklist-header v-if="checklist"
            :checklist="checklist"
        >
        </checklist-header>
      </div>
  </div>
</template>

<script setup lang="ts">
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import {onMounted, ref} from "vue";


import ChecklistHeader from "@/components/ChecklistHeader.vue";
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import SkeletonLoader from "@/components/common/skeleton-loader.vue";
import {QUERY_PARAM_CHECKLIST_ID} from "@/util/constants.ts";

const checklist = ref<DummyChecklist >();
const loading = ref(true);


onMounted(() => {
  loading.value = true;
  const dcl = new DummyChecklistService();
  dcl.getChecklists()
      .then((checklists) => {
        const urlParams = new URLSearchParams(window.location.search)
        const checklistId = urlParams.get(QUERY_PARAM_CHECKLIST_ID)
        const foundChecklist = checklists.find(checklist => checklist.id === checklistId)

        if (foundChecklist) {
          checklist.value = foundChecklist;
        } else {
          throw new Error("Checkliste wurde nicht gefunden")
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
