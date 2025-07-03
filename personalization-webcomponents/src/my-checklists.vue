<template>
  <main class="container">
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite"/>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite"/>
    <h2 style="display: flex; align-items: center; margin-bottom: 24px">
      <muc-icon
          style="width: 32px; height: 32px; margin-right: 8px"
          icon="order-bool-ascending"
      ></muc-icon>
      Aktive Checklisten ({{ checklists.length }})
    </h2>
    <muc-card-container
        v-if="loading"
        class="checklist-card-container"
    >
      <skeleton-loader
          v-for="elem in [1, 2, 3, 4]"
          :key="elem"
      >
      </skeleton-loader>
    </muc-card-container>
    <muc-card-container
        v-else
        class="checklist-card-container"
    >
      <checklist-card
          v-for="(checklist, index) in checklists"
          :key="index"
          :checklist="checklist"
          :checklist-detail-url="checklistDetailUrl"
      >
      </checklist-card>
    </muc-card-container>
  </main>
</template>

<script setup lang="ts">
import type DummyChecklist from "@/api/dummyservice/DummyChecklist.ts";

import {MucCardContainer, MucIcon} from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import {onMounted, ref} from "vue";

import DummyChecklistService from "@/api/dummyservice/DummyChecklistService.ts";
import ChecklistCard from "@/components/ChecklistCard.vue";
import SkeletonLoader from "@/components/common/skeleton-loader.vue";

defineProps<{
  checklistDetailUrl: string;
  newChecklistUrl: string;
}>();

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

<style scoped>
.checklist-card-container {
  grid-template-columns: repeat(auto-fit, 100%);
}

@media (min-width: 768px) {
  .checklist-card-container {
    grid-template-columns: repeat(auto-fit, 589px);
  }
}
</style>
