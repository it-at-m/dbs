<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />
    <div v-html="customIconsSprite" />

      <div v-if="loading">
        <skeleton-loader
            v-for="elem in [1, 2, 3, 4]"
            :key="elem"
        >
        </skeleton-loader>
      </div>
      <div v-else>
        <checklist-header
            v-for="(checklist, index) in checklists"
            :key="index"
            :checklist="checklist"
        >
        </checklist-header>
      </div>
  </div>

</template>

<script setup lang="ts">
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref} from "vue";


import ChecklistHeader from "@/components/ChecklistHeader.vue";
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";
import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import SkeletonLoader from "@/components/common/skeleton-loader.vue";


const checklists = ref<DummyChecklist[]>([]);
const loading = ref(true);

onMounted(() => {
  loading.value = true;
  const dcl = new DummyChecklistService();
  dcl
      .getChecklists()
      .then((checklist) => {
        checklists.value = checklist;
      })
      .finally(() => (loading.value = false));
});

</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>
